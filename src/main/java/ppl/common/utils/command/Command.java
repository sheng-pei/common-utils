package ppl.common.utils.command;

import ppl.common.utils.string.Strings;
import ppl.common.utils.command.argument.Argument;
import ppl.common.utils.command.argument.Option;
import ppl.common.utils.command.argument.Position;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ppl.common.utils.command.argument.Option.LONG_OPTION_PREFIX;
import static ppl.common.utils.command.argument.Option.SHORT_OPTION_PREFIX;

@SuppressWarnings("unused")
public class Command {

    private static final String END_OPTION_FLAG = "--";

    @SuppressWarnings("rawtypes")
    private static final Set<Class<? extends Argument>> SUPPORTED_ARGUMENT_TYPE;

    static {
        @SuppressWarnings("rawtypes")
        Set<Class<? extends Argument>> supported = new HashSet<>();
        supported.add(Position.class);
        supported.add(Option.class);
        SUPPORTED_ARGUMENT_TYPE = Collections.unmodifiableSet(supported);
    }

    private final Map<String, Argument<?>> allArguments;
    private final Map<String, Option<?>> longOptions;
    private final Map<Character, Option<?>> shortOptions;
    private final List<Position<?>> positions;
    private final List<String> remainArgs;

    private Command(Map<String, Argument<?>> allArguments,
                    Map<String, Option<?>> longOptions,
                    Map<Character, Option<?>> shortOptions,
                    List<Position<?>> positions) {
        this.allArguments = Collections.unmodifiableMap(allArguments);
        this.longOptions = Collections.unmodifiableMap(longOptions);
        this.shortOptions = Collections.unmodifiableMap(shortOptions);
        this.positions = Collections.unmodifiableList(positions);
        this.remainArgs = new ArrayList<>();
    }

    public void init(String[] args) throws CommandLineException {
        Parser parser = new Parser(args);
        parser.parse();
        for (Argument<?> argument : allArguments.values()) {
            if (argument instanceof Option) {
                Option<?> option = (Option<?>) argument;
                if (!option.received() && option.isToggle()) {
                    option.receive("false");
                }
                if (!option.received()) {
                    option.receive();
                }
                option.resolve();
            }
        }

        int i = 0;
        for (; i < positions.size() && i < remainArgs.size(); i++) {
            positions.get(i).receive(remainArgs.get(i));
        }

        if (i < positions.size()) {
            for (; i < positions.size(); i++) {
                positions.get(i).receive();
            }
        } else if (i < remainArgs.size()) {
            List<String> tmp = remainArgs.subList(i, remainArgs.size());
            remainArgs.clear();
            remainArgs.addAll(tmp);
        }
        positions.forEach(Position::resolve);
    }

    private class Parser {
        private final String[] args;
        private int i;

        public Parser(String[] args) {
            this.args = args;
            this.i = 0;
        }

        public void parse() {
            boolean isEnd = false;
            for (; i < args.length; i++) {
                String arg = args[i];
                if (isEnd) {
                    remainArgs.add(arg);
                } else if (isShortOption(arg)) {
                    parseShortOption(arg.charAt(1), arg);
                } else if (isLongOption(arg)) {
                    parseLongOption(arg.substring(2));
                } else if (isEndOptionFlag(arg)) {
                    isEnd = true;
                } else {
                    remainArgs.add(arg);
                }
            }
        }

        private void parseShortOption(char shortOption, String arg) {
            Option<?> option = resolverOfShortOption(shortOption, arg);

            if (option.isToggle()) {
                parseToggleShortOptions(arg);
            } else {
                parseNonToggleShortOption(option, shortOption, arg);
            }
        }

        private void parseToggleShortOptions(String arg) {
            for (int j = 1; j < arg.length(); j++) {
                parseToggleShortOption(arg.charAt(j), arg);
            }
        }

        private void parseToggleShortOption(char shortOption, String arg) {
            Option<?> option = resolverOfShortOption(shortOption, arg);
            if (!option.isToggle()) {
                throw new CommandLineException(Strings.format(
                        "Non-toggle short option '{}' must not be in '{}'",
                        shortOption, arg));
            }
            option.receive("true");
        }

        private Option<?> resolverOfShortOption(char shortOption, String arg) {
            Option<?> option = shortOptions.get(shortOption);
            if (option == null) {
                throw new CommandLineException(Strings.format(
                        "Unknown short option '{}' in '{}'.", shortOption, arg));
            }
            return option;
        }

        private void parseNonToggleShortOption(Option<?> option, char shortOption, String arg) {
            if (arg.length() == 2) {
                resolveValue(option);
            } else {
                resolveValueInShortOption(option, shortOption, arg);
            }
        }

