package ppl.common.utils.config.nodes.iterator;

import ppl.common.utils.config.Node;
import ppl.common.utils.config.NodeException;
import ppl.common.utils.config.Nodes;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class ArrayIterator implements Iterator<Node> {
    private int cursor = 0;
    private final Iterator<?> iter;
    private final Function<Integer, String> childPathCreator;

    public ArrayIterator(Iterator<?> iter, Function<Integer, String> childPathCreator) {
        this.iter = iter;
        this.childPathCreator = childPathCreator;
    }

    @Override
    public boolean hasNext() {
        return this.iter.hasNext();
    }

    @Override
    public Node next() {
        String path = childPathCreator.apply(this.cursor);
        Object ele = iter.next();

        Node node;
        try {
            node = Nodes.createByPath(path, ele);
        } catch (RuntimeException e) {
            throw new NodeException("Unknown value of '" + path + "'.", e);
        }

        this.cursor ++;
        return node;
    }
}
