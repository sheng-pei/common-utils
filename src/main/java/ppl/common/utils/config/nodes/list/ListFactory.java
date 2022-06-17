package ppl.common.utils.config.nodes.list;

import ppl.common.utils.config.Node;
import ppl.common.utils.config.NodeFactory;

import java.util.List;

public class ListFactory implements NodeFactory {
    @Override
    public int order() {
        return -30;
    }

    @Override
    public boolean accept(Object obj) {
        return obj instanceof List;
    }

    @Override
    public Node createRoot(Object obj) {
        return create(Node.ROOT_PATH, obj);
    }

    @Override
    public Node create(String path, Object obj) {
        if (!accept(obj)) {
            throw new IllegalArgumentException("Not list.");
        }

        return new ListNode(path, (List<?>) obj);
    }
}
