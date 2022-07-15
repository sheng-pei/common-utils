package ppl.common.utils.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppl.common.utils.config.nodes.MissingNode;
import ppl.common.utils.config.nodes.NullNode;
import ppl.common.utils.config.nodes.jackson.JacksonFactory;
import ppl.common.utils.config.nodes.list.ListFactory;
import ppl.common.utils.config.nodes.map.MapFactory;
import ppl.common.utils.config.nodes.scalar.ScalarFactory;

import java.util.*;

/**
 * Utility class for making various {@link Node}s except for {@link MissingNode}.
 * Node making is delegated to {@link NodeFactory}s, each of which requires an
 * order value. The factory with a higher order value has higher priority in node
 * making. If you want to implement factory for yourself, please provide a non-negative
 * order value for it. Otherwise, this factory will be ignored by this utility.
 * Meanwhile, it is your responsibility to make sure that no two factories have the
 * same order value. Otherwise, this utility will keep the first one of them and
 * ignore the others.
 */
public class Nodes {

    private static final Logger logger = LoggerFactory.getLogger(Nodes.class);

    //package-private for testing.
    static final List<NodeFactory> FACTORIES;

    static {
        List<NodeFactory> factories = new ArrayList<>();
        factories.addAll(systemFactories());
        factories.addAll(customFactories(ServiceLoader.load(NodeFactory.class)));
        factories.sort(Comparator.comparing(NodeFactory::order, Comparator.reverseOrder()));
        FACTORIES = factories;
    }

    private static List<NodeFactory> systemFactories() {
        List<NodeFactory> res = new ArrayList<>();
        res.add(new ScalarFactory());
        res.add(new ListFactory());
        res.add(new MapFactory());
        res.add(new JacksonFactory());
        return res;
    }

    private static List<NodeFactory> customFactories(Iterable<NodeFactory> factories) {
        List<NodeFactory> res = toList(factories);
        ignoreNegativeOrderFactories(res);
        removeDuplicateOrderFactories(res);
        return res;
    }

    private static List<NodeFactory> toList(Iterable<NodeFactory> factories) {
        List<NodeFactory> res = new ArrayList<>();
        factories.forEach(res::add);
        return res;
    }

    //package-private for testing.
    static void ignoreNegativeOrderFactories(List<NodeFactory> factories) {
        Iterator<NodeFactory> iter = factories.iterator();
        while (iter.hasNext()) {
            NodeFactory factory = iter.next();
            if (factory.order() < 0) {
                logger.warn("Ignore the custom NodeFactory \"{}\". " +
                                "Because the order value of this factory is negative.",
                        factory.getClass().getName());
                iter.remove();
            }
        }
    }

    //package-private for testing.
    static void removeDuplicateOrderFactories(List<NodeFactory> factories) {
        Iterator<NodeFactory> iter = factories.iterator();
        Map<Integer, NodeFactory> seen = new HashMap<>();
        while (iter.hasNext()) {
            NodeFactory factory = iter.next();
            if (seen.containsKey(factory.order())) {
                logger.warn("Ignore the custom NodeFactory \"{}\". " +
                                "Because the order value of this factory is the same as that of the factory \"{}\".",
                        factory.getClass().getName(), seen.get(factory.order()).getClass().getName());
                iter.remove();
            }
            seen.put(factory.order(), factory);
        }
    }

    private Nodes() {}

    /**
     * Method for creating a root node. If the specified material is not null, this method
     * will iterate all factories according to the priority to see that if the material is
     * accepted by one of the factories. Once an accepting factory is visited, it will create
     * a root node out of the material. Then this method will stop iterating and return.
     * If the material is null, a root {@link NullNode} will be returned.
     * @param material the material to process.
     * @return root node out of the specified material. It is created by a factory that
     * accepts the material and has the highest priority if the material is not null.
     * Otherwise, a root {@link NullNode} will be returned.
     * @throws IllegalArgumentException if no factory accepts the specified material.
     */
    public static Node root(Object material) {
        return createByPath(Node.ROOT_PATH, material);
    }

    /**
     * Method for creating a node whose path is the specified path. If the specified material
     * is not null, this method will iterate all factories according to the priority to see
     * that if the material is accepted by one of the factories. Once an accepting factory is
     * visited, it will create a node out of the material. Then this method will stop iterating
     * and return. If the material is null, a {@link NullNode} will be returned.
     * @param path the path of this node.
     * @param material the material to process.
     * @return a node out of the specified material whose path is the specified path. It is
     * created by a factory that accepts the material and has the highest priority if the
     * material is not null. Otherwise, a {@link NullNode} will be returned.
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
