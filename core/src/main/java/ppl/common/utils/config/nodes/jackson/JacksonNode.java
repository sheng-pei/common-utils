package ppl.common.utils.config.nodes.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.POJONode;
import ppl.common.utils.config.*;
import ppl.common.utils.config.convert.Converters;
import ppl.common.utils.config.nodes.AbstractNode;
import ppl.common.utils.config.nodes.MissingNode;
import ppl.common.utils.config.nodes.iterator.ArrayIterator;
import ppl.common.utils.config.nodes.iterator.ObjectIterator;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Iterator;

public class JacksonNode extends AbstractNode {

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
    public int size() {
        return json.size();
    }

    @Override
    public Node getChild(String fieldName) {
        String path = childPath(fieldName);
        JsonNode node = json.path(fieldName);
        try {
            return create(path, node);
        } catch (RuntimeException e) {
            throw new NodeException("Unknown value of '" + path + "'.", e);
        }
    }

    @Override
    public Node getChild(Integer index) {
        String path = childPath(index);
        JsonNode node = json.path(index);
        try {
            return create(path, node);
        } catch (RuntimeException e) {
            throw new NodeException("Unknown value of '" + path + "'.", e);
        }
    }

    private Node create(String path, JsonNode node) {
        if (node.isMissingNode()) {
            return new MissingNode(path);
        } else {
            return Nodes.createByPath(path, value(node));
        }
    }

    private Object value(JsonNode node) {
        if (node.isValueNode()) {
            if (node.isNumber()) {
                return node.numberValue();
            } else if (node.isBoolean()) {
                return node.booleanValue();
            } else if (node.isNull()) {
                return null;
            } else if (node.isTextual()) {
                return node.textValue();
            } else if (node.isPojo()) {
                return ((POJONode) node).getPojo();
            } else {
                throw new IllegalArgumentException("Disallowed jackson value node type in config.");
            }
        } else if (node.isContainerNode()) {
            return node;
        } else {
            throw new IllegalArgumentException("Disallowed jackson node type in config.");
        }
    }

    @Override
    public Iterator<Node> iterator() {
        if (this.json.isArray()) {
            return new ArrayIterator(this.json.iterator(), this::childPath);
        } else if (this.json.isObject()) {
            return new ObjectIterator(this.json.fields(), this::childPath);
        }
        return Collections.emptyIterator();
    }

    @Override
    public String textValue(String def) {
        return textValue();
    }

    @Override
    public String textValue() {
        if (json.isContainerNode()) {
            throw new ConvertException("Jackson container node.");
        }
        return Converters.stringValue(json);
    }

    @Override
    public Byte byteValue(Byte def) {
        return byteValue();
    }

    @Override
    public Byte byteValue() {
        if (json.isContainerNode()) {
            throw new ConvertException("Jackson container node.");
        }
        return Converters.byteValue(json);
    }

    @Override
    public Short shortValue(Short def) {
        return shortValue();
    }

    @Override
    public Short shortValue() {
        if (json.isContainerNode()) {
            throw new ConvertException("Jackson container node.");
        }
        return Converters.shortValue(json);
    }

    @Override
    public Integer intValue(Integer def) {
        return intValue();
    }

    @Override
    public Integer intValue() {
        if (json.isContainerNode()) {
            throw new ConvertException("Jackson container node.");
        }
        return Converters.intValue(json);
    }

    @Override
    public Long longValue(Long def) {
        return longValue();
    }

    @Override
    public Long longValue() {
        if (json.isContainerNode()) {
            throw new ConvertException("Jackson container node.");
        }
        return Converters.longValue(json);
    }

    @Override
    public BigInteger bigintValue(BigInteger def) {
        return bigintValue();
    }

    @Override
    public BigInteger bigintValue() {
        if (json.isContainerNode()) {
            throw new ConvertException("Jackson container node.");
        }
        return Converters.bigintValue(json);
    }

    @Override
    public Boolean boolValue(Boolean def) {
        return boolValue();
    }

    @Override
    public Boolean boolValue() {
        if (json.isContainerNode()) {
            throw new ConvertException("Jackson container node.");
        }
        return Converters.boolValue(json);
    }

    @Override
    public Float floatValue(Float def) {
        return floatValue();
    }

    @Override
    public Float floatValue() {
        if (json.isContainerNode()) {
            throw new ConvertException("Jackson container node.");
        }
        return Converters.floatValue(json);
    }

    @Override
    public Double doubleValue(Double def) {
        return doubleValue();
    }

    @Override
    public Double doubleValue() {
        if (json.isContainerNode()) {
            throw new ConvertException("Jackson container node.");
        }
        return Converters.doubleValue(json);
    }

    @Override
    public Double doubleValue(int scale) {
        if (json.isContainerNode()) {
            throw new ConvertException("Jackson container node.");
        }
        return Converters.doubleValue(json, scale);
    }

    @Override
    public BigDecimal decimalValue(BigDecimal def) {
        return decimalValue();
    }

    @Override
    public BigDecimal decimalValue() {
        if (json.isContainerNode()) {
            throw new ConvertException("Jackson container node.");
        }
        return Converters.decimalValue(json);
    }

    @Override
    public <E extends Enum<E>> E enumValue(Class<E> enumClass) {
        if (json.isContainerNode()) {
            throw new ConvertException("Jackson container node.");
        }
        return Converters.def().convert(json, enumClass);
    }

}
