package ppl.common.utils.config.nodes.list;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ppl.common.utils.config.ConvertException;
import ppl.common.utils.config.Node;

import java.util.*;

class ListNodeTest {

    private static final ListNode LIST_NODE = new ListNode(Node.ROOT_PATH, new ArrayList<Object>() {
        {
            this.add("u");
            this.add("v");
        }
    });

    @Test
    void isContainer() {
        Assertions.assertTrue(LIST_NODE.isContainer());
    }

    @Test
    void size() {
        Assertions.assertEquals(2, LIST_NODE.size());
    }

    @Test
    void getChildOnFieldName() {
        Assertions.assertTrue(LIST_NODE.getChild("aaa").isMissing());
    }

    @Test
    void getChildOnIndex() {
        Node node = LIST_NODE.getChild(0);
        Assertions.assertEquals("u", node.textValue());
    }

    @Test
    void getNoChildOnIndex() {
        Node node = LIST_NODE.getChild(9);
        Assertions.assertTrue(node.isMissing());
    }

    @Test
    void iterator() {
        List<Object> actual = new ArrayList<>();
        for (Node node : LIST_NODE) {
            actual.add(node.textValue());
        }

        Assertions.assertArrayEquals(new Object[] {"u", "v"}, actual.toArray());
    }

    @Test
    void textValue() {
        Assertions.assertThrows(ConvertException.class, LIST_NODE::textValue);
    }

    @Test
    void byteValue() {
        Assertions.assertThrows(ConvertException.class, LIST_NODE::byteValue);
    }

    @Test
    void shortValue() {
        Assertions.assertThrows(ConvertException.class, LIST_NODE::shortValue);
    }

    @Test
    void intValue() {
        Assertions.assertThrows(ConvertException.class, LIST_NODE::intValue);
    }

    @Test
    void longValue() {
        Assertions.assertThrows(ConvertException.class, LIST_NODE::longValue);
    }

    @Test
    void boolValue() {
        Assertions.assertThrows(ConvertException.class, LIST_NODE::boolValue);
    }

    @Test
    void doubleValue() {
        Assertions.assertThrows(ConvertException.class, LIST_NODE::doubleValue);
    }

    private enum Standard {
        A, B, C
    }

    @Test
    void enumValue() {
        Assertions.assertThrows(ConvertException.class, () -> LIST_NODE.enumValue(Standard.class));
    }
}