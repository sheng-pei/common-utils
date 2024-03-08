package ppl.common.utils.config.nodes.scalar;

import ppl.common.utils.config.Node;
import ppl.common.utils.config.convert.Converters;
import ppl.common.utils.config.nodes.AbstractNode;
import ppl.common.utils.config.nodes.MissingNode;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class ScalarNode extends AbstractNode {

    private static final Set<Class<?>> BASE_TYPES = new HashSet<Class<?>>() {
        {
            add(boolean.class);
            add(Boolean.class);
            add(byte.class);
            add(Byte.class);
            add(short.class);
            add(Short.class);
            add(int.class);
            add(Integer.class);
            add(long.class);
            add(Long.class);
            add(BigInteger.class);
            add(BigDecimal.class);
            add(float.class);
            add(Float.class);
            add(double.class);
            add(Double.class);
            add(String.class);
        }
    };

    private final Object scalar;

    ScalarNode(String path, Object scalar) {
        super(path);
        if (scalar == null || !BASE_TYPES.contains(scalar.getClass())) {
            throw new IllegalArgumentException("Only base type is allowed.");
        }
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
