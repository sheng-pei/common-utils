package ppl.common.utils.command;

import ppl.common.utils.StringUtils;

import java.util.Optional;
import java.util.regex.Pattern;

public abstract class Argument<V> {

    private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z][0-9a-zA-Z]*");

    private final String name;
    private final boolean required;
    private final V defaultValue;
    private final Converter<V> converter;
    private final Validator<V> validator;

    protected Argument(String name,
                       boolean required, V defaultValue,
                       Converter<V> converter, Validator<V> validator) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Argument name is required.");
        }

        this.name = checkName(name.trim());
        this.required = required;
        this.defaultValue = defaultValue;
        this.converter = converter;
        this.validator = validator;
    }

    private static String checkName(String name) {
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("Invalid argument name: " + name);
        }
        return name;
    }

    public String getName() {
        return this.name;
    }

    public final boolean isRequired() {
        return required;
    }

    public final Optional<V> getDefaultValue() {
        return Optional.ofNullable(this.defaultValue);
    }

    public Optional<V> resolve(String value) {
        Optional<V> res = convert(value);
        res.ifPresent(this::validValue);
        return res;
    }

    private Optional<V> convert(String value) {
        if (this.converter == null) {
            throw new UnsupportedOperationException("No converter.");
        }

        try {
            return this.converter.convert(value);
        } catch (Throwable t) {
            throw new IllegalArgumentException(StringUtils.format(
                    "Value '{}' is in error format. Please use format: '{}'",
                    value, this.converter.comment()), t);
        }
    }

    private void validValue(V v) {
        if (this.validator == null) {
            throw new UnsupportedOperationException("No validator.");
        }

        if (!validator.isValid(v)) {
            throw new IllegalArgumentException(
                    StringUtils.format(
                            "Value '{}' must meet the condition: '{}'.",
                            v, validator.comment()));
        }
    }

    @Override
    public final int hashCode() {
        return getName().hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        if (!(obj instanceof Argument)) {
            return false;
        }
        Argument<?> argument = (Argument<?>) obj;
        return getName().equals(argument.getName());
    }

    static abstract class Builder<V, T extends Builder<V, T>> {
        protected String name;
        protected boolean required;
        protected V defaultValue;
        protected Converter<V> converter;
        protected Validator<V> validator;

        @SuppressWarnings("unchecked")
        protected T withSuper(Argument<V> argument) {
            this.name = argument.getName();
            this.required = argument.isRequired();
            this.defaultValue = argument.defaultValue;
            this.converter = argument.converter;
            this.validator = argument.validator;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T withName(String name) {
            this.name = name;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T withRequired(boolean required) {
            this.required = required;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T withDefaultValue(V defaultValue) {
            this.defaultValue = defaultValue;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T withConverter(Converter<V> converter) {
            this.converter = converter;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T withValidator(Validator<V> validator) {
            this.validator = validator;
            return (T) this;
        }

    }

}
