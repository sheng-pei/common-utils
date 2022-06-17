package ppl.common.utils.config.nodes.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.POJONode;
import ppl.common.utils.config.*;
import ppl.common.utils.config.nodes.AbstractNode;
import ppl.common.utils.config.nodes.MissingNode;
import ppl.common.utils.logging.Logger;
import ppl.common.utils.logging.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;

public class JacksonNode extends AbstractNode {

    private final static Logger logger = LoggerFactory.getLogger(JacksonNode.class);

    private final JsonNode json;

    JacksonNode(String path, JsonNode json) {
        super(path);
        this.json = json;
    }

    @Override
    public boolean isContainer() {
        return json.isContainerNode();
    }

    @Override
    public boolean isRoot() {
        return false;
    }

    @Override
    public int size() {
        return json.size();
    }

    @Override
    public Node getChild(String fieldName) {
        String path = childPath(fieldName);
        JsonNode node = json.path(fieldName);
        return create(path, node);
    }

    @Override
    public Node getChild(Integer index) {
        String path = childPath(index);
        JsonNode node = json.path(index);
        return create(path, node);
    }

    private Node create(String path, JsonNode node) {
        Node res;
        if (node.isMissingNode()) {
            res = new MissingNode(path);
        } else if (node.isValueNode()) {
            if (node.isNumber()) {
                res = Nodes.createByPath(path, node.numberValue());
            } else if (node.isBoolean()) {
                res = Nodes.createByPath(path, node.booleanValue());
            } else if (node.isNull()) {
                res = Nodes.createByPath(path, null);
            } else if (node.isTextual()) {
                res = Nodes.createByPath(path, node.textValue());
            } else if (node.isBinary()) {
                try {
                    res = Nodes.createByPath(path, node.binaryValue());
                } catch (IOException e) {
                    throw new NodeException("Unreachable.");
                }
            } else if (node.isPojo()) {
                res = Nodes.createByPath(path, ((POJONode) node).getPojo());
            } else {
                throw new NodeException("Unknown jackson value node type.");
            }
        } else if (node.isContainerNode()) {
            res = Nodes.createByPath(path, node);
        } else {
            throw new NodeException("Unknown jackson node type.");
        }
        return res;
    }

    @Override
    public Iterator<Node> iterator() {
        return null;
    }

    @Override
    public byte[] binaryValue() {
        return new byte[0];
    }

    @Override
    public byte[] binaryValue(byte[] def) {
        return new byte[0];
    }

    @Override
    public String textValue(String def) {
        if (json.isContainerNode()) {
            throw new ConvertException("Container node.");
        }

        return null;
    }

    @Override
    public String textValue() {
        if (json.isContainerNode()) {
            throw new ConvertException("Container node.");
        }
        return null;
    }

    @Override
    public Byte byteValue(Byte def) {
        if (json.isContainerNode()) {
            throw new ConvertException("Container node.");
        }
        return null;
    }

    @Override
    public Byte byteValue() {
        if (json.isContainerNode()) {
            throw new ConvertException("Container node.");
        }
        return null;
    }

    @Override
    public Short shortValue(Short def) {
        if (json.isContainerNode()) {
            throw new ConvertException("Container node.");
        }
        return null;
    }

    @Override
    public Short shortValue() {
        if (json.isContainerNode()) {
            throw new ConvertException("Container node.");
        }
        return null;
    }

    @Override
    public Integer intValue(Integer def) {
        if (json.isContainerNode()) {
            throw new ConvertException("Container node.");
        }
        return null;
    }

    @Override
    public Integer intValue() {
        if (json.isContainerNode()) {
            throw new ConvertException("Container node.");
        }
        return null;
    }

    @Override
    public Long longValue(Long def) {
        if (json.isContainerNode()) {
            throw new ConvertException("Container node.");
        }
        return null;
    }

    @Override
    public Long longValue() {
        if (json.isContainerNode()) {
            throw new ConvertException("Container node.");
        }
        return null;
    }

    @Override
    public Boolean boolValue(Boolean def) {
        if (json.isContainerNode()) {
            throw new ConvertException("Container node.");
        }
        return null;
    }

    @Override
    public Boolean boolValue() {
        if (json.isContainerNode()) {
            throw new ConvertException("Container node.");
        }
        return null;
    }

    @Override
    public Double doubleValue() {
        if (json.isContainerNode()) {
            throw new ConvertException("Container node.");
        }
        return null;
    }

    @Override
    public Double doubleValue(Double def) {
        if (json.isContainerNode()) {
            throw new ConvertException("Container node.");
        }
        return null;
    }

    @Override
    public <E extends Enum<E>> E enumValue(Class<E> enumClass) {
        if (json.isContainerNode()) {
            throw new ConvertException("Container node.");
        }
        return null;
    }
}
