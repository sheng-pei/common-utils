package ppl.common.utils.argument.argument.value;

import java.util.Objects;

public class ArgumentValue<K, V> {
    private final ValueArgument<K, V> argument;
    private final V value;

    public static <K, V> ArgumentValue<K, V> create(ValueArgument<K, V> argument, V value) {
        return new ArgumentValue<>(argument, value);
    }

    private ArgumentValue(ValueArgument<K, V> argument, V value) {
        Objects.requireNonNull(argument);
        this.argument = argument;
        this.value = value;
    }

    public K key() {
        return this.argument.getName();
    }

    public V value() {
        return value;
    }

    public ArgumentValue<K, V> merge(ArgumentValue<K, V> o) {
        if (argument != o.argument) {
            throw new IllegalArgumentException("Required same argument.");
        }
        return new ArgumentValue<>(argument, argument.merge(value, o.value));
    }

    @Override
    public String toString() {
        return argument.toCanonicalString(value);
    }
}