        private void resolveValueInShortOption(Option<?> option, char shortOption, String arg) {
            option.receive(arg.substring(2));
        }

        private void parseLongOption(String name) {
            Option<?> option = resolverOfLongOption(name);
            if (option.isToggle()) {
                option.receive("true");
            } else {
                resolveValue(option);
            }
        }

        private Option<?> resolverOfLongOption(String longOption) {
            Option<?> option = longOptions.get(longOption);
            if (option == null) {
                throw new CommandLineException(Strings.format(
                        "Unknown long option '{}'.", longOption));
            }
            return option;
        }

        private void resolveValue(Option<?> option) {
            option.receive(value());
        }

        private String value() {
            if (i >= args.length) {
                return null;
            }
            String arg = args[i];
            if (isOption(arg)) {
                return null;
            }
            i++;
            return arg;
        }

        private boolean isShortOption(String arg) {
            return arg.length() >= 2 &&
                    arg.startsWith(SHORT_OPTION_PREFIX) &&
                    !arg.startsWith(LONG_OPTION_PREFIX);
        }

        private boolean isLongOption(String arg) {
            return arg.length() > 3 &&
                    arg.startsWith(LONG_OPTION_PREFIX) &&
                    !arg.startsWith(SHORT_OPTION_PREFIX);
        }

        private boolean isEndOptionFlag(String arg) {
            return arg.equals(END_OPTION_FLAG);
        }

        private boolean isOption(String arg) {
            return isShortOption(arg) || isLongOption(arg) || isEndOptionFlag(arg);
        }

    }

    public Object get(String argument, Object defaultValue) {
        if (!this.allArguments.containsKey(argument)) {
            throw new IllegalArgumentException("Unknown argument: " + argument);
        }

        @SuppressWarnings("unchecked")
        Optional<Object> value = (Optional<Object>) this.allArguments.get(argument).resolve();
        return value.orElse(defaultValue);
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

    public static class Builder {
        private Set<Argument<?>> allArguments;
        private Map<String, Option<?>> longOptions;
        private Map<Character, Option<?>> shortOptions;
        private List<Position<?>> positions;

        private Builder() {}

        public Command build() {
            return new Command(allArguments.stream()
                    .collect(Collectors.toMap(Argument::getName, Function.identity())),
                    longOptions, shortOptions, positions);
        }

        public void addArguments(List<Argument<?>> arguments) {
            for (Argument<?> argument : arguments) {
                addArgument(argument);
            }
        }

        public void addArgument(Argument<?> argument) {
            _addArgument(argument);
            if (argument instanceof Option) {
                addLongOption((Option<?>) argument);
                addShortOption((Option<?>) argument);
            } else if (argument instanceof Position) {
                addPosition((Position<?>) argument);
            } else {
                throw new IllegalArgumentException(Strings.format(
                        "Unsupported argument type '{}' in command. Please use '{}'.",
                        argument.getClass().getCanonicalName(), SUPPORTED_ARGUMENT_TYPE));
            }
        }

        private void _addArgument(Argument<?> argument) {
            if (allArguments == null) {
                allArguments = new HashSet<>();
            }

            if (allArguments.contains(argument)) {
                throw new IllegalArgumentException(Strings.format(
                        "Argument {} is already exists.",
                        argument.getName()));
            }
            allArguments.add(argument);
        }

        private void addLongOption(Option<?> option) {
            if (longOptions == null) {
                longOptions = new HashMap<>();
            }

            Map<String, Option<?>> lOptions = longOptions;
            option.getLongOptions().forEach(name -> {
                if (lOptions.containsKey(name)) {
                    throw new IllegalArgumentException(Strings.format(
                            "Long option '{}' in '{}' is already exists in '{}'.",
                            name, option, lOptions.get(name)));
                }
                lOptions.put(name, option);
            });
        }

        private void addShortOption(Option<?> option) {
            if (shortOptions == null) {
                shortOptions = new HashMap<>();
            }

            Map<Character, Option<?>> sOptions = shortOptions;
            option.getShortOptions().forEach(name -> {
                if (sOptions.containsKey(name)) {
                    throw new IllegalArgumentException(Strings.format(
                            "Short option '{}' in '{}' is already exists in '{}'.",
                            name, option, sOptions.get(name)));
                }
                sOptions.put(name, option);
            });
        }

        private void addPosition(Position<?> position) {
            if (positions == null) {
                positions = new ArrayList<>();
            }

            position.init(positions.size());
            positions.add(position);
        }

    }
}
