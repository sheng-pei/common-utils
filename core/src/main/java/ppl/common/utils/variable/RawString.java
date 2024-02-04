package ppl.common.utils.variable;

import java.util.Map;
import java.util.Objects;

class RawString implements StringReplacer {

    private final String str;

    RawString(String str) {
        this.str = str;
    }

    @Override
    public String replace(Map<String, Object> env) {
        Objects.requireNonNull(env, "Env couldn't be null");
        return this.str;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RawString rawString = (RawString) o;
        return Objects.equals(str, rawString.str);
    }

    @Override
    public int hashCode() {
        return Objects.hash(str);
    }
}
