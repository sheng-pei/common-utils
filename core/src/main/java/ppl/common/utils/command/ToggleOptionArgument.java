package ppl.common.utils.command;

import ppl.common.utils.argument.argument.Argument;
import ppl.common.utils.string.Strings;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ToggleOptionArgument extends Argument<String, Void> implements Option {

    private static class OptionId implements Function<BaseOption, String> {
        @Override
        public String apply(BaseOption option) {
            List<String> first = option.getShortOptions();
            List<String> second = option.getLongOptions();
            if (!first.isEmpty()) {
                return first.get(0);
            } else {
                return second.get(0);
            }
        }
    }

    private static final ToggleOptionArgument.OptionId OPTION_ID = new ToggleOptionArgument.OptionId();

    private static final BiFunction<ToggleOptionArgument, Void, String> TOGGLE_TO_CANONICAL_STRING =
            (o, v) -> ToggleOptionArgument.OPTION_ID.apply(o.option);

    public static ToggleOptionArgument toggle(String longOption) {
        return toggle(longOption, null);
    }

    public static ToggleOptionArgument toggle(Character shortOption) {
        return toggle(null, shortOption);
    }

    public static ToggleOptionArgument toggle(String longOption, Character shortOption) {
        return toggle(BaseOption.name(longOption, shortOption),
                Collections.singletonList(longOption),
                Collections.singletonList(shortOption));
    }

    public static ToggleOptionArgument toggle(String name, List<String> longOptions, List<Character> shortOptions) {
        Builder builder = new Builder(name);
        return builder.withLongOptions(longOptions)
                .withShortOptions(shortOptions)
                .build();
    }

    private final BaseOption option;

    private ToggleOptionArgument(String name, BaseOption option) {
        super(name, TOGGLE_TO_CANONICAL_STRING);
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

    public static class Builder {

        private final String name;
        private final BaseOption.Builder option = BaseOption.newBuilder();

        private Builder(String name) {
            this.name = name;
        }

        private ToggleOptionArgument.Builder withLongOptions(List<String> longOptions) {
            option.withLongOptions(longOptions.stream()
                    .distinct()
                    .collect(Collectors.toList()));
            return this;
        }

        private ToggleOptionArgument.Builder withShortOptions(List<Character> shortOptions) {
            option.withShortOptions(shortOptions.stream()
                    .distinct()
                    .collect(Collectors.toList()));
            return this;
        }

        protected ToggleOptionArgument build() {
            return new ToggleOptionArgument(name, option.build());
        }
    }
}
