package ppl.common.utils.config.nodes.propertey;

import ppl.common.utils.config.Node;
import ppl.common.utils.config.NodeException;
import ppl.common.utils.config.nodes.MissingNode;
import ppl.common.utils.config.nodes.iterator.ConfigIterator;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public class MapPropertiesNode extends ValuePropertiesNode {

    private final Map<String, Property> map;

    MapPropertiesNode(String path, Object value, Map<String, Property> map) {
        super(path, value);
        this.map = map;
    }

    @Override
    public Node getChild(String fieldName) {
        String path = childPath(fieldName);

        if (this.map == null || !this.map.containsKey(fieldName)) {
            return new MissingNode(path);
        }

        try {
            return this.map.get(fieldName).toNode(path);
        } catch (RuntimeException e) {
            throw new NodeException("Unknown value of '" + path + "'.", e);
        }
    }

    @Override
    public int size() {
        return map == null ? 0 : map.size();
    }

    @Override
    public Iterator<Node> iterator() {
        if (map == null) {
            return Collections.emptyIterator();
        }
        return new ConfigIterator() {

            private final Iterator<? extends Map.Entry<String, Property>> iter = map.entrySet().iterator();

            @Override
            public Node next() {
                Map.Entry<String, Property> entry = this.iter.next();

                String path = childPath(entry.getKey());
                try {
                    return entry.getValue().toNode(path);
                } catch (IllegalArgumentException e) {
                    throw new NodeException("Unknown value of '" + path + "'.", e);
                }
            }

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }
        };
    }
}
