package ppl.common.utils.command;

import ppl.common.utils.StringUtils;

import java.util.*;

@SuppressWarnings("unused")
public class Command {

    private static final String END_OPTION_FLAG = "--";
    private static final String LONG_OPTION_PREFIX = "--";
    private static final String SHORT_OPTION_PREFIX = "-";

    private final Set<Resolver<?>> allResolvers;
    private final Map<String, Resolver<?>> longOptions;
    private final Map<String, Resolver<?>> shortOptions;

    private final Map<String, Optional<?>> parsedArgs;
    private final List<String> remainArgs;

    public Command() {
        this(Collections.emptyList());
    }

    public Command(List<Resolver<?>> resolvers) {
        this.allResolvers = new HashSet<>();

        Map<String, Resolver<?>> lOptions = new HashMap<>();
        Map<String, Resolver<?>> sOptions = new HashMap<>();
        for (Resolver<?> resolver : resolvers) {
            _addResolver(resolver);
            addLongOption(lOptions, resolver);
            addShortOption(sOptions, resolver);
        }
        longOptions = lOptions;
        shortOptions = sOptions;

        this.remainArgs = new ArrayList<>();
        this.parsedArgs = new HashMap<>();
    }

    public void addResolver(Resolver<?> resolver) {
        _addResolver(resolver);
        addLongOption(longOptions, resolver);
        addShortOption(shortOptions, resolver);
    }

    private void _addResolver(Resolver<?> resolver) {
        if (allResolvers.contains(resolver)) {
            throw new CommandLineException(StringUtils.format(
                    "Argument '{}' is already exists.",
                    resolver.getArgument()));
        }
        allResolvers.add(resolver);
    }

    private static void addLongOption(Map<String, Resolver<?>> lOptions, Resolver<?> resolver) {
        resolver.getNames().forEach(name -> {
            if (lOptions.containsKey(name)) {
                throw new CommandLineException(StringUtils.format(
                        "Long option '{}' in '{}' is already exists in '{}'.",
                        name, resolver, lOptions.get(name)));
            }
            lOptions.put(name, resolver);
        });
    }

    private static void addShortOption(Map<String, Resolver<?>> sOptions, Resolver<?> resolver) {
        resolver.getShortNames().forEach(name -> {
            if (sOptions.containsKey(name)) {
                throw new CommandLineException(StringUtils.format(
                        "Short option '{}' in '{}' is already exists in '{}'.",
                        name, resolver, sOptions.get(name)));
            }
            sOptions.put(name, resolver);
        });
    }

    public void init(String[] args) {
        Parser parser = new Parser(args);
        parser.parse();
        for (Resolver<?> resolver : allResolvers) {
            if (resolver.isRequired() && !parsedArgs.containsKey(resolver.getArgument())) {
                throw new CommandLineException(StringUtils.format(
                        "Argument '{}' is not specified in command line.", resolver.getArgument()));
            }
        }
        for (Resolver<?> resolver : allResolvers) {
            if (!parsedArgs.containsKey(resolver.getArgument())) {
                parsedArgs.put(resolver.getArgument(), resolver.getDefaultValue());
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
            Resolver<?> resolver = resolverOfShortOption(shortOption, arg);
            if (resolver.isToggle()) {
                parseToggleShortOptions(arg);
            } else {
                parseNonToggleShortOption(resolver, shortOption, arg);
            }
        }

        private void parseToggleShortOptions(String arg) {
            for (int j = 1; j < arg.length(); j++) {
                parseToggleShortOption(arg.charAt(j), arg);
            }
        }

        private void parseToggleShortOption(char shortOption, String arg) {
            Resolver<?> resolver = resolverOfShortOption(shortOption, arg);
            if (!resolver.isToggle()) {
                throw new CommandLineException(StringUtils.format(
                        "Non-toggle short option '{}' must not be in '{}'",
                        shortOption, arg));
            }
            parsedArgs.put(resolver.getArgument(), Optional.of(true));
        }

        private Resolver<?> resolverOfShortOption(char shortOption, String arg) {
            Resolver<?> resolver = shortOptions.get(Character.toString(shortOption));
            if (resolver == null) {
                throw new CommandLineException(StringUtils.format(
                        "Unknown short option '{}' in '{}'.", shortOption, arg));
            }
            return resolver;
        }

        private void parseNonToggleShortOption(Resolver<?> resolver, char shortOption, String arg) {
            if (arg.length() == 2) {
                resolveNextValue(resolver, Character.toString(shortOption), false);
            } else {
                resolveValueInShortOption(resolver, shortOption, arg);
            }
        }

        private void resolveValueInShortOption(Resolver<?> resolver, char shortOption, String arg) {
            try {
                parsedArgs.put(resolver.getArgument(),
                        resolver.resolve(arg.substring(2)));
            } catch (Throwable t) {
                throw new CommandLineException(StringUtils.format(
                        "Invalid value for short option '{}' in '{}'.",
                        shortOption, arg), t);
            }
        }

        private void parseLongOption(String name) {
            Resolver<?> resolver = resolverOfLongOption(name);
            if (resolver.isToggle()) {
                parsedArgs.put(resolver.getArgument(), Optional.of(true));
            } else {
                resolveNextValue(resolver, name, true);
            }
        }

        private Resolver<?> resolverOfLongOption(String longOption) {
            Resolver<?> resolver = longOptions.get(longOption);
            if (resolver == null) {
                throw new CommandLineException(StringUtils.format(
                        "Unknown long option '{}'.", longOption));
            }
            return resolver;
        }

        private void resolveNextValue(Resolver<?> resolver, String name, boolean longOption) {
            try {
                parsedArgs.put(resolver.getArgument(),
                        resolver.resolve(nextValue(name)));
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
        command.addResolver(Resolver.required("host", 'h'));
        command.addResolver(Resolver.required("port", 'p',
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
        command.init(args);

        System.out.println(command.get("host"));
        System.out.println(command.get("port"));
    }
}
