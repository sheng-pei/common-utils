package ppl.common.utils.config.nodes.scalar;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ppl.common.utils.config.Node;

import java.util.Iterator;

public class ScalarNodeTest {

    private static final ScalarNode SCALAR = new ScalarNode(Node.ROOT_PATH, "string");

    @Test
    void isContainer() {
        Assertions.assertFalse(SCALAR.isContainer());
    }

    @Test
    void size() {
        Assertions.assertThrows(UnsupportedOperationException.class, SCALAR::size);
    }

    @Test
    void missingNode() {
        Assertions.assertTrue(SCALAR.getChild("a").isMissing());
        Assertions.assertTrue(SCALAR.getChild(1).isMissing());
    }

    @Test
    void getChildByFieldName() {
        Node child = SCALAR.getChild("a");
        Assertions.assertEquals(".a", child.path());

        child = child.getChild("a.b");
        Assertions.assertEquals(".a.{a.b}", child.path());
    }

    @Test
    void getChildByIndex() {
        Node child = SCALAR.getChild(1);
        Assertions.assertEquals(".[1]", child.path());

        child = child.getChild(1);
        Assertions.assertEquals(".[1].[1]", child.path());
    }

    @Test
    void iterator() {
        Assertions.assertThrows(UnsupportedOperationException.class, SCALAR::iterator);
    }
}
