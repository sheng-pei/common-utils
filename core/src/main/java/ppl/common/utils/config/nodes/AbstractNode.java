package ppl.common.utils.config.nodes;

import ppl.common.utils.string.Strings;
import ppl.common.utils.config.Node;

import java.util.regex.Matcher;

public abstract class AbstractNode implements Node {

    private final String key;
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

        this.key = path.equals(".") ? null : matcher.group("last");
        this.path = path;
    }

    @Override
    public final boolean isRoot() {
        return path.equals(ROOT_PATH);
    }

    @Override
    public final Integer index() {
        String k = key;
        if (k != null && k.startsWith("[")) {
            return Integer.parseInt(k.substring(1, k.length() - 1));
        }
        return null;
    }

    @Override
    public final String fieldName() {
        String k = key;
        if (k == null) {
            return null;
        }

        if (k.startsWith("{")) {
            return k.substring(1, k.length() - 1);
        } else if (!k.startsWith("[")) {
            return k;
        }
        return null;
    }

    @Override
    public final String path() {
        return this.path;
    }

    protected final String childPath(String fieldName) {
        ParsedName pn = ParsedName.obj(fieldName);
        return Strings.format("{}{}{}", this.path, pathSeparator(), pn);
    }

    protected final String childPath(Integer index) {
        ParsedName pn = ParsedName.arr(index);
        return Strings.format("{}{}{}", this.path, pathSeparator(), pn);
    }

    private String pathSeparator() {
        return isRoot() ? "" : PATH_SEPARATOR;
    }

}
