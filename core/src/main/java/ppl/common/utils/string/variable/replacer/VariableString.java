package ppl.common.utils.string.variable.replacer;

import java.util.Map;
import java.util.Objects;

public class VariableString implements StringReplacer {

    private final String name;

    public VariableString(String name) {
        this.name = name;
    }

    @Override
    public String replace(Map<String, ?> env) {
        return replace(env, true);
    }

    @Override
    public String replace(Map<String, ?> env, boolean reserveNullVariable) {
        Objects.requireNonNull(env, "Env couldn't be null");
        Object tmp = env.get(this.name);
        return tmp == null ? (reserveNullVariable ? toString() : "") : tmp.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VariableString that = (VariableString) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "${" + name + "}";
    }
}
