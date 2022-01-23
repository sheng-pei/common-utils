package ppl.common.utils.config.map;

import ppl.common.utils.config.AbstractNode;
import ppl.common.utils.config.Node;
import ppl.common.utils.config.Nodes;
import ppl.common.utils.config.NullNode;

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
        return Nodes.createByPath(path, this.map.get(fieldName));
    }

    @Override
    public Node getChild(Integer index) {
        String path = childPath(index);
        return new NullNode(path);
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
