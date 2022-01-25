package ppl.common.utils.config.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import ppl.common.utils.config.Node;
import ppl.common.utils.config.NodeFactory;
import ppl.common.utils.config.list.ListNode;

import java.util.List;

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
        if (!accept(obj)) {
            throw new IllegalArgumentException("Not jackson.");
        }

        return new JacksonNode((JsonNode) obj);
    }

    @Override
    public Node create(String path, Object obj) {
        if (!accept(obj)) {
            throw new IllegalArgumentException("Not jackson.");
        }

        return new JacksonNode(path, (JsonNode) obj);
    }
}
