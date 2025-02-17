package ppl.common.utils.config.nodes.iterator;

import ppl.common.utils.config.Node;
import ppl.common.utils.config.NodeException;

import java.util.Iterator;

public interface ConfigIterator extends Iterator<Node> {

    /**
     *
     * Returns the next element in the iteration.
     * @return the next element in the iteration.
     * @throws NodeException invalid value come up against.
     */
    @Override
    Node next();
}
