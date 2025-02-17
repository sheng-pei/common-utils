package ppl.common.utils.config.nodes.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import ppl.common.utils.config.*;
import ppl.common.utils.config.convert.Converters;
import ppl.common.utils.config.nodes.AbstractNode;
import ppl.common.utils.config.nodes.iterator.ArrayIterator;
import ppl.common.utils.config.nodes.iterator.ConfigIterator;
import ppl.common.utils.config.nodes.iterator.ObjectIterator;

import java.math.BigDecimal;
import java.math.BigInteger;

public class JacksonNode extends AbstractNode {

    private final JsonNode json;

    JacksonNode(String path, JsonNode json) {
        super(path);
        this.json = json;
    }

    @Override
    public boolean isMissing() {
        return json.isMissingNode();
    }

    @Override
    public boolean isContainer() {
        return json.isObject() || json.isArray();
    }

    @Override
    public boolean isValue() {
        return json.isValueNode();
    }

    @Override
    public Node getChild(String fieldName) {
        String path = childPath(fieldName);
        JsonNode node = json.path(fieldName);
        try {
            return Nodes.createByPath(path, node);
        } catch (RuntimeException e) {
            throw new NodeException("Unknown value of '" + path + "'.", e);
        }
    }

    @Override
    public Node getChild(Integer index) {
        String path = childPath(index);
        JsonNode node = json.path(index);
        try {
            return Nodes.createByPath(path, node);
        } catch (RuntimeException e) {
            throw new NodeException("Unknown value of '" + path + "'.", e);
        }
    }

    @Override
    public int size() {
        if (!isContainer()) {
            throw new UnsupportedOperationException("Not an iterable node.");
        }
        return json.size();
    }

    @Override
    public ConfigIterator iterator() {
        if (this.json.isArray()) {
            return new ArrayIterator(this.json.iterator(), this::childPath);
        } else if (this.json.isObject()) {
            return new ObjectIterator(this.json.fields(), this::childPath);
        }
        throw new UnsupportedOperationException("Not an iterable node.");
    }

    @Override
    public String textValue(String def) {
        return textValue();
    }

    @Override
    public String textValue() {
        if (!isValue()) {
            throw new ConvertException("Not a value node.");
        }
        return Converters.stringValue(json);
    }

    @Override
    public Byte byteValue(Byte def) {
        return byteValue();
    }

    @Override
    public Byte byteValue() {
        if (!isValue()) {
            throw new ConvertException("Not a value node.");
        }
        return Converters.byteValue(json);
    }

    @Override
    public Short shortValue(Short def) {
        return shortValue();
    }

    @Override
    public Short shortValue() {
        if (!isValue()) {
            throw new ConvertException("Not a value node.");
        }
        return Converters.shortValue(json);
    }

    @Override
    public Integer intValue(Integer def) {
        return intValue();
    }

    @Override
    public Integer intValue() {
        if (!isValue()) {
            throw new ConvertException("Not a value node.");
        }
        return Converters.intValue(json);
    }

    @Override
    public Long longValue(Long def) {
        return longValue();
    }

    @Override
    public Long longValue() {
        if (!isValue()) {
            throw new ConvertException("Not a value node.");
        }
        return Converters.longValue(json);
    }

    @Override
    public BigInteger bigintValue(BigInteger def) {
        return bigintValue();
    }

    @Override
    public BigInteger bigintValue() {
        if (!isValue()) {
            throw new ConvertException("Not a value node.");
        }
        return Converters.bigintValue(json);
    }

    @Override
    public Boolean boolValue(Boolean def) {
        return boolValue();
    }

    @Override
    public Boolean boolValue() {
        if (!isValue()) {
            throw new ConvertException("Not a value node.");
        }
        return Converters.boolValue(json);
    }

    @Override
    public Float floatValue(Float def) {
        return floatValue();
    }

    @Override
    public Float floatValue() {
        if (!isValue()) {
            throw new ConvertException("Not a value node.");
        }
        return Converters.floatValue(json);
    }

    @Override
    public Double doubleValue(Double def) {
        return doubleValue();
    }

    @Override
    public Double doubleValue() {
        if (!isValue()) {
            throw new ConvertException("Not a value node.");
        }
        return Converters.doubleValue(json);
    }

    @Override
    public Double doubleValue(int scale) {
        if (!isValue()) {
            throw new ConvertException("Not a value node.");
        }
        return Converters.doubleValue(json, scale);
    }

    @Override
    public BigDecimal decimalValue(BigDecimal def) {
        return decimalValue();
    }

    @Override
    public BigDecimal decimalValue() {
        if (!isValue()) {
            throw new ConvertException("Not a value node.");
        }
        return Converters.decimalValue(json);
    }

    @Override
    public <E extends Enum<E>> E enumValue(Class<E> enumClass) {
        if (!isValue()) {
            throw new ConvertException("Not a value node.");
        }
        return Converters.def().convert(json, enumClass);
    }

}
