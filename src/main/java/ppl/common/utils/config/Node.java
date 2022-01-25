package ppl.common.utils.config;

import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * Node interface for node instances which form the basis of tree config model. The following characters
 * '[', ']', '{' and '}' are disallowed from being used as a field name of a field of an object node. We
 * use a path to address to a node from the specified root node. Path is made up of keys which are separated
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
 * <li><b>The path (.a.{b.c}.[0]) identify the first element of the array node L.</b> The key of this node is '[0]'.
 * The index 0 is surrounded by square brackets ([]) in the key.
 * </ol>
 * </p>
 */
public interface Node extends Value {

    String ROOT_PATH = ".";
    String PATH_SEPARATOR = ".";
    Pattern FIELD_NAME_PATTERN = Pattern.compile("^[^\\[\\]\\{\\}]+$");

    /**
     * Method that returns true for "virtual" nodes which represent missing entries constructed by accessor methods
     * when there is no actual node matching given criteria.
     * @return true if this node represents a "missing" node.
     */
    default boolean isMissing() {
        return false;
    }

    /**
     * Method that returns true for an array node or object node.
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
     * Method that returns the key of this node. For elements of an array node, given [1] as an example;
     * for fields of an object node, given fieldName or {fieldName} to avoid ambiguity as examples;
     * for root, empty string.
     * @return the key of this node.
     */
    String key();

    /**
     * Method that returns the path of this node, for example ".key1.key2". The keys "", "key1" and "key2"
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
     */
    Node getChild(String fieldName);

    /**
     * Method for getting an element that this node contains if this node is an array node.
     * @param index index of element to get.
     * @return the node you expected if this node is an array node and contains the specified element,
     * otherwise {@link MissingNode} is returned.
     */
    Node getChild(Integer index);

    /**
     * Method for accessing child nodes. If this node is an object or array node.
     * @return {@link Iterator} for iterating all child nodes of this node if this node is an object or array node,
     * otherwise "empty iterator" is returned.
     */
    Iterator<Node> iterator();

}
