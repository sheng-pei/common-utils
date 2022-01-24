package ppl.common.utils.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractNode implements Node {

    private final int keyStart;
    private final String path;

    protected AbstractNode() {
        this.keyStart = 1;
        this.path = Node.ROOT_PATH;
    }

    protected AbstractNode(String path) {
        this.keyStart = path.lastIndexOf(Node.PATH_SEPARATOR) + 1;
        this.path = path;
    }

    @Override
    public final String key() {
        return this.path.substring(this.keyStart);
    }

    @Override
    public final String path() {
        return this.path;
    }

    protected final String childPath(String fieldName) {
        Matcher matcher = FIELD_NAME_PATTERN.matcher(fieldName);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid field name: " + fieldName +
                    ". No letter '[', ']', '{' and '}' is acceptable.");
        }

        String key = fieldName.contains(".") ? "{" + fieldName + "}" : fieldName;
        return this.path + "." + key;
    }

    protected final String childPath(Integer index) {
        if (index == null || index < 0) {
            throw new IllegalArgumentException("Index must be nonnegative.");
        }

        return this.path + ".[" + index + "]";
    }

}
