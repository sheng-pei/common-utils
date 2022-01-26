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
 * Utility class for instantiating various nodes of the tree config, except for {@link MissingNode}. The specific work
 * is delegates to one of {@link NodeFactory NodeFactories}. The factories will be sorted by 
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
     * Method for creating root node of tree config. 算法描述 工厂 accept create meterial
     * @param material the material to process.
     * @return root node out of the specified meterial. It is created by a factory that accepts the meterial .
     * @throws IllegalArgumentException if no factory accepts the specified meterial.
     */
    public static Node root(Object meterial) {
        if (meterial == null) {
            return new NullNode();
        }

        for (NodeFactory factory : FACTORIES) {
            if (factory.accept(meterial)) {
                return factory.createRoot(meterial);
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
