package ppl.common.utils.config.nodes.map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ppl.common.utils.config.ConvertException;
import ppl.common.utils.config.Node;

import java.util.*;

class MapNodeTest {

    private static final MapNode MAP_NODE = new MapNode(Node.ROOT_PATH, new HashMap<String, Object>() {{
        this.put("a.b", "u");
        this.put("a", "v");
    }});

    private static final MapNode INVALID_MAP_NODE = new MapNode(Node.ROOT_PATH, new HashMap<Object, Object>() {{
        this.put("a.b", "u");
        this.put(1, 1);
    }});

    @Test
    void isContainer() {
        Assertions.assertTrue(MAP_NODE.isContainer());
    }

    @Test
    void size() {
        Assertions.assertEquals(2, MAP_NODE.size());
    }

    @Test
    void getChildOnFieldName() {
        Node node = MAP_NODE.getChild("a.b");
        Assertions.assertEquals("u", node.textValue());
    }

    @Test
    void getNoChildOnFieldName() {
        Node node = MAP_NODE.getChild("kfj");
        Assertions.assertTrue(node.isMissing());
    }

    @Test
    void getChildOnFieldNameFromInvalidNode() {
        Node node = INVALID_MAP_NODE.getChild("a.b");
        Assertions.assertEquals("u", node.textValue());
    }

    @Test
    void getNoChildOnFieldNameFromInvalidNode() {
        Node node = INVALID_MAP_NODE.getChild("1");
        Assertions.assertTrue(node.isMissing());
    }

    @Test
    void getChildOnIndex() {
        Node node = MAP_NODE.getChild(1);
        Assertions.assertTrue(node.isMissing());
    }

    @Test
    void getChildOnIndexFromInvalidNode() {
        Node node = INVALID_MAP_NODE.getChild(1);
        Assertions.assertTrue(node.isMissing());
    }

    @Test
    void iterator() {
        Set<Object> actual = new HashSet<>();
        Iterator<Node> iter = MAP_NODE.iterator();
        while (iter.hasNext()) {
            Node node = iter.next();
            actual.add(node.textValue());
        }

        Set<Object> expected = new HashSet<>();
        expected.add("u");
        expected.add("v");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void iteratorException() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            Iterator<Node> iter = INVALID_MAP_NODE.iterator();
            while (iter.hasNext()) {
                iter.next();
            }
        });
    }

    @Test
    void textValue() {
        Assertions.assertThrows(ConvertException.class, MAP_NODE::textValue);
    }

    @Test
    void byteValue() {
        Assertions.assertThrows(ConvertException.class, MAP_NODE::byteValue);
    }

    @Test
    void shortValue() {
        Assertions.assertThrows(ConvertException.class, MAP_NODE::shortValue);
    }

    @Test
    void intValue() {
        Assertions.assertThrows(ConvertException.class, MAP_NODE::intValue);
    }

    @Test
    void longValue() {
        Assertions.assertThrows(ConvertException.class, MAP_NODE::longValue);
    }

    @Test
    void boolValue() {
        Assertions.assertThrows(ConvertException.class, MAP_NODE::boolValue);
    }

    @Test
    void doubleValue() {
        Assertions.assertThrows(ConvertException.class, MAP_NODE::doubleValue);
    }

    private enum Standard {
        A, B, C;
    }

    @Test
    void enumValue() {
        Assertions.assertThrows(ConvertException.class, () -> MAP_NODE.enumValue(Standard.class));
    }
}