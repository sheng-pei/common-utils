package ppl.common.utils.command;

import ppl.common.utils.argument.*;
import ppl.common.utils.argument.value.ArgumentValue;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Command {

    static final String SEPARATOR = " ";

    private final CommandArguments arguments;
    private final CommandParser parser;
    private final Analyzer<String, Object> analyzer;

    @SuppressWarnings("rawtypes")
    private Map toggles;
    @SuppressWarnings("rawtypes")
    private Map values;
    private List<String> remains;

    public Command(CommandArguments arguments) {
        this.arguments = arguments;
        this.parser = new CommandParser(arguments);
        this.analyzer = new Analyzer<>(arguments);
    }

    public void init(String[] args) throws CommandLineException {
        Stream<Fragment<Object, String>> stream = parser.parse(args);
        List<Object> analyzedCommand = analyse(stream);
        this.values = analyzedCommand.stream()
                .filter(v -> v instanceof ArgumentValue)
                .map(v -> (ArgumentValue<?, ?>) v)
                .collect(Collectors.toMap(ArgumentValue::key, Function.identity()));
        this.toggles = analyzedCommand.stream()
                .filter(v -> v instanceof Argument)
                .map(v -> (Argument<?, ?>) v)
                .collect(Collectors.toMap(Argument::getName, Function.identity()));
        this.remains = analyzedCommand.stream()
                .filter(av -> av instanceof Fragment)
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    private List<Object> analyse(Stream<Fragment<Object, String>> stream) {
        try {
            return analyzer.analyse(stream);
        } catch (ArgumentException e) {
            throw new CommandLineException(e.getMessage(), e);
        }
    }

    public Object get(String argument, Object defaultValue) {
        Argument<String, ?> a = this.arguments.getByName(argument);
        if (a == null) {
            throw new IllegalArgumentException("Unknown argument: " + argument);
        }

        if (a instanceof ToggleOptionArgument) {
            if (defaultValue != null) {
                throw new IllegalArgumentException(
                        "Toggle option certainly has value, no default value is needed.");
            }
            defaultValue = false;
        }

        @SuppressWarnings("unchecked")
        ArgumentValue<String, Object> av = (ArgumentValue<String, Object>) this.values.get(argument);
        if (av != null) {
            return av.value();
        } else if (toggles.containsKey(argument)) {
            return true;
        }
        return defaultValue;
    }

    public Object get(String argument) {
        return get(argument, (Object) null);
    }

    public <V> V get(String argument, V defaultValue, Class<V> clazz) {
        Object res = get(argument, defaultValue);
        if (res == null) {
            return null;
        }
        return clazz.cast(res);
    }

    public <V> V get(String argument, Class<V> clazz) {
        return get(argument, null, clazz);
    }
}

