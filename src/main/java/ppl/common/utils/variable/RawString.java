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

}
