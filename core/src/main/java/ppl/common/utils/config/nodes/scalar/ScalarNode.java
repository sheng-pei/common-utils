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
            add(char.class);
            add(Character.class);
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

    public static boolean isScalar(Object object) {
        return object != null && BASE_TYPES.contains(object.getClass());
    }

    private final Object scalar;

    ScalarNode(String path, Object scalar) {
        super(path);
        this.scalar = scalar;
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
    public int size() {
        throw new UnsupportedOperationException("Not an iterable node.");
    }

    @Override
    public Iterator<Node> iterator() {
        throw new UnsupportedOperationException("Not an iterable node.");
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
    public BigInteger bigintValue(BigInteger def) {
        return bigintValue();
    }

    @Override
    public BigInteger bigintValue() {
        return Converters.bigintValue(scalar);
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
    public Float floatValue(Float def) {
        return floatValue();
    }

    @Override
    public Float floatValue() {
        return Converters.floatValue(scalar);
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
    public Double doubleValue(int scale) {
        return Converters.doubleValue(scalar, scale);
    }

    @Override
    public BigDecimal decimalValue(BigDecimal def) {
        return decimalValue();
    }

    @Override
    public BigDecimal decimalValue() {
        return Converters.decimalValue(scalar);
    }

    @Override
    public <E extends Enum<E>> E enumValue(Class<E> enumClass) {
        return Converters.def().convert(scalar, enumClass);
    }
}
