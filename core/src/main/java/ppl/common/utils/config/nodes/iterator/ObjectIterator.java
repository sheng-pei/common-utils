package ppl.common.utils.config.nodes.iterator;

import ppl.common.utils.config.Node;
import ppl.common.utils.config.NodeException;
import ppl.common.utils.config.Nodes;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

public class ObjectIterator implements ConfigIterator {
    private final Iterator<? extends Map.Entry<?, ?>> iter;
    private final Function<String, String> childPathCreator;

    public ObjectIterator(Iterator<? extends Map.Entry<?, ?>> iter, Function<String, String> childPathCreator) {
        this.iter = iter;
        this.childPathCreator = childPathCreator;
    }

    @Override
    public boolean hasNext() {
        return this.iter.hasNext();
    }

    @Override
    public Node next() {
        Map.Entry<?, ?> entry = this.iter.next();
        if (!(entry.getKey() instanceof String)) {
            throw new NodeException("Non-string fieldName is unsupported.");
        }

        String path;
        try {
            path = childPathCreator.apply((String) entry.getKey());
        } catch (IllegalArgumentException e) {
            throw new NodeException("Invalid fieldName: " + entry.getKey(), e);
        }

        try {
            return Nodes.createByPath(path, entry.getValue());
        } catch (IllegalArgumentException e) {
            throw new NodeException("Unknown value of '" + path + "'.", e);
        }
    }
}
