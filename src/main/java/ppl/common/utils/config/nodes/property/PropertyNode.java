package ppl.common.utils.config.nodes.property;

import ppl.common.utils.config.Node;
import ppl.common.utils.config.nodes.AbstractValueNode;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class PropertyNode extends AbstractValueNode {

//    private final Map<String, Node> children;

    protected PropertyNode(String path, Properties properties) {
        super(path, null);
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Node getChild(String fieldName) {
        return null;
    }

    @Override
    public Node getChild(Integer index) {
        return null;
    }

    @Override
    public Iterator<Node> iterator() {
        return null;
    }

    @Override
    public String textValue(String def) {
        return null;
    }

    @Override
    public String textValue() {
        return null;
    }

    @Override
    public Byte byteValue(Byte def) {
        return null;
    }

    @Override
    public Byte byteValue() {
        return null;
    }

    @Override
    public Short shortValue(Short def) {
        return null;
    }

    @Override
    public Short shortValue() {
        return null;
    }

    @Override
    public Integer intValue(Integer def) {
        return null;
    }

    @Override
    public Integer intValue() {
        return null;
    }

    @Override
    public Long longValue(Long def) {
        return null;
    }

    @Override
    public Long longValue() {
        return null;
    }

    @Override
    public Boolean boolValue(Boolean def) {
        return null;
    }

    @Override
    public Boolean boolValue() {
        return null;
    }

    @Override
    public Double doubleValue(Double def) {
        return null;
    }

    @Override
    public Double doubleValue() {
        return null;
    }

    @Override
    public <E extends Enum<E>> E enumValue(Class<E> enumClass) {
        return null;
    }
}
