//package ppl.common.utils.command;
//
//import ppl.common.utils.StringUtils;
//import ppl.common.utils.command.collector.ListCollector;
//
//import java.util.*;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//@SuppressWarnings("unused")
//public class Command {
//
//    private static final String END_OPTION_FLAG = "--";
//
//    @SuppressWarnings("rawtypes")
//    private static final Set<Class<? extends Argument>> SUPPORTED_ARGUMENT_TYPE;
//
//    static {
//        @SuppressWarnings("rawtypes")
//        Set<Class<? extends Argument>> supported = new HashSet<>();
//        supported.add(Position.class);
//        supported.add(Option.class);
//        SUPPORTED_ARGUMENT_TYPE = Collections.unmodifiableSet(supported);
//    }
//
//    private final Map<String, Argument<?>> allArguments;
//    private final Map<String, Option<?>> longOptions;
//    private final Map<Character, Option<?>> shortOptions;
//    private final List<Position<?>> positions;
//    private final List<String> remainArgs;
//
//    private Command(Map<String, Argument<?>> allArguments,
//                    Map<String, Option<?>> longOptions,
//                    Map<Character, Option<?>> shortOptions,
//                    List<Position<?>> positions) {
//        this.allArguments = Collections.unmodifiableMap(allArguments);
//        this.longOptions = Collections.unmodifiableMap(longOptions);
//        this.shortOptions = Collections.unmodifiableMap(shortOptions);
//        this.positions = Collections.unmodifiableList(positions);
//        this.remainArgs = new ArrayList<>();
//    }
//
//    public void init(String[] args) throws CommandLineException {
////        Parser parser = new Parser(args);
////        parser.parse();
////        for (Argument<?> argument : allArguments) {
////            if (argument.isRequired() && !parsedArgs.containsKey(argument.getName())) {
////                throw new CommandLineException(StringUtils.format(
////                        "Argument '{}' is not specified in command line.", argument.getName()));
////            }
////        }
////        for (Argument<?> argument : allArguments) {
////            if (!parsedArgs.containsKey(argument.getName())) {
////                parsedArgs.put(argument.getName(), argument.getDefaultValue());
////            }
////        }
//        List<List<Integer>> option = Option.newBuilder("test", 't')
//                .map(Integer::parseInt)
//                .collect(new ListCollector<>());
//    }
//
//    private class Parser {
//        private final String[] args;
//        private int i;
//
//        public Parser(String[] args) {
//            this.args = args;
//            this.i = 0;
//        }
//
//        public void parse() {
//            boolean isEnd = false;
//            for (; i < args.length; i++) {
//                String arg = args[i];
//                if (isEnd) {
//                    remainArgs.add(arg);
//                } else if (isShortOption(arg)) {
//                    parseShortOption(arg.charAt(1), arg);
//                } else if (isLongOption(arg)) {
//                    parseLongOption(arg.substring(2));
//                } else if (isEndOptionFlag(arg)) {
//                    isEnd = true;
//                } else {
//                    remainArgs.add(arg);
//                }
//            }
//        }
//
//        private void parseShortOption(char shortOption, String arg) {
//            Option<?> option = resolverOfShortOption(shortOption, arg);
//
//            if (option.isToggle()) {
//                parseToggleShortOptions(arg);
//            } else {
//                parseNonToggleShortOption(option, shortOption, arg);
//            }
//        }
//
//        private void parseToggleShortOptions(String arg) {
//            for (int j = 1; j < arg.length(); j++) {
//                parseToggleShortOption(arg.charAt(j), arg);
//            }
//        }
//
//        private void parseToggleShortOption(char shortOption, String arg) {
////            Option<?> option = resolverOfShortOption(shortOption, arg);
////            if (!option.isToggle()) {
////                throw new CommandLineException(StringUtils.format(
////                        "Non-toggle short option '{}' must not be in '{}'",
////                        shortOption, arg));
////            }
////            parsedArgs.put(option.getName(), Optional.of(true));
//        }
//
//        private Option<?> resolverOfShortOption(char shortOption, String arg) {
//            Option<?> option = shortOptions.get(shortOption);
//            if (option == null) {
//                throw new CommandLineException(StringUtils.format(
//                        "Unknown short option '{}' in '{}'.", shortOption, arg));
//            }
//            return option;
//        }
//
//        private void parseNonToggleShortOption(Option<?> option, char shortOption, String arg) {
//            if (arg.length() == 2) {
//                resolveNextValue(option, Character.toString(shortOption), false);
//            } else {
//                resolveValueInShortOption(option, shortOption, arg);
//            }
//        }
//
//        private void resolveValueInShortOption(Option<?> option, char shortOption, String arg) {
////            try {
////                parsedArgs.put(option.getName(),
////                        option.resolve(arg.substring(2)));
////            } catch (UnsupportedOperationException e) {
////                throw e;
////            } catch (Throwable t) {
////                throw new CommandLineException(StringUtils.format(
////                        "Invalid value for short option '{}' in '{}'.",
////                        shortOption, arg), t);
////            }
//        }
//
//        private void parseLongOption(String name) {
////            Option<?> option = resolverOfLongOption(name);
////            if (option.isToggle()) {
////                parsedArgs.put(option.getName(), Optional.of(true));
////            } else {
////                resolveNextValue(option, name, true);
////            }
//        }
//
//        private Option<?> resolverOfLongOption(String longOption) {
//            Option<?> option = longOptions.get(longOption);
//            if (option == null) {
//                throw new CommandLineException(StringUtils.format(
//                        "Unknown long option '{}'.", longOption));
//            }
//            return option;
//        }
//
//        private void resolveNextValue(Option<?> option, String name, boolean longOption) {
////            try {
////                parsedArgs.put(option.getName(),
////                        option.resolve(nextValue(name)));
////            } catch (UnsupportedOperationException e) {
////                throw e;
////            } catch (Throwable t) {
////                throw new CommandLineException(
////                        StringUtils.format("Invalid value for {} option '{}'.",
////                                longOption ? "long" : "short", name), t);
////            }
//        }
//
//        private String nextValue(String name) {
//            String value = nextArg();
//            if (isOption(value)) {
//                throw new CommandLineException(StringUtils.format(
//                        "No value is specified for long option '{}'.", name));
//            }
//            return value;
//        }
//
//        private String nextArg() {
//            i++;
//            if (i >= args.length) {
//                throw new CommandLineException("Command argument is already used up.");
//            }
//            return args[i];
//        }
//
//        private boolean isShortOption(String arg) {
//            return arg.length() >= 2 &&
//                    arg.startsWith(SHORT_OPTION_PREFIX) &&
//                    !arg.startsWith(SHORT_OPTION_PREFIX, 1);
//        }
//
//        private boolean isLongOption(String arg) {
//            return arg.length() > 3 &&
//                    arg.startsWith(LONG_OPTION_PREFIX);
//        }
//
//        private boolean isEndOptionFlag(String arg) {
//            return arg.equals(END_OPTION_FLAG);
//        }
//
//        private boolean isOption(String arg) {
//            return isShortOption(arg) || isLongOption(arg) || isEndOptionFlag(arg);
//        }
//
//    }
//
//    public Object get(String argument, Object defaultValue) {
//        if (!this.allArguments.containsKey(argument)) {
//            throw new IllegalArgumentException("Unknown argument: " + argument);
//        }
//
//        @SuppressWarnings("unchecked")
//        Optional<Object> value = (Optional<Object>) this.allArguments.get(argument).resolve();
//        return value.orElse(defaultValue);
//    }
//
//    public Object get(String argument) {
//        return get(argument, (Object) null);
//    }
//
//    public <V> V get(String argument, V defaultValue, Class<V> clazz) {
//        Object res = get(argument, defaultValue);
//        if (res == null) {
//            return null;
//        }
//        return clazz.cast(res);
//    }
//
//    public <V> V get(String argument, Class<V> clazz) {
//        return get(argument, null, clazz);
//    }
//
//    public Object required(String argument) {
//        if (!this.allArguments.containsKey(argument)) {
//            throw new IllegalArgumentException("Unknown argument: " + argument);
//        }
//
//        @SuppressWarnings("unchecked")
//        Optional<Object> value = (Optional<Object>) this.allArguments.get(argument).resolve();
//        return value.orElseThrow(() -> new CommandLineException(StringUtils.format(
//                "No value of argument '{}' is specified.", argument)));
//    }
//
//    public <V> V required(String argument, Class<V> clazz) {
//        Object res = required(argument);
//        return clazz.cast(res);
//    }
//
//    public static class Builder {
//        private Set<Argument<?>> allArguments;
//        private Map<String, Option<?>> longOptions;
//        private Map<Character, Option<?>> shortOptions;
//        private List<Position<?>> positions;
//
//        private Builder() {}
//
//        public Command build() {
//            return new Command(allArguments.stream()
//                    .collect(Collectors.toMap(Argument::getName, Function.identity())),
//                    longOptions, shortOptions, positions);
//        }
//
//        public void addArguments(List<Argument<?>> arguments) {
//            for (Argument<?> argument : arguments) {
//                addArgument(argument);
//            }
//        }
//
//        public void addArgument(Argument<?> argument) {
//            _addArgument(argument);
//            if (argument instanceof Option) {
//                addLongOption((Option<?>) argument);
//                addShortOption((Option<?>) argument);
//            } else if (argument instanceof Position) {
//                addPosition((Position<?>) argument);
//            } else {
//                throw new IllegalArgumentException(StringUtils.format(
//                        "Unsupported argument type '{}' in command. Please use '{}'.",
//                        argument.getClass().getCanonicalName(), SUPPORTED_ARGUMENT_TYPE));
//            }
//        }
//
//        private void _addArgument(Argument<?> argument) {
//            if (allArguments == null) {
//                allArguments = new HashSet<>();
//            }
//
//            if (allArguments.contains(argument)) {
//                throw new IllegalArgumentException(StringUtils.format(
//                        "Argument {} is already exists.",
//                        argument.getName()));
//            }
//            allArguments.add(argument);
//        }
//
//        private void addLongOption(Option<?> option) {
//            if (longOptions == null) {
//                longOptions = new HashMap<>();
//            }
//
//            Map<String, Option<?>> lOptions = longOptions;
//            option.getLongOptions().forEach(name -> {
//                if (lOptions.containsKey(name)) {
//                    throw new IllegalArgumentException(StringUtils.format(
//                            "Long option '{}' in '{}' is already exists in '{}'.",
//                            name, option, lOptions.get(name)));
//                }
//                lOptions.put(name, option);
//            });
//        }
//
//        private void addShortOption(Option<?> option) {
//            if (shortOptions == null) {
//                shortOptions = new HashMap<>();
//            }
//
//            Map<Character, Option<?>> sOptions = shortOptions;
//            option.getShortOptions().forEach(name -> {
//                if (sOptions.containsKey(name)) {
//                    throw new IllegalArgumentException(StringUtils.format(
//                            "Short option '{}' in '{}' is already exists in '{}'.",
//                            name, option, sOptions.get(name)));
//                }
//                sOptions.put(name, option);
//            });
//        }
//
//        private void addPosition(Position<?> position) {
//            if (positions == null) {
//                positions = new ArrayList<>();
//            }
//
//            position.init(positions.size());
//            positions.add(position);
//        }
//
//    }
//}
