package ppl.common.utils.config;

import ppl.common.utils.StringUtils;
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

public class Nodes {

    public static List<NodeFactory> factories;

    static {
        List<NodeFactory> factories = new ArrayList<>();
        factories.add(new ScalarFactory());
        factories.add(new ListFactory());
        factories.add(new MapFactory());
        ServiceLoader<NodeFactory> loader = ServiceLoader.load(NodeFactory.class);
        for (NodeFactory factory : loader) {
            factories.add(factory);
        }
        factories.sort(Comparator.comparing(NodeFactory::order, Comparator.reverseOrder()));
        Nodes.factories = factories;
    }

    private Nodes() {}

    public static Node root(Object object) {
        if (object == null) {
            return new NullNode();
        }

        for (NodeFactory factory : factories) {
            if (factory.accept(object)) {
                return factory.createRoot(object);
            }
        }

        throw new IllegalArgumentException("No NodeFactory accept this object.");
    }

    public static Node createByPath(String path, Object object) {
        if (object == null) {
            return new NullNode(path);
        }

        for (NodeFactory factory : factories) {
            if (factory.accept(object)) {
                return factory.create(path, object);
            }
        }

        throw new IllegalArgumentException("No NodeFactory accept this object.");
    }

}
