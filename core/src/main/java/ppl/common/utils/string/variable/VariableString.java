package ppl.common.utils.string.variable;

import java.util.Map;
import java.util.Objects;

class VariableString implements StringReplacer {

    private final String name;

    VariableString(String name) {
        this.name = name;
    }

    @Override
    public String replace(Map<String, Object> env) {
        Objects.requireNonNull(env, "Env couldn't be null");
        Object tmp = env.get(this.name);
        return tmp == null ? "" : tmp.toString();
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

}
