package ppl.common.utils.command;

import ppl.common.utils.StringUtils;

import java.util.*;

@SuppressWarnings("unused")
public class Position<V> extends Argument<V> {

    public static Position<String> requiredIdentity(String name, int position) {
        return required(name, position, Converter.IDENTITY, Validator.alwaysTrue());
    }

    public static <V> Position<V> required(
            String name, int position,
            Converter<V> converter,
            Validator<V> validator) {
        Objects.requireNonNull(converter, "Converter is required.");
        Objects.requireNonNull(validator, "Validator is required.");
        return new Builder<V>()
                .withName(name)
                .withPosition(position)
                .withRequired(true)
                .withDefaultValue(null)
                .withConverter(converter)
                .withValidator(validator)
                .build();
    }

    public static Position<String> optionalIdentity(String name, int position, String defaultValue) {
        return new Builder<String>()
                .withName(name)
                .withPosition(position)
                .withRequired(false)
                .withDefaultValue(defaultValue)
                .withConverter(Converter.IDENTITY)
                .withValidator(Validator.alwaysTrue())
                .build();
    }

    public static Position<String> optionalIdentity(String name, int position) {
        return optionalIdentity(name, position, null);
    }

    public static <V> Position<V> optional(
            String name, int position,
            V defaultValue,
            Converter<V> converter,
            Validator<V> validator) {
        Objects.requireNonNull(converter, "Converter is required.");
        Objects.requireNonNull(validator, "Validator is required.");
        return new Builder<V>()
                .withName(name)
                .withPosition(position)
                .withRequired(false)
                .withDefaultValue(defaultValue)
                .withConverter(converter)
                .withValidator(validator)
                .build();
    }

    private final int position;

    private Position(String name,
                     int position,
                     boolean required,
                     V defaultValue,
                     Converter<V> converter,
                     Validator<V> validator) {
        super(name, required, defaultValue, converter, validator);
        if (position <= 0) {
            throw new IllegalArgumentException("Position must be positive.");
        }
        this.position = position;
    }

    public Builder<V> with() {
        return new Builder<V>()
                .with(this);
    }

    public int getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return StringUtils.format("position argument '{}'", getName());
    }

    public static class Builder<V> extends Argument.Builder<V, Builder<V>> {
        private int position;

        private Builder<V> with(Position<V> position) {
            return new Builder<V>()
                    .withSuper(position)
                    .withPosition(position.getPosition());
        }

        public Builder<V> withPosition(int position) {
            this.position = position;
            return this;
        }

        public Position<V> build() {
            return new Position<>(name, position, required, defaultValue, converter, validator);
        }

    }
}
