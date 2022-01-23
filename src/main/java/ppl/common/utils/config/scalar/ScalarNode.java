package ppl.common.utils.config.scalar;

import ppl.common.utils.config.AbstractNode;
import ppl.common.utils.config.Node;
import ppl.common.utils.config.NullNode;

import java.util.Collections;
import java.util.Iterator;

public final class ScalarNode extends AbstractNode {

    private final Object scalar;

    public ScalarNode(Object scalar) {
        super();
        this.scalar = scalar;
    }

    public ScalarNode(String path, Object scalar) {
        super(path);
        this.scalar = scalar;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Node getChild(String fieldName) {
        return new NullNode(childPath(fieldName));
    }

    @Override
    public Node getChild(Integer index) {
        return new NullNode(childPath(index));
    }

    @Override
    public Iterator<Node> iterator() {
        return Collections.emptyIterator();
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

}
