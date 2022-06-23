package ppl.common.utils.config.nodes.scalar;

import ppl.common.utils.config.nodes.AbstractNode;
import ppl.common.utils.config.nodes.MissingNode;
import ppl.common.utils.config.Node;
import ppl.common.utils.config.convert.Converters;

import java.util.Collections;
import java.util.Iterator;

public final class ScalarNode extends AbstractNode {

    private final Object scalar;

    ScalarNode(String path, Object scalar) {
        super(path);
        this.scalar = scalar;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Node getChild(String fieldName) {
        return new MissingNode(childPath(fieldName));
    }

    @Override
    public Node getChild(Integer index) {
        return new MissingNode(childPath(index));
    }

    @Override
    public Iterator<Node> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    public String textValue(String def) {
        return textValue();
    }

    @Override
    public String textValue() {
        return Converters.stringValue(scalar);
    }

    @Override
    public Byte byteValue(Byte def) {
        return byteValue();
    }

    @Override
    public Byte byteValue() {
        return Converters.byteValue(scalar);
    }

    @Override
    public Short shortValue(Short def) {
        return shortValue();
    }

    @Override
    public Short shortValue() {
        return Converters.shortValue(scalar);
    }

    @Override
    public Integer intValue(Integer def) {
        return intValue();
    }

    @Override
    public Integer intValue() {
        return Converters.intValue(scalar);
    }

    @Override
    public Long longValue(Long def) {
        return longValue();
    }

    @Override
    public Long longValue() {
        return Converters.longValue(scalar);
    }

    @Override
    public Boolean boolValue(Boolean def) {
        return boolValue();
    }

    @Override
    public Boolean boolValue() {
        return Converters.boolValue(scalar);
    }

    @Override
    public Double doubleValue(Double def) {
        return doubleValue();
    }

    @Override
    public Double doubleValue() {
        return Converters.doubleValue(scalar);
    }

    @Override
    public <E extends Enum<E>> E enumValue(Class<E> enumClass) {
        return Converters.convert(scalar, enumClass);
    }

}
