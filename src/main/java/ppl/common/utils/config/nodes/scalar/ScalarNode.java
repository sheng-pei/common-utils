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
    public byte[] binaryValue(byte[] def) {
        return new byte[0];
    }

    @Override
    public byte[] binaryValue() {
        return new byte[0];
    }

    @Override
    public String textValue(String def) {
        String res = textValue();
        return res == null ? def : res;
    }

    @Override
    public String textValue() {
        return Converters.convert(scalar, String.class);
    }

    @Override
    public Byte byteValue(Byte def) {
        Byte res = byteValue();
        return res == null ? def : res;
    }

    @Override
    public Byte byteValue() {
        return Converters.convert(scalar, Byte.class);
    }

    @Override
    public Short shortValue(Short def) {
        Short res = shortValue();
        return res == null ? def : res;
    }

    @Override
    public Short shortValue() {
        return Converters.convert(scalar, Short.class);
    }

    @Override
    public Integer intValue(Integer def) {
        Integer res = intValue();
        return res == null ? def : res;
    }

    @Override
    public Integer intValue() {
        return Converters.convert(scalar, Integer.class);
    }

    @Override
    public Long longValue(Long def) {
        Long res = longValue();
        return res == null ? def : res;
    }

    @Override
    public Long longValue() {
        return Converters.convert(scalar, Long.class);
    }

    @Override
    public Boolean boolValue(Boolean def) {
        Boolean res = boolValue();
        return res == null ? def : res;
    }

    @Override
    public Boolean boolValue() {
        return Converters.convert(scalar, Boolean.class);
    }

    @Override
    public Double doubleValue(Double def) {
        Double res = doubleValue();
        return res == null ? def : res;
    }

    @Override
    public Double doubleValue() {
        return Converters.convert(scalar, Double.class);
    }

    @Override
    public <E extends Enum<E>> E enumValue(Class<E> enumClass) {
        return Converters.convert(scalar, enumClass);
    }

}
