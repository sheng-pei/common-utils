package ppl.common.utils.variable;

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

}
