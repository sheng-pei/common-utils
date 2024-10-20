package ppl.common.utils.string.variable.replacer;

import java.util.Map;
import java.util.Objects;

public class RawString implements StringReplacer {

    private final String str;

    public RawString(String str) {
        this.str = str;
    }

    @Override
    public String replace(Map<String, Object> env) {
        return replace(env, true);
    }

    @Override
    public String replace(Map<String, Object> env, boolean reserveNullVariable) {
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
