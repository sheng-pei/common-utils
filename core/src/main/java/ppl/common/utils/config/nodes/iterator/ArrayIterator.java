package ppl.common.utils.config.nodes.iterator;

import ppl.common.utils.config.Node;
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
        try {
            Object ele = iter.next();
            return Nodes.createByPath(childPathCreator.apply(this.cursor ++), ele);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("No such element: " + childPathCreator.apply(this.cursor));
        }
    }
}
