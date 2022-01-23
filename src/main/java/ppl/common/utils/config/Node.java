package ppl.common.utils.config;

import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * Node of tree config model. No letter '[', ']', '{' and '}' is accepted by field name of any Object node.
 * We use various paths to address to any node from the specified node. Path is made up of keys which are
 * separated by letter '.'.
 *
 * <p>
 * For example:
 * <pre>
 * {
 *     "a": {
 *         "b.c": [100, 200]
 *     }
 * }</pre>
 *
 * <ol>
 * <li><b>The path (.) identify the root node</b>
 * which is an Object node. The key of root node is always empty string.
 * <li><b>The path (.a) identify an Object node</b>
 * which is a field of the Root node. The key of this node is 'a'.
 * For simplicity, we use a letter O to represent this node.
 * <li><b>The path (.a.{b.c}) identify a List node</b>
 * which is a field of the node O. For simplicity, we use a letter L to represent this node.
 * The key of this node is '{b.c}'. In order to avoid ambiguity,
 * the field name "b.c" is surrounded by curly brackets ({}).
 * <li><b>The path (.a.{b.c}.[0]) identify the first element of the List node L.</b>
 * The key of this node is '[0]'. The index 0 is surrounded by square brackets ([]).
 * </ol>
 * </p>
 */
public interface Node extends Value {

    String ROOT_PATH = ".";
    String PATH_SEPARATOR = ".";
    Pattern FIELD_NAME_PATTERN = Pattern.compile("^[^\\[\\]\\{\\}]+$");

    /**
     * @return the key of this node. for elements of a List node, [1] for example,
     * for fields of a Object node, fieldName or {fieldName} to avoid ambiguity;
     * for root, empty string.
     */
    String key();

    /**
     * @return the path of this node, ".key1.key2" for example.
     * "key1", "key2" are keys of node.
     */
    String path();

    /**
     * @return number of child nodes that this node contains if this node is Object or Array node, 0 otherwise.
     */
    int size();

    /**
     * @param fieldName name of a field to get.
     * @return the field you expected if this node is Object node and has the specified field,
     * NullObject otherwise.
     */
    Node getChild(String fieldName);

    /**
     * @param index index of element to get.
     * @return the element you expected if this node is Array node and has the specified element,
     * NullObject otherwise.
     */
    Node getChild(Integer index);

    /**
     * @return iterator for iterating all child nodes of this node if this node is Object or Array node,
     * empty iterator otherwise.
     */
    Iterator<Node> iterator();

}
