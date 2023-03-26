package ppl.common.utils.command;

import ppl.common.utils.StringUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public final class Resolver<V> {

    private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z][0-9a-zA-Z]*");
    private static final Pattern SHORT_NAME_PATTERN = Pattern.compile("[a-zA-Z]");

    public static Resolver<Boolean> newToggle(String name, Character shortName) {
        return new Resolver<>(name, shortName,
                false, true,
                false, null, null);
    }

    public static Resolver<Boolean> newToggle(String name) {
        return newToggle(name, null);
    }

    public static Resolver<Boolean> newToggle(Character shortName) {
        return newToggle(null, shortName);
    }

    public static Resolver<String> required(String name, Character shortName) {
        return required(name, shortName, Converter.IDENTITY, Validator.alwaysTrue());
    }

    public static Resolver<String> required(String name) {
        return required(name, null);
    }

    public static Resolver<String> required(Character shortName) {
        return required(null, shortName);
    }

    public static <V> Resolver<V> required(
            String name, Character shortName,
            Converter<V> converter,
            Validator<V> validator) {
        Objects.requireNonNull(converter, "Converter is required.");
        Objects.requireNonNull(validator, "Validator is required.");
        return new Resolver<>(name, shortName,
                true, false,
                null, converter, validator);
    }

    public static Resolver<String> identity(String name, Character shortName, String defaultValue) {
        return new Resolver<>(name, shortName,
                false, false,
                defaultValue,
                Converter.IDENTITY,
                Validator.alwaysTrue()
        );
    }

    public static Resolver<String> identity(Character shortName, String defaultValue) {
        return identity(null, shortName, defaultValue);
    }

    public static Resolver<String> identity(String name, String defaultValue) {
        return identity(name, null, defaultValue);
    }

    public static Resolver<String> identity(String name, Character shortName) {
        return identity(name, shortName, null);
    }

    public static Resolver<String> identity(Character shortName) {
        return identity(null, shortName);
    }

    public static Resolver<String> identity(String name) {
        return identity(name, (Character) null);
    }

    public static <V> Resolver<V> newInstance(
            String name, Character shortName,
            V defaultValue,
            Converter<V> converter,
            Validator<V> validator) {
        Objects.requireNonNull(converter, "Converter is required.");
        Objects.requireNonNull(validator, "Validator is required.");
        return new Resolver<>(name, shortName,
                false, false,
                defaultValue, converter, validator);
    }

    private final String name;
    private final Set<String> aliases;
    private final Character shortName;
    private final Set<Character> shortAliases;
    private final boolean required;
    private final boolean toggle;
    private final V defaultValue;
    private final Converter<V> converter;
    private final Validator<V> validator;

    private Resolver(String name,
                     Character shortName,
                     boolean required,
                     boolean toggle,
                     V defaultValue,
                     Converter<V> converter,
                     Validator<V> validator) {
        String n = checkName(name);
        Character s = checkShortName(shortName);
        if (n.isEmpty() && s == null) {
            throw new IllegalArgumentException("Name or ShortName must not be empty.");
        }
        this.name = n;
        this.shortName = s;
        this.aliases = new HashSet<>();
        if (!n.isEmpty()) {
            this.aliases.add(name);
        }
        this.shortAliases = new HashSet<>();
        if (s != null) {
            this.shortAliases.add(shortName);
        }
        this.required = required;
        this.toggle = toggle;
        this.defaultValue = defaultValue;
        this.converter = converter;
        this.validator = validator;
    }

    private static String checkName(String name) {
        name = (name == null ? "" : name.trim());
        if (!name.isEmpty() && !NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("Invalid long option: " + name);
        }
        return name;
    }

    private static Character checkShortName(Character shortName) {
        if (shortName != null && !SHORT_NAME_PATTERN.matcher(shortName.toString()).matches()) {
            throw new IllegalArgumentException("Invalid short option: " + shortName);
        }
        return shortName;
    }

    public Set<String> getNames() {
        return Collections.unmodifiableSet(this.aliases);
    }

    public Set<String> getShortNames() {
        return Collections.unmodifiableSet(this.shortAliases.stream()
                .map(Object::toString).collect(Collectors.toSet()));
    }

    public String getArgument() {
        String name = this.name;
        if (name.isEmpty()) {
            name = this.shortName.toString();
        }
        return name;
    }

    public boolean isToggle() {
        return toggle;
    }

    public boolean isRequired() {
        return required;
    }

    public Optional<V> getDefaultValue() {
        return Optional.ofNullable(this.defaultValue);
    }

    public Optional<V> resolve(String value) {
        if (this.validator == null) {
            throw new UnsupportedOperationException("No validator.");
        }

        Optional<V> res = convert(value);
        if (!res.filter(validator::isValid).isPresent()) {
            throw new InvalidOptionValueException(
                    StringUtils.format(
                            "Value '{}' must meet the condition: '{}'.",
                            res.orElse(null), validator.comment()));
        }
        return res;
    }

    private Optional<V> convert(String value) {
        if (this.converter == null) {
            throw new UnsupportedOperationException("No converter.");
        }

        try {
            return this.converter.convert(value);
        } catch (Throwable t) {
            throw new InvalidOptionValueException(StringUtils.format(
                    "Value '{}' is in error format. Please use format: '{}'",
                    value, this.converter.comment()), t);
        }
    }

    @Override
    public String toString() {
        return getArgument();
    }

    @Override
    public int hashCode() {
        return getArgument().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Resolver)) {
            return false;
        }
        Resolver<?> resolver = (Resolver<?>) obj;
        return getArgument().equals(resolver.getArgument());
    }
}

