package ppl.common.utils.command.argument;

import ppl.common.utils.StringUtils;
import ppl.common.utils.command.argument.collector.Collectors;
import ppl.common.utils.command.argument.map.Mappers;

import java.util.*;
import java.util.regex.Pattern;

public class Option<V> extends BaseArgument<V> {

    public static final String LONG_OPTION_PREFIX = "--";
    public static final String SHORT_OPTION_PREFIX = "-";

    private static final Pattern LONG_OPTION_PATTERN = Pattern.compile("[a-zA-Z][0-9a-zA-Z]*");
    private static final Pattern SHORT_OPTION_PATTERN = Pattern.compile("[a-zA-Z]");

    public static Option<Boolean> toggle(String longOption) {
        return toggle(longOption, null);
    }

    public static Option<Boolean> toggle(Character shortOption) {
        return toggle(null, shortOption);
    }

    public static Option<Boolean> toggle(String longOption, Character shortOption) {
        return toggle(name(longOption, shortOption),
                Collections.singleton(longOption),
                Collections.singleton(shortOption));
    }

    public static Option<Boolean> toggle(String name, Set<String> longOptions, Set<Character> shortOptions) {
        return new Builder<Boolean>()
                .withName(name)
                .withLongOptions(longOptions)
                .withShortOptions(shortOptions)
                .withMax(0)
                .map(Mappers.defaultValue(true))
                .collect(Collectors.one());
    }

    public static Option<String> requiredIdentity(String longOption) {
        return requiredIdentity(longOption, null);
    }

    public static Option<String> requiredIdentity(Character shortOption) {
        return requiredIdentity(null, shortOption);
    }

    public static Option<String> requiredIdentity(String longOption, Character shortOption) {
        return newBuilder(longOption, shortOption)
                .map(Mappers.required())
                .collect(Collectors.one());
    }

    public static Option<String> optionalIdentity(String longOption) {
        return optionalIdentity(longOption, (Character) null);
    }

    public static Option<String> optionalIdentity(Character shortOption) {
        return optionalIdentity(null, shortOption);
    }

    public static Option<String> optionalIdentity(String longOption, Character shortOption) {
        return optionalIdentity(longOption, shortOption, null);
    }

    public static Option<String> optionalIdentity(String longOption, String defaultValue) {
        return optionalIdentity(longOption, null, defaultValue);
    }

    public static Option<String> optionalIdentity(Character shortOption, String defaultValue) {
        return optionalIdentity(null, shortOption, defaultValue);
    }

    public static Option<String> optionalIdentity(String longOption, Character shortOption, String defaultValue) {
        return optionalIdentity(name(longOption, shortOption),
                Collections.singleton(longOption),
                Collections.singleton(shortOption),
                defaultValue);
    }

    public static Option<String> optionalIdentity(String name,
                                          Set<String> longOptions,
                                          Set<Character> shortOptions,
                                          String defaultValue) {
        return newBuilder(name, longOptions, shortOptions)
                .map(Mappers.defaultValue(defaultValue))
                .collect(Collectors.one());
    }

    public static Builder<String> newBuilder(String longOption, Character shortOption) {
        return newBuilder(name(longOption, shortOption), longOption, shortOption);
    }

    public static Builder<String> newBuilder(String name, String longOption, Character shortOption) {
        return newBuilder(name, Collections.singleton(longOption), Collections.singleton(shortOption));
    }

    public static Builder<String> newBuilder(String name, Set<String> longOptions, Set<Character> shortOptions) {
        return new Builder<String>()
                .withName(name)
                .withLongOptions(longOptions)
                .withShortOptions(shortOptions);
    }

    private final int max;
    private final Set<String> longOptions;
    private final Set<Character> shortOptions;

    private Option(String name,
                   Set<String> longOptions,
                   Set<Character> shortOptions,
                   Integer max,
                   Splitter splitter,
                   List<Mapper<?, ?>> mappers,
                   Collector<?, ?> collector) {
        super(name, splitter, mappers, collector);
        this.longOptions = Collections.unmodifiableSet(longOptions.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(String::isEmpty)
                .peek(Option::checkLongOption)
                .collect(java.util.stream.Collectors.toSet()));
        this.shortOptions = Collections.unmodifiableSet(shortOptions.stream()
                .filter(Objects::nonNull)
                .peek(Option::checkShortOption)
                .collect(java.util.stream.Collectors.toSet()));
        if (this.longOptions.isEmpty() && this.shortOptions.isEmpty()) {
            throw new IllegalArgumentException("Long options or short options must not be empty.");
        }
        max = max == null ? 1 : max;
        if (max < 0) {
            throw new IllegalArgumentException("Max value amount is non-negative.");
        }
        this.max = max;
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

    public Builder<V> with() {
        return new Builder<V>()
                .with(this);
    }

    public Set<String> getLongOptions() {
        return this.longOptions;
    }

    public Set<Character> getShortOptions() {
        return this.shortOptions;
    }

    @Override
    public String toString() {
        return StringUtils.format(
                "argument: (short option->{}, long option->{}, name->{})",
                this.shortOptions.stream()
                        .map(s -> SHORT_OPTION_PREFIX + s)
                        .collect(java.util.stream.Collectors.joining("|")),
                this.longOptions.stream()
                        .map(l -> LONG_OPTION_PREFIX + l)
                        .collect(java.util.stream.Collectors.joining("|")), getName());
    }

    public static class Builder<V> extends BaseArgument.Builder<V, Option<V>, Builder<V>> {
        private Set<String> longOptions;
        private Set<Character> shortOptions;
        private Integer max;

        private Builder() {}

        public Builder<V> with(Option<V> option) {
            return super.with(option)
                    .withLongOptions(option.getLongOptions())
                    .withShortOptions(option.getShortOptions());
        }

        public Builder<V> withLongOptions(Set<String> longOptions) {
            this.longOptions = longOptions;
            return this;
        }

        public Builder<V> withShortOptions(Set<Character> shortOptions) {
            this.shortOptions = shortOptions;
            return this;
        }

        public Builder<V> withMax(Integer max) {
            this.max = max;
            return this;
        }

        @Override
        public Builder<V> split(Splitter splitter) {
            return (Builder<V>) super.split(splitter);
        }

        @Override
        public <R> Builder<R> map(Mapper<V, R> mapper) {
            return (Builder<R>) super.map(mapper);
        }

        @Override
        public Option<V> collect() {
            return (Option<V>) super.collect();
        }

        @Override
        public <R> Option<R> collect(Collector<V, R> collector) {
            return (Option<R>) super.collect(collector);
        }

        protected Option<V> build() {
            return new Option<>(getName(), longOptions, shortOptions,
                    max, getSplitter(), getMappers(), getCollector());
        }

    }
}
