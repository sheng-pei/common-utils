package ppl.common.utils.config.list;

import ppl.common.utils.config.AbstractNode;
import ppl.common.utils.config.Node;
import ppl.common.utils.config.Nodes;
import ppl.common.utils.config.NullNode;

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
        return new NullNode(path);
    }

    @Override
    public Node getChild(Integer index) {
        String path = childPath(index);

        if (index >= this.list.size()) {
            return new NullNode(path);
        }

        return Nodes.createByPath(path, this.list.get(index));
    }

    @Override
    public Iterator<Node> iterator() {
        return new Iter();
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
