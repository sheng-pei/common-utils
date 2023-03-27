package ppl.common.utils.command;

import ppl.common.utils.StringUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class Option<V> extends Argument<V> {

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
        return new Builder<Boolean>().withName(name)
                .withLongOptions(longOptions)
                .withShortOptions(shortOptions)
                .withRequired(false)
                .withToggle(true)
                .withDefaultValue(false)
                .withConverter(null)
                .withValidator(null)
                .build();
    }

    public static Option<String> requiredIdentity(String longOption) {
        return requiredIdentity(longOption, null);
    }

    public static Option<String> requiredIdentity(Character shortOption) {
        return requiredIdentity(null, shortOption);
    }

    public static Option<String> requiredIdentity(String longOption, Character shortOption) {
        return required(longOption, shortOption, Converter.IDENTITY, Validator.alwaysTrue());
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
        return optional(name,
                longOptions, shortOptions,
                defaultValue,
                Converter.IDENTITY,
                Validator.alwaysTrue());
    }

    public static <V> Option<V> required(
            String longOption, Character shortOption,
            Converter<V> converter,
            Validator<V> validator) {
        Objects.requireNonNull(converter, "Converter is required.");
        Objects.requireNonNull(validator, "Validator is required.");
        return required(name(longOption, shortOption),
                Collections.singleton(longOption),
                Collections.singleton(shortOption),
                converter, validator);
    }

    public static <V> Option<V> required(String name,
                                         Set<String> longOptions, Set<Character> shortOptions,
                                         Converter<V> converter,
                                         Validator<V> validator) {
        Objects.requireNonNull(converter, "Converter is required.");
        Objects.requireNonNull(validator, "Validator is required.");
        return new Builder<V>()
                .withName(name)
                .withLongOptions(longOptions)
                .withShortOptions(shortOptions)
                .withRequired(true)
                .withToggle(false)
                .withDefaultValue(null)
                .withConverter(converter)
                .withValidator(validator)
                .build();
    }

    public static <V> Option<V> optional(
            String longOption, Character shortOption,
            V defaultValue,
            Converter<V> converter,
            Validator<V> validator) {
        Objects.requireNonNull(converter, "Converter is required.");
        Objects.requireNonNull(validator, "Validator is required.");
        return optional(name(longOption, shortOption),
                Collections.singleton(longOption),
                Collections.singleton(shortOption),
                defaultValue, converter, validator);
    }

    public static <V> Option<V> optional(
            String name,
            Set<String> longOptions, Set<Character> shortOptions,
            V defaultValue,
            Converter<V> converter,
            Validator<V> validator) {
        Objects.requireNonNull(converter, "Converter is required.");
        Objects.requireNonNull(validator, "Validator is required.");
        return new Builder<V>()
                .withName(name)
                .withLongOptions(longOptions)
                .withShortOptions(shortOptions)
                .withRequired(false)
                .withToggle(false)
                .withDefaultValue(defaultValue)
                .withConverter(converter)
                .withValidator(validator)
                .build();
    }

    private final Set<String> longOptions;
    private final Set<Character> shortOptions;
    private final boolean toggle;

    private Option(String name,
                   Set<String> longOptions,
                   Set<Character> shortOptions,
                   boolean required,
                   boolean toggle,
                   V defaultValue,
                   Converter<V> converter,
                   Validator<V> validator) {
        super(name, required, defaultValue, converter, validator);
        this.longOptions = Collections.unmodifiableSet(longOptions.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(String::isEmpty)
                .peek(Option::checkLongOption)
                .collect(Collectors.toSet()));
        this.shortOptions = Collections.unmodifiableSet(shortOptions.stream()
                .filter(Objects::nonNull)
                .peek(Option::checkShortOption)
                .collect(Collectors.toSet()));
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

    public boolean isToggle() {
        return toggle;
    }

    @Override
    public String toString() {
        return StringUtils.format("position argument '{}'", getName());
    }

    public static class Builder<V> extends Argument.Builder<V, Builder<V>> {
        private Set<String> longOptions;
        private Set<Character> shortOptions;
        private boolean toggle;

        private Builder<V> with(Option<V> option) {
            return new Builder<V>()
                    .withSuper(option)
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

        public Builder<V> withToggle(boolean toggle) {
            this.toggle = toggle;
            return this;
        }

        public Option<V> build() {
            return new Option<>(name,
                    longOptions, shortOptions,
                    required, toggle,
                    defaultValue, converter, validator);
        }

    }
}
