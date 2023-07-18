package ppl.common.utils.command;

import ppl.common.utils.argument.*;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Command {

    static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z][a-zA-Z0-9]*");
    static final String SEPARATOR = " ";

    static boolean isName(String string) {
        return NAME_PATTERN.matcher(string).matches();
    }

    private final CommandArguments arguments;
    private final CommandParser parser;
    private final Analyzer<String, Object> analyzer;
    private Map<String, ArgumentValue<String, Object>> values;
    private List<String> remains;

    public Command(CommandArguments arguments) {
        this.arguments = arguments;
        this.parser = new CommandParser(arguments);
        this.analyzer = new Analyzer<>(arguments);
    }

    public void init(String[] args) throws CommandLineException {
        Stream<Fragment<Object, String>> stream = parser.parse(args);
        List<ArgumentValue<String, Object>> values = analyzer.analyse(stream);
        this.values = values.stream()
                .filter(ArgumentValue::isKnown)
                .collect(Collectors.toMap(ArgumentValue::key, Function.identity()));
        this.remains = values.stream()
                .filter(av -> !av.isKnown())
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    public Object get(String argument, Object defaultValue) {
        AbstractArgument<String, Object> a = this.arguments.getByName(argument);
        if (a == null) {
            throw new IllegalArgumentException("Unknown argument: " + argument);
        }

        ArgumentValue<String, Object> av = this.values.get(argument);
        if (a instanceof Option) {
            Option<?> option = (Option<?>) a;
            if (option.isToggle()) {
                if (defaultValue != null) {
                    throw new IllegalArgumentException(
                            "Toggle option certainly has value, no default value is needed.");
                }
                return av != null;
            }
        }
        return av == null || av.value() == null ? defaultValue : av.value();
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

