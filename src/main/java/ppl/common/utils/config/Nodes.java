package ppl.common.utils.config;

import ppl.common.utils.StringUtils;
import ppl.common.utils.config.jackson.JacksonFactory;
import ppl.common.utils.config.list.ListFactory;
import ppl.common.utils.config.list.ListNode;
import ppl.common.utils.config.map.MapFactory;
import ppl.common.utils.config.map.MapNode;
import ppl.common.utils.config.scalar.ScalarFactory;
import ppl.common.utils.config.scalar.ScalarNode;
import ppl.common.utils.logging.Logger;
import ppl.common.utils.logging.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;

/**
 * Utility class for instantiating various nodes of tree configs, except for {@link MissingNode}. The specific work
 * is delegates to one of {@link NodeFactory NodeFactories}. Each factory has an order value. And lower order values
 * have higher priority. If you want to custom NodeFactory, please give a nonnegative order value with it. Otherwise
 * this utility will ignore it. Meanwhile, it is your responsibility to make sure that no two factories have the
 * same order value. If some factories have the same order value, this utility will ignore one of them.
 */
public class Nodes {

    private static final Logger logger = LoggerFactory.getLogger(Nodes.class);
    private static final List<NodeFactory> FACTORIES;

    static {
        List<NodeFactory> factories = new ArrayList<>();
        factories.add(new ScalarFactory());
        factories.add(new ListFactory());
        factories.add(new MapFactory());
        factories.add(new JacksonFactory());

        Map<Integer, NodeFactory> seen = new HashMap<>();
        ServiceLoader<NodeFactory> loader = ServiceLoader.load(NodeFactory.class);
        for (NodeFactory factory : loader) {
            if (factory.order() < 0) {
                logger.warn("Ignore the custom NodeFactory \"{}\". " +
                                "Because the order value of this factory is negative.",
                        factory.getClass().getName());
                continue;
            }

            if (seen.containsKey(factory.order())) {
                logger.warn("Ignore the custom NodeFactory \"{}\". " +
                                "Because the order value of this factory is the same as that of the factory \"{}\".",
                        factory.getClass().getName(), seen.get(factory.order()).getClass().getName());
                continue;
            }

            seen.put(factory.order(), factory);
            factories.add(factory);
        }
        factories.sort(Comparator.comparing(NodeFactory::order, Comparator.reverseOrder()));
        FACTORIES = factories;
    }

    private Nodes() {}

    /**
     * Method for creating a root node. This method iterates all factories according to the priority to check that
     * the specified meterial is accepted by one of factories. Once an accepting factory is visited, it will create
     * a root node out of the specified material. And this method will stop iterating.
     * @param material the material to process.
     * @return root node out of the specified meterial. It is created by a factory that accepts the meterial and
     * has highest priority.
     * @throws IllegalArgumentException if no factory accepts the specified meterial.
     */
    public static Node root(Object material) {
        if (material == null) {
            return new NullNode();
        }

        return visitFactories(material).createRoot(material);
    }

    /**
     * Method for creating a node whose path is the specified path. This method iterates all factories according to the
     * priority to check that the specified material is accepted by one of the factories. Once an accepting factory
     * is visited, it will create a node out of the material. Then this method will stop iterating.
     * @param path the path of this node.
     * @param material the material to process.
     * @return a node out of the specified material whose path is the specified path. It is created by a factory that
     * accepts the material and has highest priority.
     * @throws IllegalArgumentException if no factory accepts the specified material.
     */
    public static Node createByPath(String path, Object material) {
        if (material == null) {
            return new NullNode(path);
        }

        return visitFactories(material).create(path, material);
    }

    private static NodeFactory visitFactories(Object material) {
        for (NodeFactory factory : FACTORIES) {
            if (factory.accept(material)) {
                return factory;
            }
        }

        throw new IllegalArgumentException("No NodeFactory accepts this material.");
    }

}
