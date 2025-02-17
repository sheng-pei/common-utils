package ppl.common.utils.config;

/**
 * Factory interface for node {@link Node} instances of tree config.
 */
public interface NodeFactory {

    /**
     * Get the order value of a factory.
     * @return order value.
     */
    int order();

    /**
     * Check if the specified material can be accepted for processing.
     * @param material the material to process.
     * @return true if the specified material can be accepted for processing, false if not.
     */
    boolean accept(Object material);

    /**
     * Create a root node out of the material.
     * @param material the material to process.
     * @return root of config
     * @throws NodeException if the specified material couldn't be converted into {@link Node}.
     */
    Node createRoot(Object material);

    /**
     * Create a node out of the material.
     * @param path the config path of the created node.
     * @param material the material to process.
     * @return a node
     * @throws NodeException if the specified material couldn't be converted into {@link Node}.
     */
    Node create(String path, Object material);

}
