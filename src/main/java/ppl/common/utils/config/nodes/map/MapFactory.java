package ppl.common.utils.config.nodes.map;

import ppl.common.utils.config.Node;
import ppl.common.utils.config.NodeFactory;

import java.util.Map;

public class MapFactory implements NodeFactory {
    @Override
    public int order() {
        return -30;
    }

    @Override
    public boolean accept(Object obj) {
        return obj instanceof Map;
    }

    @Override
    public Node createRoot(Object obj) {
        return create(Node.ROOT_PATH, obj);
    }

    @Override
    public Node create(String path, Object obj) {
        if (!accept(obj)) {
            throw new IllegalArgumentException("Not map.");
        }

        return new MapNode(path, (Map<?, ?>) obj);
    }
}
