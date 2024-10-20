package ppl.common.utils.string.variable.replacer;

import java.util.Map;

public interface StringReplacer {
    String replace(Map<String, Object> env);
    String replace(Map<String, Object> env, boolean reserveNullVariable);
}
