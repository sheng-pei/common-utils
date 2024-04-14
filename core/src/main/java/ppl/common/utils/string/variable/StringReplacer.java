package ppl.common.utils.string.variable;

import java.util.Map;

public interface StringReplacer {
    String replace(Map<String, Object> env);
    String replace(Map<String, Object> env, boolean reserveNullVariable);
}
