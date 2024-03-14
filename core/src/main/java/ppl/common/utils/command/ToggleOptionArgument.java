package ppl.common.utils.command;

import ppl.common.utils.argument.argument.Argument;
import ppl.common.utils.argument.argument.ArgumentBuilder;
import ppl.common.utils.string.Strings;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class ToggleOptionArgument extends Argument implements Option {

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
        super(name);
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
        return Strings.format("{}, name->{}", this.option, name());
    }

    @Override
    public String keyString() {
        return id();
    }

    public static class Builder extends ArgumentBuilder {

        private final BaseOption.Builder option = BaseOption.newBuilder();

        private Builder(String name) {
            super(name);
        }

        @Override
        protected <A extends Argument> A create(String name) {
            @SuppressWarnings("unchecked")
            A ret = (A) new ToggleOptionArgument(name, option.build());
            return ret;
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

    }
}
