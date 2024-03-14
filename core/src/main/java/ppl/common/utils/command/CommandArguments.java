package ppl.common.utils.command;

import ppl.common.utils.argument.argument.Argument;
import ppl.common.utils.argument.argument.Arguments;
import ppl.common.utils.string.Strings;

import java.util.*;
import java.util.regex.Pattern;

public class CommandArguments implements Arguments<Object, Argument> {
    private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z][a-zA-Z0-9]*");

    private static final Set<Class<? extends Argument>> SUPPORTED_ARGUMENT_TYPE;

    static {
        Set<Class<? extends Argument>> supported = new HashSet<>();
        supported.add(PositionArgument.class);
        supported.add(ValueOptionArgument.class);
        supported.add(ToggleOptionArgument.class);
        SUPPORTED_ARGUMENT_TYPE = Collections.unmodifiableSet(supported);
    }

    @SuppressWarnings("rawtypes")
    private final Map allArguments;
    @SuppressWarnings("rawtypes")
    private final Map longOptions;
    @SuppressWarnings("rawtypes")
    private final Map shortOptions;
    @SuppressWarnings("rawtypes")
    private final List positions;

    private CommandArguments(
            @SuppressWarnings("rawtypes") Map allArguments,
            @SuppressWarnings("rawtypes") Map longOptions,
            @SuppressWarnings("rawtypes") Map shortOptions,
            @SuppressWarnings("rawtypes") List positions) {
        this.allArguments = allArguments;
        this.longOptions = longOptions;
        this.shortOptions = shortOptions;
        this.positions = positions;
    }

    @Override
    public List<Argument> getArguments() {
        @SuppressWarnings({"rawtypes", "unchecked"})
        List<Argument> res = new ArrayList<>(allArguments.values());
        return res;
    }

    @Override
    public Argument getByKey(Object s) {
        if (s instanceof String) {
            String option = (String) s;
            if (BaseOption.isLongOption(option)) {
                return getLongOption(option);
            } else if (BaseOption.isShortOption(option)) {
                return getShortOption(option);
            }
        } else if (s instanceof Integer) {
            int index = (Integer) s;
            if (index >= 0) {
                return getPosition(index);
            }
        }
        throw new IllegalArgumentException("Please ensure '" + s + "' is a long option or " +
                "a short option or a non-negative index.");
    }

    private Argument getLongOption(String option) {
        return (Argument) longOptions.get(option);
    }

    private Argument getShortOption(String option) {
        return (Argument) shortOptions.get(option);
    }

    private Argument getPosition(int index) {
        if (index >= positions.size()) {
            return null;
        }

        return (Argument) positions.get(index);
    }

    @Override
    public Argument getByName(String name) {
        checkName(name);
        return (Argument) allArguments.get(name);
    }

    private static void checkName(String name) {
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("Invalid argument name: " + name);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        @SuppressWarnings("rawtypes")
        private Map allArguments;
        @SuppressWarnings("rawtypes")
        private Map longOptions;
        @SuppressWarnings("rawtypes")
        private Map shortOptions;
        @SuppressWarnings("rawtypes")
        private List positions;

        private Builder() {
        }

        public CommandArguments build() {
            @SuppressWarnings({"rawtypes", "unchecked"})
            Map<String, Argument> allArguments = (Map) this.allArguments;
            allArguments = allArguments == null ? Collections.emptyMap() : Collections.unmodifiableMap(allArguments);
            @SuppressWarnings({"rawtypes", "unchecked"})
            Map<String, ValueOptionArgument<Object>> longOptions = (Map) this.longOptions;
            longOptions = longOptions == null ? Collections.emptyMap() : Collections.unmodifiableMap(longOptions);
            @SuppressWarnings({"rawtypes", "unchecked"})
            Map<String, ValueOptionArgument<Object>> shortOptions = (Map) this.shortOptions;
            shortOptions = shortOptions == null ? Collections.emptyMap() : Collections.unmodifiableMap(shortOptions);
            @SuppressWarnings({"rawtypes", "unchecked"})
            List<PositionArgument<Object>> positions = (List) this.positions;
            positions = positions == null ? Collections.emptyList() : Collections.unmodifiableList(positions);
            return new CommandArguments(allArguments,
                    longOptions, shortOptions, positions);
        }


        public Builder addArgument(Argument argument) {
            checkArgument(argument);
            addAllArgument(argument);
            _addArgument(argument);
            return this;
        }

        public Builder addArgument(ToggleOptionArgument argument) {
            checkArgument(argument);
            addAllArgument(argument);
            _addToggleOptionArgument(argument);
            return this;
        }

        public Builder addArgument(ValueOptionArgument<?> argument) {
            checkArgument(argument);
            addAllArgument(argument);
            _addValueOptionArgument(argument);
            return this;
        }

        public Builder addArgument(PositionArgument<?> argument) {
            checkArgument(argument);
            addAllArgument(argument);
            _addPositionArgument(argument);
            return this;
        }

        private void addAllArgument(Argument argument) {
            String name = argument.name();
            @SuppressWarnings("unchecked")
            Map<String, Argument> allArguments = ensureAllArguments();
            if (allArguments.containsKey(name)) {
                throw new IllegalArgumentException(Strings.format(
                        "The arguments '{}' and '{}' has same name: '{}'.",
                        argument, allArguments.get(name), name));
            }
            allArguments.put(name, argument);
        }

        private void _addArgument(Argument argument) {
            if (argument instanceof ValueOptionArgument) {
                _addValueOptionArgument((ValueOptionArgument<?>) argument);
            } else if (argument instanceof ToggleOptionArgument) {
                _addToggleOptionArgument((ToggleOptionArgument) argument);
            } else if (argument instanceof PositionArgument) {
                _addPositionArgument((PositionArgument<?>) argument);
            } else {
                throw new IllegalArgumentException(Strings.format(
                        "Unsupported argument type '{}' in command. Please use '{}'.",
                        argument.getClass().getCanonicalName(), SUPPORTED_ARGUMENT_TYPE));
            }
        }

        private void _addPositionArgument(PositionArgument<?> argument) {
            addPosition(argument);
        }

        public void _addToggleOptionArgument(ToggleOptionArgument argument) {
            addLongOption(argument);
            addShortOption(argument);
        }

        public void _addValueOptionArgument(ValueOptionArgument<?> argument) {
            addLongOption(argument);
            addShortOption(argument);
        }

        private void addShortOption(ToggleOptionArgument argument) {
            addShortOption(argument, argument);
        }

        private void addShortOption(ValueOptionArgument<?> argument) {
            addShortOption(argument, argument);
        }

        private void addShortOption(Option option, Argument argument) {
            @SuppressWarnings("unchecked")
            Map<String, Argument> optionArguments = ensureShortOptions();
            addOption(optionArguments, option.getShortOptions(), argument);
        }

        private void addLongOption(ToggleOptionArgument argument) {
            addLongOption(argument, argument);
        }

        private void addLongOption(ValueOptionArgument<?> argument) {
            addLongOption(argument, argument);
        }

        private void addLongOption(Option option, Argument argument) {
            @SuppressWarnings("unchecked")
            Map<String, Argument> optionArguments = ensureLongOptions();
            addOption(optionArguments, option.getLongOptions(), argument);
        }

        private void addOption(Map<String, Argument> optionArguments,
                               List<String> options,
                               Argument argument) {
            options.forEach(o -> {
                if (optionArguments.containsKey(o)) {
                    throw new IllegalArgumentException(Strings.format(
                            "The arguments '{}' and '{}' has same option: '{}'.",
                            argument, optionArguments.get(o), o));
                }
                optionArguments.put(o, argument);
            });
        }

        private void addPosition(PositionArgument<?> argument) {
            @SuppressWarnings("unchecked")
            List<PositionArgument<?>> positions = ensurePositions();
            argument.init(positions.size());
            positions.add(argument);
        }

        @SuppressWarnings("rawtypes")
        private Map ensureAllArguments() {
            Map res = allArguments;
            if (res == null) {
                res = new HashMap<>();
            }
            return allArguments = res;
        }

        @SuppressWarnings("rawtypes")
        private Map ensureLongOptions() {
            Map res = longOptions;
            if (res == null) {
                res = new HashMap<>();
            }
            return longOptions = res;
        }

        @SuppressWarnings("rawtypes")
        private Map ensureShortOptions() {
            Map res = shortOptions;
            if (res == null) {
                res = new HashMap<>();
            }
            return shortOptions = res;
        }

        @SuppressWarnings("rawtypes")
        private List ensurePositions() {
            List res = positions;
            if (res == null) {
                res = new ArrayList<>();
            }
            return positions = res;
        }

        private void checkArgument(Argument argument) {
            checkName(argument.name());
        }

    }
}
