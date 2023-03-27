package ppl.common.utils.command;

import ppl.common.utils.StringUtils;

import java.util.*;

@SuppressWarnings("unused")
public class Command {

    private static final String END_OPTION_FLAG = "--";
    private static final String LONG_OPTION_PREFIX = "--";
    private static final String SHORT_OPTION_PREFIX = "-";

    @SuppressWarnings("rawtypes")
    private static final Set<Class<? extends Argument>> SUPPORTED_ARGUMENT_TYPE;

    static {
        @SuppressWarnings("rawtypes")
        Set<Class<? extends Argument>> supported = new HashSet<>();
        supported.add(Position.class);
        supported.add(Option.class);
        SUPPORTED_ARGUMENT_TYPE = Collections.unmodifiableSet(supported);
    }

    private final Map<String, Command> commands;  //DAG

    private final Set<Argument<?>> allArguments;
    private final Map<String, Option<?>> longOptions;
    private final Map<Character, Option<?>> shortOptions;
    private final Map<Integer, Position<?>> positions;

    private final Map<String, Optional<?>> parsedArgs;
    private final List<String> remainArgs;

    public Command() {
        this(Collections.emptyList());
    }

    public Command(List<Argument<?>> arguments) {
        this.commands = new HashMap<>();
        this.allArguments = new HashSet<>();

        this.positions = new HashMap<>();
        this.longOptions = new HashMap<>();
        this.shortOptions = new HashMap<>();
        addArguments(arguments);

        this.remainArgs = new ArrayList<>();
        this.parsedArgs = new HashMap<>();
    }

    public void addArguments(List<Argument<?>> arguments) {
        for (Argument<?> argument : arguments) {
            addArgument(argument);
        }
    }

    public void addArgument(Argument<?> argument) {
        _addArgument(argument);
        if (argument instanceof Option) {
            addLongOption(longOptions, (Option<?>) argument);
            addShortOption(shortOptions, (Option<?>) argument);
        } else if (argument instanceof Position) {
            addPosition(positions, (Position<?>) argument);
        } else {
            throw new IllegalArgumentException(StringUtils.format(
                    "Cannot put argument of type '{}' into command. Please use '{}'.",
                    argument.getClass().getCanonicalName(), SUPPORTED_ARGUMENT_TYPE));
        }
    }

    private void _addArgument(Argument<?> argument) {
        if (allArguments.contains(argument)) {
            throw new IllegalArgumentException(StringUtils.format(
                    "Argument {} is already exists.",
                    argument.getName()));
        }
        allArguments.add(argument);
    }

    private static void addLongOption(Map<String, Option<?>> lOptions, Option<?> option) {
        option.getLongOptions().forEach(name -> {
            if (lOptions.containsKey(name)) {
                throw new IllegalArgumentException(StringUtils.format(
                        "Long option '{}' in '{}' is already exists in '{}'.",
                        name, option, lOptions.get(name)));
            }
            lOptions.put(name, option);
        });
    }

    private static void addShortOption(Map<Character, Option<?>> sOptions, Option<?> option) {
        option.getShortOptions().forEach(name -> {
            if (sOptions.containsKey(name)) {
                throw new IllegalArgumentException(StringUtils.format(
                        "Short option '{}' in '{}' is already exists in '{}'.",
                        name, option, sOptions.get(name)));
            }
            sOptions.put(name, option);
        });
    }

    private static void addPosition(Map<Integer, Position<?>> positions, Position<?> position) {
        int pos = position.getPosition();
        if (positions.containsKey(pos)) {
            throw new IllegalArgumentException(StringUtils.format(
                    "Position '{}' in '{}' is already exists in '{}'.",
                    pos, position, positions.get(pos)));
        }
        positions.put(pos, position);
    }

    public void init(String[] args) throws CommandLineException {
        checkPositions();
        ensureRequiredPositionConsecutive();

        Parser parser = new Parser(args);
        parser.parse();
        for (Argument<?> argument : allArguments) {
            if (argument.isRequired() && !parsedArgs.containsKey(argument.getName())) {
                throw new CommandLineException(StringUtils.format(
                        "Argument '{}' is not specified in command line.", argument.getName()));
            }
        }
        for (Argument<?> argument : allArguments) {
            if (!parsedArgs.containsKey(argument.getName())) {
                parsedArgs.put(argument.getName(), argument.getDefaultValue());
            }
        }
    }

    private void checkPositions() {
        Integer maxPosition = positions.keySet().stream()
                .max(Integer::compareTo)
                .orElse(0);
        if (maxPosition != positions.size()) {
            throw new IllegalStateException("Position arguments must be consecutive.");
        }
    }

