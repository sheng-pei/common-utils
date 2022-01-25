package ppl.common.utils.config.list;

import ppl.common.utils.config.*;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public final class ListNode extends AbstractNode {

    private final List<?> list;

    public ListNode(List<?> list) {
        super();
        this.list = list;
    }

    public ListNode(String path, List<?> list) {
        super(path);
        this.list = list;
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
        return new Iter();
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
    public Double doubleValue() {
        throw new ConvertException("Container node");
    }

    @Override
    public Double doubleValue(Double def) {
        throw new ConvertException("Container node");
    }

    @Override
    public <E extends Enum<E>> E enumValue(Class<E> enumClass) {
        throw new ConvertException("Container node");
    }

    private class Iter implements Iterator<Node> {

        private int cursor = 0;
        private Iterator<?> iter;

        private Iter() {
            this.iter = ListNode.this.iterator();
        }

        @Override
        public boolean hasNext() {
            return this.iter.hasNext();
        }

        @Override
        public Node next() {
            try {
                Object ele = iter.next();
                return Nodes.createByPath(ListNode.this.childPath(this.cursor ++), ele);
            } catch (NoSuchElementException e) {
                throw new NoSuchElementException("No such element: " + ListNode.this.childPath(this.cursor));
            }
        }

    }

}
