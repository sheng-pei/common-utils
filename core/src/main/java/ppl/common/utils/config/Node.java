package ppl.common.utils.config;

import ppl.common.utils.config.nodes.MissingNode;

import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * Node interface for nodes of tree config model. A node could have either one base type value
 * (boolean, char, byte, short, int, long, float, double, BigInteger, BigDecimal, String) or any number
 * of child nodes. The following characters '[', ']', '{' and '}' are disallowed in field name of an
 * object node. We use a path to address to a node in the config. Path is a sequence of keys separated
 * by letter '.'.
 *
 * <p>
 * Given a tree config as an example:
 * <pre>
 * {
 *     "a": {
 *         "b.c": [100, 200]
 *     }
 * }</pre>
 *
 * <ol>
 * <li><b>The path (.) identify the root Object node.</b> The key of root node is always empty string.
 * <li><b>The path (.a) identify the Object node</b> which is a field named "a" of the root node.
 * The key of this node is 'a'. For simplicity, we use a letter O to represent this node.
 * <li><b>The path (.a.{b.c}) identify the array node</b> which is a field named "b.c" of the node O.
 * For simplicity, we use a letter L to represent this node. The key of this node is '{b.c}'.
 * In order to avoid ambiguity, the field name "b.c" is surrounded by curly brackets ({}) in the key.
 * <li><b>The path (.a.{b.c}.[0]) identify the first element of the array node L.</b> The key of this
 * node is '[0]'. The index 0 is surrounded by square brackets ([]) in the key.
 * </ol>
 *
 * Note: Two paths, A and B, have and not have respectively two successive separators. Replacing every
 * successive separators in B with single separator results in a path which is same as A. A and B can be
 * replaced with each other.
 * </p>
 */
public interface Node extends Value, Iterable<Node> {

    String ROOT_PATH = ".";
    String PATH_SEPARATOR = ".";
    char START_OBJECT = '{';
    char END_OBJECT = '}';
    char START_ARRAY = '[';
    char END_ARRAY = ']';

    int ARRAY = 1;
    int OBJECT = 2;

    String INDEX_FIELD_PATTERN_STRING = "\\[[0-9]+]";
    String COMPLEX_OBJECT_FIELD_PATTERN_STRING = "\\{[\\041-\\0132\\0134\\0136-\\0172\\0174\\0176]+\\}";
    String SIMPLE_OBJECT_FIELD_PATTERN_STRING = "[\\041-\\055\\057-\\0132\\0134\\0136-\\0172\\0174\\0176]+";
    String FIELD_PATTERN_STRING = INDEX_FIELD_PATTERN_STRING + "|" +
            COMPLEX_OBJECT_FIELD_PATTERN_STRING + "|" +
            SIMPLE_OBJECT_FIELD_PATTERN_STRING;
    Pattern FIELD_PATTERN = Pattern.compile(FIELD_PATTERN_STRING);
    Pattern PATH_PATTERN = Pattern.compile("^\\.|\\.?(?:(?:" +
            FIELD_PATTERN_STRING +
            ")\\.+)*(?<last>" +
            FIELD_PATTERN_STRING +
            ")\\.*$");

    /**
     * Method that returns true for "virtual" nodes which represent missing entries constructed by accessor methods
     * when there is no actual node matching given criteria.
     * @return true if this node represents a "missing" node.
     */
    default boolean isMissing() {
        return false;
    }

    /**
     * Method that returns true for a node which support iterator.
     * @return true if this node is an array node or object node, false if not.
     */
    default boolean isContainer() {
        return false;
    }

    /**
     * Method that returns true if this node is a root node of a tree.
     * @return true for root node, false otherwise.
     */
    boolean isRoot();

    /**
     * @return the index of this node if it is in an array, otherwise null.
     */
    Integer index();

    /**
     * @return the field name of this node if it is in an object, otherwise null.
     */
    String fieldName();

    /**
     * Method that returns the path of this node, for example ".key1.key2". The keys "key1" and "key2"
     * are keys of nodes in the path.
     * @return the path of this node.
     */
    String path();

