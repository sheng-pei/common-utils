package ppl.common.utils.config;

import ppl.common.utils.StringUtils;
import ppl.common.utils.config.jackson.JacksonFactory;
import ppl.common.utils.config.list.ListFactory;
import ppl.common.utils.config.list.ListNode;
import ppl.common.utils.config.map.MapFactory;
import ppl.common.utils.config.map.MapNode;
import ppl.common.utils.config.scalar.ScalarFactory;
import ppl.common.utils.config.scalar.ScalarNode;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;

/**
 * Utility class for instantiating nodes of the tree config, except for {@link MissingNode}.
 */
public class Nodes {

    private static final List<NodeFactory> FACTORIES;

    static {
        List<NodeFactory> factories = new ArrayList<>();
        factories.add(new ScalarFactory());
        factories.add(new ListFactory());
        factories.add(new MapFactory());
        factories.add(new JacksonFactory());
        ServiceLoader<NodeFactory> loader = ServiceLoader.load(NodeFactory.class);
        for (NodeFactory factory : loader) {
            factories.add(factory);
        }
        factories.sort(Comparator.comparing(NodeFactory::order, Comparator.reverseOrder()));
        FACTORIES = factories;
    }

    private Nodes() {}

    /**
     *
     * @param object
     * @return
     */
    public static Node root(Object object) {
        if (object == null) {
            return new NullNode();
        }

        for (NodeFactory factory : FACTORIES) {
            if (factory.accept(object)) {
                return factory.createRoot(object);
            }
        }

        throw new IllegalArgumentException("No NodeFactory accept this object.");
    }

    /**
     *
     * @param path
     * @param object
     * @return
     */
    public static Node createByPath(String path, Object object) {
        if (object == null) {
            return new NullNode(path);
        }

        for (NodeFactory factory : FACTORIES) {
            if (factory.accept(object)) {
                return factory.create(path, object);
            }
        }

        throw new IllegalArgumentException("No NodeFactory accept this object.");
    }

}
