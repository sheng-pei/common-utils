package ppl.common.utils.command;

import ppl.common.utils.argument.AbstractArgument;
import ppl.common.utils.argument.Arguments;
import ppl.common.utils.string.Strings;

import java.util.*;
import java.util.regex.Pattern;

public class CommandArguments implements Arguments<String, Object> {
    private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z][a-zA-Z0-9]*");

    @SuppressWarnings("rawtypes")
    private static final Set<Class<? extends AbstractArgument>> SUPPORTED_ARGUMENT_TYPE;

    static {
        @SuppressWarnings("rawtypes")
        Set<Class<? extends AbstractArgument>> supported = new HashSet<>();
        supported.add(Position.class);
        supported.add(Option.class);
        SUPPORTED_ARGUMENT_TYPE = Collections.unmodifiableSet(supported);
    }

    private final Map<String, AbstractArgument<String, Object>> allArguments;
    private final Map<String, Option<Object>> longOptions;
    private final Map<String, Option<Object>> shortOptions;
    private final List<Position<Object>> positions;

    private CommandArguments(Map<String, AbstractArgument<String, Object>> allArguments,
                    Map<String, Option<Object>> longOptions,
                    Map<String, Option<Object>> shortOptions,
                    List<Position<Object>> positions) {
        this.allArguments = allArguments;
        this.longOptions = longOptions;
        this.shortOptions = shortOptions;
        this.positions = positions;
    }

    @Override
    public AbstractArgument<String, Object> get(Object s) {
        if (s instanceof String && Option.isLongOption((String) s)) {
            return longOptions.get(s);
        } else if (s instanceof String && Option.isShortOption((String) s)) {
            return shortOptions.get(s);
        } else if (s instanceof Integer && (Integer) s >= 0) {
            int pos = (Integer) s;
            return pos < positions.size() ? positions.get(pos) : null;
        }
        throw new IllegalArgumentException("Please ensure '" + s + "' is a long option or " +
                "a short option or an index of position.");
    }

    @Override
    public AbstractArgument<String, Object> getByName(String name) {
        if (Command.isName(name)) {
            return allArguments.get(name);
        }
        throw new IllegalArgumentException("Please ensure '" + name + "' is a command argument name.");
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private Map<String, AbstractArgument<String, ?>> allArguments;
        private Map<String, Option<?>> longOptions;
        private Map<String, Option<?>> shortOptions;
        private List<Position<?>> positions;

        private Builder() {}

        public CommandArguments build() {
            @SuppressWarnings({"rawtypes", "unchecked"})
            Map<String, AbstractArgument<String, Object>> allArguments = (Map) this.allArguments;
            allArguments = allArguments == null ? Collections.emptyMap() : Collections.unmodifiableMap(allArguments);
            @SuppressWarnings({"rawtypes", "unchecked"})
            Map<String, Option<Object>> longOptions = (Map) this.longOptions;
            longOptions = longOptions == null ? Collections.emptyMap() : Collections.unmodifiableMap(longOptions);
            @SuppressWarnings({"rawtypes", "unchecked"})
            Map<String, Option<Object>> shortOptions = (Map) this.shortOptions;
            shortOptions = shortOptions == null ? Collections.emptyMap() : Collections.unmodifiableMap(shortOptions);
            @SuppressWarnings({"rawtypes", "unchecked"})
            List<Position<Object>> positions = (List) this.positions;
            positions = positions == null ? Collections.emptyList() : Collections.unmodifiableList(positions);
            return new CommandArguments(allArguments,
                    longOptions, shortOptions, positions);
        }

        public Builder addArguments(List<AbstractArgument<String, ?>> arguments) {
            for (AbstractArgument<String, ?> argument : arguments) {
                addArgument(argument);
            }
            return this;
        }

        public Builder addArgument(AbstractArgument<String, ?> argument) {
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
            return this;
        }

        private void _addArgument(AbstractArgument<String, ?> argument) {
            checkArgument(argument);

            if (allArguments == null) {
                allArguments = new HashMap<>();
            }

            if (allArguments.containsKey(argument.getName())) {
                throw new IllegalArgumentException(Strings.format(
                        "Argument {} is already exists.",
                        argument.getName()));
            }
            allArguments.put(argument.getName(), argument);
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

            Map<String, Option<?>> sOptions = shortOptions;
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

        private void checkArgument(AbstractArgument<String, ?> argument) {
            String name = argument.getName();
            if (!NAME_PATTERN.matcher(name).matches()) {
                throw new IllegalArgumentException("");
            }
        }

    }
}