    /**
     * Method that returns the number of child nodes that this node contains if this node is an object or array node.
     * @return number of child nodes for an object or array node, 0 otherwise.
     */
    int size();

    /**
     * Method for getting a field that this node contains if this node is an object node.
     * @param fieldName name of a field to get.
     * @return the node you expected if this node is an object node and has the specified field,
     * otherwise {@link MissingNode} is returned.
     * @throws NodeException when value of the given field is invalid.
     */
    Node getChild(String fieldName);

    /**
     * Method for getting an element that this node contains if this node is an array node.
     * @param index index of element to get.
     * @return the node you expected if this node is an array node and contains the specified element,
     * otherwise {@link MissingNode} is returned.
     * @throws NodeException when value of the given element is invalid.
     */
    Node getChild(Integer index);

    /**
     * Method for accessing child nodes. If this node is an object or array node.
     * @return {@link Iterator} for iterating all child nodes of this node if this node is an object or array node,
     * otherwise "empty iterator" is returned.
     * @throws NodeException error during iterating child nodes with {@link Iterator} returned.
     */
    Iterator<Node> iterator();

    class ParsedName {

        private static final String AS = "" + START_ARRAY;
        private static final String AE = "" + END_ARRAY;
        private static final String OS = "" + START_OBJECT;
        private static final String OE = "" + END_OBJECT;

        private final int type;
        private final Object field;

        public static ParsedName parse(String name) {
            if (!FIELD_PATTERN.matcher(name).matches()) {
                throw new IllegalArgumentException("Invalid name: " + name);
            }

            if (name.startsWith(AS)) {
                String iStr = name.substring(1, name.length() - 1);
                int idx = Integer.parseInt(iStr);
                return new ParsedName(ARRAY, idx);
            } else {
                String inner = name;
                if (name.startsWith(OS)) {
                    inner = name.substring(1, name.length() - 1);
                }
                return new ParsedName(OBJECT, inner);
            }
        }

        public static ParsedName arr(Integer idx) {
            if (idx == null || idx < 0) {
                throw new IllegalArgumentException("Index must be non-negative.");
            }

            return new ParsedName(ARRAY, idx);
        }

        public static ParsedName obj(String name) {
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Name is required.");
            }

            char first = name.charAt(0);
            if (first == START_ARRAY || first == START_OBJECT) {
                throw new IllegalArgumentException(String.format(
                        "Name do not allow for starting with '%s' or '%s'.", START_ARRAY, START_OBJECT));
            }

            try {
                return ParsedName.parse(Node.START_OBJECT + name + Node.END_OBJECT);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid name: " + name + ".");
            }
        }

        private ParsedName(int type, Object field) {
            this.type = type;
            this.field = field;
        }

        public boolean isArray() {
            return type == ARRAY;
        }

        public boolean isObject() {
            return type == OBJECT;
        }

        public Object getField() {
            if (field instanceof Integer || field instanceof String) {
                return field;
            } else {
                throw new IllegalStateException("Error field.");
            }
        }

        public <T> T getField(Class<T> clazz) {
            if (checkArray(clazz) || checkObject(clazz)) {
                @SuppressWarnings("unchecked")
                T t = (T) field;
                return t;
            } else {
                throw new IllegalArgumentException("Error type: " + clazz);
            }
        }

        private boolean checkArray(Class<?> clazz) {
            return (int.class.equals(clazz) || Integer.class.equals(clazz)) && type == ARRAY;
        }

        private boolean checkObject(Class<?> clazz) {
            return String.class.equals(clazz) && type == OBJECT;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            if (ARRAY == type) {
                builder.append(AS);
                builder.append(field);
                builder.append(AE);
            } else {
                String s = (String) field;
                boolean complex = s.contains(".");
                if (complex) {
                    builder.append(OS);
                }
                builder.append(s);
                if (complex) {
                    builder.append(OE);
                }
            }
            return builder.toString();
        }

        public int getType() {
            return type;
        }
    }

}
