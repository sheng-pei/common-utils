package ppl.common.utils.config;

import java.util.Collections;
import java.util.Iterator;

public final class NullNode extends AbstractNode {

    public NullNode() {
        super();
    }

    public NullNode(String path) {
        super(path);
    }

    @Override
    public boolean isRoot() {
        return false;
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
    public byte[] binaryValue() {
        return new byte[0];
    }

    @Override
    public byte[] binaryValue(byte[] def) {
        return new byte[0];
    }

    @Override
    public String textValue(String def) {
        return def;
    }

    @Override
    public String textValue() {
        return null;
    }

    @Override
    public Byte byteValue(Byte def) {
        return def;
    }

    @Override
    public Byte byteValue() {
        return null;
    }

    @Override
    public Short shortValue(Short def) {
        return def;
    }

    @Override
    public Short shortValue() {
        return null;
    }

    @Override
    public Integer intValue(Integer def) {
        return def;
    }

    @Override
    public Integer intValue() {
        return null;
    }

    @Override
    public Long longValue(Long def) {
        return def;
    }

    @Override
    public Long longValue() {
        return null;
    }

    @Override
    public Boolean boolValue(Boolean def) {
        return def;
    }

    @Override
    public Boolean boolValue() {
        return null;
    }

    @Override
    public Double doubleValue() {
        return null;
    }

    @Override
    public Double doubleValue(Double def) {
        return def;
    }

    @Override
    public <E extends Enum<E>> E enumValue(Class<E> enumClass) {
        return null;
    }
}