    private void ensureRequiredPositionConsecutive() {
        int optional = 1;
        for (int i = 1; i <= positions.size(); i++) {
            Position<?> position = positions.get(i);
            if (position.isRequired()) {
                if (i > optional) {
                    for (int j = optional; j < i; j++) {
                        positions.put(j, positions.get(j).with().withRequired(true).build());
                    }
                }
                optional = i + 1;
            }
        }
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
                throw new CommandLineException(StringUtils.format(
                        "Non-toggle short option '{}' must not be in '{}'",
                        shortOption, arg));
            }
            parsedArgs.put(option.getName(), Optional.of(true));
        }

        private Option<?> resolverOfShortOption(char shortOption, String arg) {
            Option<?> option = shortOptions.get(shortOption);
            if (option == null) {
                throw new CommandLineException(StringUtils.format(
                        "Unknown short option '{}' in '{}'.", shortOption, arg));
            }
            return option;
        }

        private void parseNonToggleShortOption(Option<?> option, char shortOption, String arg) {
            if (arg.length() == 2) {
                resolveNextValue(option, Character.toString(shortOption), false);
            } else {
                resolveValueInShortOption(option, shortOption, arg);
            }
        }

        private void resolveValueInShortOption(Option<?> option, char shortOption, String arg) {
            try {
                parsedArgs.put(option.getName(),
                        option.resolve(arg.substring(2)));
            } catch (UnsupportedOperationException e) {
                throw e;
            } catch (Throwable t) {
                throw new CommandLineException(StringUtils.format(
                        "Invalid value for short option '{}' in '{}'.",
                        shortOption, arg), t);
            }
        }

        private void parseLongOption(String name) {
            Option<?> option = resolverOfLongOption(name);
            if (option.isToggle()) {
                parsedArgs.put(option.getName(), Optional.of(true));
            } else {
                resolveNextValue(option, name, true);
            }
        }

        private Option<?> resolverOfLongOption(String longOption) {
            Option<?> option = longOptions.get(longOption);
            if (option == null) {
                throw new CommandLineException(StringUtils.format(
                        "Unknown long option '{}'.", longOption));
            }
            return option;
        }

        private void resolveNextValue(Option<?> option, String name, boolean longOption) {
            try {
                parsedArgs.put(option.getName(),
                        option.resolve(nextValue(name)));
            } catch (UnsupportedOperationException e) {
                throw e;
            } catch (Throwable t) {
                throw new CommandLineException(
                        StringUtils.format("Invalid value for {} option '{}'.",
                                longOption ? "long" : "short", name), t);
            }
        }

        private String nextValue(String name) {
            String value = nextArg();
            if (isOption(value)) {
                throw new CommandLineException(StringUtils.format(
                        "No value is specified for long option '{}'.", name));
            }
            return value;
        }

        private String nextArg() {
            i++;
            if (i >= args.length) {
                throw new CommandLineException("Command argument is already used up.");
            }
            return args[i];
        }

        private boolean isShortOption(String arg) {
            return arg.length() >= 2 &&
                    arg.startsWith(SHORT_OPTION_PREFIX) &&
                    !arg.startsWith(SHORT_OPTION_PREFIX, 1);
        }

        private boolean isLongOption(String arg) {
            return arg.length() > 3 &&
                    arg.startsWith(LONG_OPTION_PREFIX);
        }

        private boolean isEndOptionFlag(String arg) {
            return arg.equals(END_OPTION_FLAG);
        }

        private boolean isOption(String arg) {
            return isShortOption(arg) || isLongOption(arg) || isEndOptionFlag(arg);
        }

    }

    public Object get(String argument, Object defaultValue) {
        if (!this.parsedArgs.containsKey(argument)) {
            throw new IllegalArgumentException("Unknown argument: " + argument);
        }

        @SuppressWarnings("unchecked")
        Optional<Object> value = (Optional<Object>) this.parsedArgs.get(argument);
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

    public List<String> getRemainArgs() {
        return remainArgs;
    }

    public static void main(String[] args) {
        Command command = new Command();
        command.addArgument(Position.requiredIdentity("t1", 1));
        command.addArgument(Position.optionalIdentity("t2", 2));
        command.addArgument(Position.requiredIdentity("t3", 3));
        command.addArgument(Position.optionalIdentity("t4", 4));
        command.addArgument(Position.optionalIdentity("t5", 5));
        command.addArgument(Option.requiredIdentity("host", 'h'));
        command.addArgument(Option.required("port", 'p',
                Converter.INTEGER_CONVERTER,
                new Validator<Integer>() {
                    @Override
                    public String comment() {
                        return "Must be in [0, 65535]";
                    }

                    @Override
                    public boolean isValid(Integer value) {
                        return 0 <= value && value <= 65535;
                    }
                }));
        System.out.println(command.positions.get(2).isRequired());
        System.out.println(command.positions.get(4).isRequired());
        System.out.println(command.positions.get(5).isRequired());
        command.ensureRequiredPositionConsecutive();
        System.out.println(command.positions.get(2).isRequired());
        System.out.println(command.positions.get(4).isRequired());
        System.out.println(command.positions.get(5).isRequired());
        command.init(args);

        System.out.println(command.get("host"));
        System.out.println(command.get("port"));
    }
}
