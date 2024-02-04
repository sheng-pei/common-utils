package ppl.common.utils.config.nodes.list;

import ppl.common.utils.config.*;
import ppl.common.utils.config.nodes.AbstractNode;
import ppl.common.utils.config.nodes.MissingNode;
import ppl.common.utils.config.nodes.iterator.ArrayIterator;

import java.util.Iterator;
import java.util.List;

public final class ListNode extends AbstractNode {

    private final List<?> list;

    ListNode(String path, List<?> list) {
        super(path);
        this.list = list;
    }

    @Override
    public boolean isContainer() {
        return true;
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public Node getChild(String fieldName) {
        String path = childPath(fieldName);
        return new MissingNode(path);
    }

    @Override
    public Node getChild(Integer index) {
        String path = childPath(index);

        if (index >= this.list.size()) {
            return new MissingNode(path);
        }

        return Nodes.createByPath(path, this.list.get(index));
    }

    @Override
    public Iterator<Node> iterator() {
        return new ArrayIterator(this.list.iterator(), this::childPath);
    }

    @Override
    public String textValue(String def) {
        throw new ConvertException("Container node");
    }

    @Override
    public String textValue() {
        throw new ConvertException("Container node");
    }

    @Override
    public Byte byteValue(Byte def) {
        throw new ConvertException("Container node");
    }

    @Override
    public Byte byteValue() {
        throw new ConvertException("Container node");
    }

    @Override
    public Short shortValue(Short def) {
        throw new ConvertException("Container node");
    }

    @Override
    public Short shortValue() {
        throw new ConvertException("Container node");
    }

    @Override
    public Integer intValue(Integer def) {
        throw new ConvertException("Container node");
    }

    @Override
    public Integer intValue() {
        throw new ConvertException("Container node");
    }

    @Override
    public Long longValue(Long def) {
        throw new ConvertException("Container node");
    }

    @Override
    public Long longValue() {
        throw new ConvertException("Container node");
    }

    @Override
    public Boolean boolValue(Boolean def) {
        throw new ConvertException("Container node");
    }

    @Override
    public Boolean boolValue() {
        throw new ConvertException("Container node");
    }

    @Override
    public Double doubleValue(Double def) {
        throw new ConvertException("Container node");
    }

    @Override
    public Double doubleValue() {
        throw new ConvertException("Container node");
    }

    @Override
    public <E extends Enum<E>> E enumValue(Class<E> enumClass) {
        throw new ConvertException("Container node");
    }

}
