package ppl.common.utils.argument.argument;

import java.util.Objects;

public abstract class Argument {
    private final String name;

    protected Argument(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Argument name is required.");
        }

        this.name = name;
    }

    public String name() {
        return this.name;
    }

    public abstract String keyString();

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Argument argument = (Argument) object;
        return Objects.equals(name, argument.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
