package ppl.common.utils.config.nodes.scalar;

import ppl.common.utils.config.Node;
import ppl.common.utils.config.NodeException;
import ppl.common.utils.config.NodeFactory;

public class ScalarFactory implements NodeFactory {
    @Override
    public int order() {
        return Integer.MIN_VALUE;
    }

    @Override
    public boolean accept(Object obj) {
        return ScalarNode.isScalar(obj);
    }

    @Override
    public Node createRoot(Object obj) {
        return create(Node.ROOT_PATH, obj);
    }

    @Override
    public Node create(String path, Object obj) {
        if (!accept(obj)) {
            throw new NodeException("Not scalar.");
        }
        return new ScalarNode(path, obj);
    }
}
