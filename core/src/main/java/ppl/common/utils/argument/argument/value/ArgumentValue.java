package ppl.common.utils.argument.argument.value;

import java.util.Objects;

public class ArgumentValue<V> {
    private final ValueArgument<V> argument;
    private final V value;

    public static <V> ArgumentValue<V> create(ValueArgument<V> argument, V value) {
        return new ArgumentValue<>(argument, value);
    }

    private ArgumentValue(ValueArgument<V> argument, V value) {
        Objects.requireNonNull(argument);
        this.argument = argument;
        this.value = value;
    }

    public String keyString() {
        return argument.keyString();
    }

    public String valueString() {
        return argument.valueString(value);
    }

    public String name() {
        return this.argument.name();
    }

    public V value() {
        return value;
    }

    public ArgumentValue<V> merge(ArgumentValue<V> o) {
        if (argument != o.argument) {
            throw new IllegalArgumentException("Required same argument.");
        }
        return new ArgumentValue<>(argument, argument.merge(value, o.value));
    }
}
