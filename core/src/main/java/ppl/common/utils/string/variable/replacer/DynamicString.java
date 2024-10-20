package ppl.common.utils.string.variable.replacer;

import ppl.common.utils.string.variable.VariablePatternException;

import java.util.*;

public class DynamicString implements StringReplacer {

    private final List<StringReplacer> parts;

    public DynamicString(String dynamic) {
        this.parts = parseVariable(dynamic);
    }

    private List<StringReplacer> parseVariable(String src) {
        List<StringReplacer> res = new ArrayList<>();

        char[] dynamicArr = src.toCharArray();
        StringBuilder builder = new StringBuilder();
        boolean mayVariable = false;
        boolean variableStart = false;
        boolean escaped = false;
        for (char curr : dynamicArr) {
            switch (curr) {
                case '\\':
                    if (mayVariable) {
                        builder.append('$');
                        mayVariable = false;
                    }
                    if (!escaped) {
                        escaped = true;
                        break;
                    }
                    builder.append(curr);
                    escaped = false;
                    break;
                case '$':
                    if (escaped) {
                        builder.append(curr);
                        escaped = false;
                        break;
                    }
                    if (mayVariable) {
                        builder.append('$');
                        break;
                    }
                    if (variableStart) {
                        throw new VariablePatternException("Variable couldn't be nested.");
                    }
                    mayVariable = true;
                    break;
                case '{':
                    if (escaped) {
                        builder.append(curr);
                        break;
                    }
                    if (mayVariable) {
                        variableStart = true;
                        mayVariable = false;
                        if (builder.length() != 0) {
                            RawString raw = new RawString(builder.toString());
                            res.add(raw);
                            builder.setLength(0);
                        }
                        break;
                    }
                    builder.append(curr);
                    break;
                case '}':
                    if (escaped) {
                        builder.append(curr);
                        escaped = false;
                        break;
                    }
                    if (mayVariable) {
                        builder.append('$').append(curr);
                        mayVariable = false;
                        break;
                    }
                    if (variableStart) {
                        if (builder.length() == 0) {
                            throw new VariablePatternException("Variable name couldn't empty.");
                        }

                        VariableString variable = new VariableString(builder.toString());
                        res.add(variable);
                        builder.setLength(0);
                        variableStart = false;
                        break;
                    }
                    builder.append(curr);
                    break;
                default:
                    if (mayVariable) {
                        builder.append('$');
                        mayVariable = false;
                    }
                    builder.append(curr);
                    if (escaped) {
                        escaped = false;
                    }
                    break;
            }
        }

        StringBuilder last = new StringBuilder();
        if (variableStart) {
            last.append("${");
        }
        last.append(builder);
        if (escaped) {
            last.append('\\');
        }
        if (mayVariable) {
            last.append('$');
        }
        if (last.length() != 0) {
            res.add(new RawString(last.toString()));
        }
        return Collections.unmodifiableList(res);
    }

    @Override
    public String replace(Map<String, Object> env) {
        return replace(env, true);
    }

    @Override
    public String replace(Map<String, Object> env, boolean reserveNullVariable) {
        Objects.requireNonNull(env, "Env couldn't be null");
        StringBuilder builder = new StringBuilder();
        this.parts.forEach(p -> builder.append(p.replace(env, reserveNullVariable)));
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DynamicString that = (DynamicString) o;
        return Objects.equals(parts, that.parts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parts);
    }
}
