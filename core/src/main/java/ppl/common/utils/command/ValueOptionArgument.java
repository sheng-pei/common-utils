package ppl.common.utils.command;

import ppl.common.utils.argument.argument.value.ValueArgument;
import ppl.common.utils.argument.argument.value.ValueArgumentBuilder;
import ppl.common.utils.argument.argument.value.ValueArgumentNormalizer;
import ppl.common.utils.argument.argument.value.collector.ExCollectors;
import ppl.common.utils.string.Strings;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ValueOptionArgument<V> extends ValueArgument<String, V> implements Option {

    private static class OptionId implements Function<BaseOption, String> {
        @Override
        public String apply(BaseOption option) {
            List<String> first = option.getLongOptions();
            List<String> second = option.getShortOptions();
            if (!first.isEmpty()) {
                return first.get(0);
            } else {
                return second.get(0);
            }
        }
    }

    private static final OptionId OPTION_ID = new OptionId();

    @SuppressWarnings({"rawtypes"})
    private static final BiFunction DEFAULT_TO_CANONICAL_STRING =
            ValueArgumentNormalizer.newBuilder(Command.SEPARATOR, true)
                    .withKey(a -> OPTION_ID.apply(((ValueOptionArgument) a).option))
                    .build();

    public static <V> BiFunction<ValueOptionArgument<V>, V, String> newToCanonical(Function<V, String> value) {
        return ValueArgumentNormalizer.<String, V, ValueOptionArgument<V>>newBuilder(Command.SEPARATOR, true)
                .withKey(a -> OPTION_ID.apply(a.option))
                .withValue(value)
                .build();
    }

    public static <V> BiFunction<ValueOptionArgument<V>, V, String> defToCanonical() {
        @SuppressWarnings("unchecked")
        BiFunction<ValueOptionArgument<V>, V, String> res = DEFAULT_TO_CANONICAL_STRING;
        return res;
    }

    public static ValueOptionArgument<String> requiredIdentity(String longOption) {
        return requiredIdentity(longOption, null);
    }

    public static ValueOptionArgument<String> requiredIdentity(Character shortOption) {
        return requiredIdentity(null, shortOption);
    }

    public static ValueOptionArgument<String> requiredIdentity(String longOption, Character shortOption) {
        return newBuilder(longOption, shortOption)
                .collect(ExCollectors.required())
                .build(defToCanonical());
    }

    public static ValueOptionArgument<String> optionalIdentity(String longOption) {
        return optionalIdentity(longOption, null);
    }

    public static ValueOptionArgument<String> optionalIdentity(Character shortOption) {
        return optionalIdentity(null, shortOption);
    }

    public static ValueOptionArgument<String> optionalIdentity(String longOption, Character shortOption) {
        return optionalIdentity(
                BaseOption.name(longOption, shortOption),
                Collections.singletonList(longOption),
                Collections.singletonList(shortOption));
    }

    public static ValueOptionArgument<String> optionalIdentity(String name,
                                                               List<String> longOptions,
                                                               List<Character> shortOptions) {
        return newBuilder(name, longOptions, shortOptions)
                .collect(ExCollectors.one())
                .build(defToCanonical());
    }

    public static Builder<String> newBuilder(String longOption, Character shortOption) {
        return newBuilder(BaseOption.name(longOption, shortOption), longOption, shortOption);
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

    private final BaseOption option;

    private ValueOptionArgument(String name,
                                BaseOption option,
                                Function<String, Stream<String>> splitter,
                                @SuppressWarnings("rawtypes") List mappers,
                                @SuppressWarnings("rawtypes") Collector collector,
                                BiFunction<ValueOptionArgument<V>, V, String> toCanonicalString) {
        super(name, splitter, mappers, collector, toCanonicalString);
        this.option = option;
    }

    @Override
    public List<String> getLongOptions() {
        return this.option.getLongOptions();
    }

    @Override
    public List<String> getShortOptions() {
        return this.option.getShortOptions();
    }

    @Override
    public String toString() {
        return Strings.format("{}, name->{}", this.option, getName());
    }

    public static class Builder<V> extends ValueArgumentBuilder<String, V> {
        private final BaseOption.Builder option = BaseOption.newBuilder();

        private Builder(String name) {
            super(name);
        }

        @Override
        protected <A extends ValueArgument<String, V>> A create(String name, Function<String, Stream<String>> splitter, List<?> mappers, Collector<?, ?, ?> collector, BiFunction<A, V, String> toCanonicalString) {
            BiFunction<ValueOptionArgument<V>, V, String> bi = defToCanonical();
            @SuppressWarnings("unchecked")
            BiFunction<ValueOptionArgument<V>, V, String> in = (BiFunction<ValueOptionArgument<V>, V, String>) toCanonicalString;
            @SuppressWarnings("unchecked")
            A ret = (A) new ValueOptionArgument<>(name,
                    option.build(),
                    splitter, mappers,
                    collector, toCanonicalString == null ? bi : in);
            return ret;
        }

        private Builder<V> withLongOptions(List<String> longOptions) {
            option.withLongOptions(longOptions.stream()
                    .distinct()
                    .collect(Collectors.toList()));
            return this;
        }

        private Builder<V> withShortOptions(List<Character> shortOptions) {
            option.withShortOptions(shortOptions.stream()
                    .distinct()
                    .collect(Collectors.toList()));
            return this;
        }
    }

}
