package ppl.common.utils.config.nodes.scalar;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ppl.common.utils.config.Node;

import java.util.Iterator;

public class ScalarNodeTest {

    private static final ScalarNode SCALAR = new ScalarNode(Node.ROOT_PATH, "string");
    private static final ScalarNode NULL_SCALAR = new ScalarNode(Node.ROOT_PATH, null);

    @Test
    void isContainer() {
        Assertions.assertFalse(SCALAR.isContainer());
    }

    @Test
    void size() {
        Assertions.assertEquals(0, SCALAR.size());
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
        Iterator<Node> iter = SCALAR.iterator();
        Assertions.assertFalse(iter.hasNext());
    }

    @Test
    void textValueOnDefault() {
        Assertions.assertEquals("string", NULL_SCALAR.textValue("string"));
    }

    @Test
    void byteValueOnDefault() {
        Assertions.assertEquals((byte) 1, NULL_SCALAR.byteValue((byte) 1));
    }

    @Test
    void shortValueOnDefault() {
        Assertions.assertEquals((short) 1, NULL_SCALAR.shortValue((short) 1));
    }

    @Test
    void intValueOnDefault() {
        Assertions.assertEquals(1, NULL_SCALAR.intValue(1));
    }

    @Test
    void longValueOnDefault() {
        Assertions.assertEquals(1L, NULL_SCALAR.longValue(1L));
    }

    @Test
    void boolValueOnDefault() {
        Assertions.assertEquals(false, NULL_SCALAR.boolValue(false));
    }

    @Test
    void doubleValueOnDefault() {
        Assertions.assertEquals(1.0, NULL_SCALAR.doubleValue(1.0));
    }
}
