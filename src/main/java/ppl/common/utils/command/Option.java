package ppl.common.utils.command;

import ppl.common.utils.argument.AbstractArgument;
import ppl.common.utils.argument.AbstractBuilder;
import ppl.common.utils.argument.TypeReference;
import ppl.common.utils.string.Strings;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Option<V> extends AbstractArgument<String, V> {

    public static <V> TypeReference<Option<V>> ref() {
        @SuppressWarnings("unchecked")
        TypeReference<Option<V>> res = (TypeReference<Option<V>>) TypeReference.TYPE_REFERENCE;
        return res;
    }

    static final String END_OPTION_FLAG = "--";
    static final String LONG_OPTION_PREFIX = "--";
    static final String SHORT_OPTION_PREFIX = "-";
    static final Pattern LONG_OPTION_PATTERN = Pattern.compile("[a-zA-Z][0-9a-zA-Z]*");
    static final Pattern SHORT_OPTION_PATTERN = Pattern.compile("[a-zA-Z]");

    static boolean isLongOption(String string) {
        return string.startsWith(Option.LONG_OPTION_PREFIX) &&
                Option.LONG_OPTION_PATTERN
                        .matcher(string.substring(Option.LONG_OPTION_PREFIX.length()))
                        .matches();
    }

    static boolean isShortOption(String string) {
        return string.startsWith(Option.SHORT_OPTION_PREFIX) &&
                Option.SHORT_OPTION_PATTERN
                        .matcher(string.substring(Option.SHORT_OPTION_PREFIX.length()))
                        .matches();
    }

    private static final BiFunction<Option<Void>, Void, String> TOGGLE_TO_CANONICAL_STRING = (o, v) -> {
        if (!o.shortOptions.isEmpty()) {
            return o.shortOptions.get(0);
        } else {
            return o.longOptions.get(0);
        }
    };

    public static Option<Void> toggle(String longOption) {
        return toggle(longOption, null);
    }

    public static Option<Void> toggle(Character shortOption) {
        return toggle(null, shortOption);
    }

    public static Option<Void> toggle(String longOption, Character shortOption) {
        return toggle(name(longOption, shortOption),
                Collections.singletonList(longOption),
                Collections.singletonList(shortOption));
    }

    public static Option<Void> toggle(String name, List<String> longOptions, List<Character> shortOptions) {
        return Option.<Void>pNewBuilder(name, longOptions, shortOptions)
                .withToggle()
                .build(TOGGLE_TO_CANONICAL_STRING);
    }

    public static Builder<String> newBuilder(String longOption, Character shortOption) {
        return newBuilder(name(longOption, shortOption), longOption, shortOption);
    }

    public static Builder<String> newBuilder(String name, String longOption, Character shortOption) {
        return newBuilder(name, Collections.singletonList(longOption), Collections.singletonList(shortOption));
    }

    public static Builder<String> newBuilder(String name, List<String> longOptions, List<Character> shortOptions) {
        return pNewBuilder(name, longOptions, shortOptions);
    }

    private static <V> Builder<V> pNewBuilder(String name, List<String> longOptions, List<Character> shortOptions) {
        return new Builder<V>(name)
                .withLongOptions(longOptions)
                .withShortOptions(shortOptions);
    }

    private final List<String> longOptions;
    private final List<String> shortOptions;
    private final boolean toggle;

    private Option(String name,
                   List<String> longOptions,
                   List<Character> shortOptions,
                   boolean toggle,
                   Function<String, Stream<String>> splitter,
                   @SuppressWarnings("rawtypes") List mappers,
                   @SuppressWarnings("rawtypes") Collector collector,
                   BiFunction<Option<V>, V, String> toCanonicalString) {
        super(name, splitter, mappers, collector, toCanonicalString);
        this.longOptions = Collections.unmodifiableList(longOptions.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .peek(Option::checkLongOption)
                .map(l -> LONG_OPTION_PREFIX + l)
                .collect(java.util.stream.Collectors.toList()));
        this.shortOptions = Collections.unmodifiableList(shortOptions.stream()
                .filter(Objects::nonNull)
                .peek(Option::checkShortOption)
                .map(c -> SHORT_OPTION_PREFIX + c)
                .collect(java.util.stream.Collectors.toList()));
        if (this.longOptions.isEmpty() && this.shortOptions.isEmpty()) {
            throw new IllegalArgumentException("Long options or short options must not be empty.");
        }
        this.toggle = toggle;
    }

    private static void checkLongOption(String longOption) {
        if (!LONG_OPTION_PATTERN.matcher(longOption).matches()) {
            throw new IllegalArgumentException("Invalid long option: " + longOption);
        }
    }

    private static void checkShortOption(Character shortOption) {
        if (!SHORT_OPTION_PATTERN.matcher(shortOption.toString()).matches()) {
            throw new IllegalArgumentException("Invalid short option: " + shortOption);
        }
    }

    private static String name(String longOption, Character shortOption) {
        String name = longOption;
        if (name == null || name.isEmpty()) {
            name = shortOption == null ? "" : shortOption.toString();
        }
        return name;
    }

    public List<String> getLongOptions() {
        return this.longOptions;
    }

    public List<String> getShortOptions() {
        return this.shortOptions;
    }

    public boolean isToggle() {
        return toggle;
    }

    @Override
    public String toString() {
        return Strings.format(
                "short option->{}, long option->{}, name->{}",
                this.shortOptions.stream()
                        .map(Object::toString)
                        .collect(java.util.stream.Collectors.joining("|")),
                String.join("|", this.longOptions), getName());
    }

    public static class Builder<V> extends AbstractBuilder<String, V, Option<V>> {
        private List<String> longOptions;
        private List<Character> shortOptions;
        private boolean toggle;

        private Builder(String name) {
            super(name);
        }

        public Builder<V> withLongOptions(List<String> longOptions) {
            this.longOptions = longOptions.stream()
                    .distinct().collect(Collectors.toList());
            return this;
        }

        public Builder<V> withShortOptions(List<Character> shortOptions) {
            this.shortOptions = shortOptions.stream()
                    .distinct().collect(Collectors.toList());
            return this;
        }

        private Builder<V> withToggle() {
            this.toggle = true;
            return this;
        }

        @Override
        protected Option<V> create(
                String name,
                Function<String, Stream<String>> splitter,
                List<?> mappers,
                Collector<?, ?, ?> collector,
                BiFunction<Option<V>, V, String> toCanonicalString) {
            return new Option<>(name,
                    longOptions, shortOptions, toggle,
                    splitter, mappers,
                    collector, toCanonicalString);
        }
    }
}
