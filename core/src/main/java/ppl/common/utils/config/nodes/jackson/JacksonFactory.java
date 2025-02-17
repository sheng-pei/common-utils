package ppl.common.utils.config.nodes.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import ppl.common.utils.config.Node;
import ppl.common.utils.config.NodeException;
import ppl.common.utils.config.NodeFactory;
import ppl.common.utils.config.nodes.MissingNode;
import ppl.common.utils.config.nodes.NullNode;

public class JacksonFactory implements NodeFactory {
    @Override
    public int order() {
        return -30;
    }

    @Override
    public boolean accept(Object obj) {
        try {
            return obj instanceof JsonNode;
        } catch (LinkageError e) {
            return false;
        }
    }

    @Override
    public Node createRoot(Object obj) {
        return create(Node.ROOT_PATH, obj);
    }

    @Override
    public Node create(String path, Object obj) {
        if (!accept(obj)) {
            throw new NodeException("Not jackson.");
        }

        JsonNode node = (JsonNode) obj;
        if (node.isNull()) {
            return new NullNode(path);
        } else if (node.isMissingNode()) {
            return new MissingNode(path);
        } else {
            return new JacksonNode(path, node);
        }
    }
}
