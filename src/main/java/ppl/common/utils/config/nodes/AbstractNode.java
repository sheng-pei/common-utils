package ppl.common.utils.config.nodes;

import ppl.common.utils.config.Node;

import java.util.regex.Matcher;

public abstract class AbstractNode implements Node {

    private final int keyStart;
    private final String path;

    protected AbstractNode(String path) {
        Matcher matcher = Node.PATH_PATTERN.matcher(path);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(
                    "Invalid path: " + path + ". Use a period('.') for root " +
                            "or string composed of cells which are result of " +
                            "concatenating a period('.') and a key which is '[' index ']' " +
                            "or field name, nonempty string with no letter '[', ']', or '{' field name '}'.");
        }

        this.keyStart = path.equals(".") ? 1 : matcher.end(matcher.groupCount());
        this.path = path;
    }

    @Override
    public final boolean isRoot() {
        return path.equals(ROOT_PATH);
    }

    @Override
    public final Integer index() {
        String k = key();
        if (k.startsWith("[")) {
            return Integer.parseInt(k.substring(1, k.length() - 1));
        }
        return null;
    }

    @Override
    public final String fieldName() {
        String k = key();
        if (k.startsWith("{")) {
            return k.substring(1, k.length() - 1);
        } else if (!k.startsWith("[")) {
            return k;
        }
        return null;
    }

    private String key() {
        return this.path.substring(this.keyStart);
    }

    @Override
    public final String path() {
        return this.path;
    }

    protected final String childPath(String fieldName) {
        if (fieldName.equals("") || fieldName.startsWith("{") || fieldName.startsWith("[")) {
            throw new IllegalArgumentException("FieldName do not allow for starting with '{' or '['.");
        }

        return String.format("%s%s%s", this.path, pathSeparator(), pathKeyOnFieldName(fieldName));
    }

    private String pathKeyOnFieldName(String fieldName) {
        return fieldName.contains(PATH_SEPARATOR) ? "{" + fieldName + "}" : fieldName;
    }

    protected final String childPath(Integer index) {
        if (index == null || index < 0) {
            throw new IllegalArgumentException("Index must be non-negative.");
        }

        return String.format("%s%s%s", this.path, pathSeparator(), pathKeyOnIndex(index));
    }

    private String pathKeyOnIndex(Integer index) {
        return "[" + index + "]";
    }

    private String pathSeparator() {
        return isRoot() ? "" : PATH_SEPARATOR;
    }

}
