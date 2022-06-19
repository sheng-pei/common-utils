package ppl.common.utils.config.nodes.scalar;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ppl.common.utils.config.Node;
import ppl.common.utils.config.Nodes;

import java.util.Iterator;

public class ScalarNodeTest {

    private static final Node SCALAR = Nodes.root("string");

    @Test
    void isContainer() {
        Assertions.assertFalse(SCALAR.isContainer());
    }

    @Test
    void size() {
        Assertions.assertEquals(0, SCALAR.size());
    }

    @Test
    void getChildByFieldName() {
        Node child = SCALAR.getChild("a");
        Assertions.assertTrue(child.isMissing());
        Assertions.assertEquals(".a", child.path());

        child = child.getChild("a.b");
        Assertions.assertEquals(".a.{a.b}", child.path());
    }

    @Test
    void getChildByIndex() {
        Node child = SCALAR.getChild(1);
        Assertions.assertTrue(child.isMissing());
        Assertions.assertEquals(".[1]", child.path());

        child = child.getChild(1);
        Assertions.assertEquals(".[1].[1]", child.path());
    }

    @Test
    void iterator() {
        Iterator<Node> iter = SCALAR.iterator();
        Assertions.assertFalse(iter.hasNext());
    }

    @Test
    void textValue() {
    }

    @Test
    void testTextValue() {
    }

    @Test
    void byteValue() {
    }

    @Test
    void testByteValue() {
    }

    @Test
    void shortValue() {
    }

    @Test
    void testShortValue() {
    }

    @Test
    void intValue() {
    }

    @Test
    void testIntValue() {
    }

    @Test
    void longValue() {
    }

    @Test
    void testLongValue() {
    }

    @Test
    void boolValue() {
    }

    @Test
    void testBoolValue() {
    }

    @Test
    void doubleValue() {
    }

    @Test
    void testDoubleValue() {
    }

    @Test
    void enumValue() {
    }
}
