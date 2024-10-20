package ppl.common.utils.string.variable;

import ppl.common.utils.string.variable.replacer.DynamicString;
import ppl.common.utils.string.variable.replacer.StringReplacer;

import java.util.Objects;

public class VariableParser {
    private VariableParser() {}

    public static StringReplacer parse(String src) {
        Objects.requireNonNull(src, "Variable pattern couldn't be null.");
        return new DynamicString(src);
    }
}
