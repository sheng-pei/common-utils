package ppl.common.utils.command;

import ppl.common.utils.StringUtils;

import java.util.*;

@SuppressWarnings("unused")
public class Position<V> extends Argument<V> {

    public static Position<String> required(String name, int position) {
        return required(name, position, Converter.IDENTITY, Validator.alwaysTrue());
    }

    public static <V> Position<V> required(
            String name, int position,
            Converter<V> converter,
            Validator<V> validator) {
        Objects.requireNonNull(converter, "Converter is required.");
        Objects.requireNonNull(validator, "Validator is required.");
        return new Position<>(name, position,
                true, null,
                converter, validator);
    }

    public static Position<String> identity(String name, int position, String defaultValue) {
        return new Position<>(name, position,
                false, defaultValue,
                Converter.IDENTITY,
                Validator.alwaysTrue()
        );
    }

    public static Position<String> identity(String name, Character position) {
        return identity(name, position, null);
    }

    public static <V> Position<V> newInstance(
            String name, int position,
            V defaultValue,
            Converter<V> converter,
            Validator<V> validator) {
        Objects.requireNonNull(converter, "Converter is required.");
        Objects.requireNonNull(validator, "Validator is required.");
        return new Position<>(name, position,
                false, defaultValue,
                converter, validator);
    }

    private final int position;

    private Position(String name,
                     int position,
                     boolean required,
                     V defaultValue,
                     Converter<V> converter,
                     Validator<V> validator) {
        super(name, required, defaultValue, converter, validator);
        if (position < 0) {
            throw new IllegalArgumentException("Position must not be negative.");
        }
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return StringUtils.format("position argument '{}'", getName());
    }
}
