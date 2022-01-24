package ppl.common.utils.config.map;

import ppl.common.utils.config.*;

import java.util.*;

public final class MapNode extends AbstractNode {

    private final Map<?, ?> map;

    public MapNode(Map<?, ?> map) {
        super();
        this.map = map;
    }

    public MapNode(String path, Map<?, ?> map) {
        super(path);
        this.map = map;
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public Node getChild(String fieldName) {
        String path = childPath(fieldName);
        if (!this.map.containsKey(fieldName)) {
            return new MissingNode(path);
        }
        return Nodes.createByPath(path, this.map.get(fieldName));
    }

    @Override
    public Node getChild(Integer index) {
        String path = childPath(index);
        return new MissingNode(path);
    }

    @Override
    public Iterator<Node> iterator() {
        return new Iter();
    }

    @Override
    public String textValue(String def) {
        throw new UnsupportedOperationException("Container node");
    }

    @Override
    public String textValue() {
        throw new UnsupportedOperationException("Container node");
    }

    @Override
    public Byte byteValue(Byte def) {
        throw new UnsupportedOperationException("Container node");
    }

    @Override
    public Byte byteValue() {
        throw new UnsupportedOperationException("Container node");
    }

    @Override
    public Short shortValue(Short def) {
        throw new UnsupportedOperationException("Container node");
    }

    @Override
    public Short shortValue() {
        throw new UnsupportedOperationException("Container node");
    }

    @Override
    public Integer intValue(Integer def) {
        throw new UnsupportedOperationException("Container node");
    }

    @Override
    public Integer intValue() {
        throw new UnsupportedOperationException("Container node");
    }

    @Override
    public Long longValue(Long def) {
        throw new UnsupportedOperationException("Container node");
    }

    @Override
    public Long longValue() {
        throw new UnsupportedOperationException("Container node");
    }

    @Override
    public Boolean boolValue(Boolean def) {
        throw new UnsupportedOperationException("Container node");
    }

    @Override
    public Boolean boolValue() {
        throw new UnsupportedOperationException("Container node");
    }

    @Override
    public Double doubleValue() {
        throw new UnsupportedOperationException("Container node");
    }

    @Override
    public Double doubleValue(Double def) {
        throw new UnsupportedOperationException("Container node");
    }

    @Override
    public <E extends Enum<E>> E enumValue(Class<E> enumClass) {
        throw new UnsupportedOperationException("Container node");
    }

    private class Iter implements Iterator<Node> {

        private final Iterator<Map.Entry<?, ?>> iter;

        private Iter() {
            @SuppressWarnings({"rawtypes", "unchecked"})
            Set<Map.Entry<?, ?>> entries = (Set) MapNode.this.map.entrySet();
            this.iter = entries.iterator();
        }

        @Override
        public boolean hasNext() {
            return this.iter.hasNext();
        }

        @Override
        public Node next() {
            Map.Entry<?, ?> entry = this.iter.next();
            if (!(entry.getKey() instanceof String)) {
                throw new IllegalStateException("Non-string fieldName is unsupported.");
            }

            String path;
            try {
                path = childPath((String) entry.getKey());
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }

            try {
                return Nodes.createByPath(path, entry.getValue());
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Invalid config: " + path, e);
            }
        }

    }

}
