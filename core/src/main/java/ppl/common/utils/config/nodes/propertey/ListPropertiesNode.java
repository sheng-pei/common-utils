package ppl.common.utils.config.nodes.propertey;

import ppl.common.utils.config.Node;
import ppl.common.utils.config.NodeException;
import ppl.common.utils.config.Nodes;
import ppl.common.utils.config.nodes.MissingNode;
import ppl.common.utils.config.nodes.iterator.ConfigIterator;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ListPropertiesNode extends ValuePropertiesNode {

    private final List<Property> list;

    ListPropertiesNode(String path, Object value, List<Property> list) {
        super(path, value);
        this.list = list;
    }

    @Override
    public int size() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Node getChild(Integer index) {
        String path = childPath(index);

        if (this.list == null || index >= this.list.size()) {
            return new MissingNode(path);
        }

        try {
            return this.list.get(index).toNode(path);
        } catch (RuntimeException e) {
            throw new NodeException("Unknown value of '" + path + "'.", e);
        }
    }

    @Override
    public Iterator<Node> iterator() {
        if (list == null) {
            return Collections.emptyIterator();
        }
        return new ConfigIterator() {

            private int cursor = 0;
            private final Iterator<Property> iter = list.iterator();

            @Override
            public Node next() {
                Property prop =  iter.next();
                String path = childPath(cursor ++);

                try {
                    return prop.toNode(path);
                } catch (RuntimeException e) {
                    throw new NodeException("Invalid property node.", e);
                }
            }

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }
        };
    }
}
