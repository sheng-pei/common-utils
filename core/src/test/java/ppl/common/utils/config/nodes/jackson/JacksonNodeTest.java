package ppl.common.utils.config.nodes.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ppl.common.utils.config.ConvertException;
import ppl.common.utils.config.Node;

import java.util.*;

class JacksonNodeTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final JacksonNode MAP;
    private static final JacksonNode LIST;
    private static final JacksonNode IN_BYTE;
    private static final JacksonNode IN_SHORT;
    private static final JacksonNode IN_INT;
    private static final JacksonNode IN_LONG;
    private static final JacksonNode DOUBLE;
    private static final JacksonNode BOOL;
    private static final JacksonNode TEXT;
    private static final JacksonNode ENUM;

    static {
        JacksonNode map = null;
        JacksonNode list = null;
        JacksonNode inByte = null;
        JacksonNode inShort = null;
        JacksonNode inInt = null;
        JacksonNode inLong = null;
        JacksonNode dbl = null;
        JacksonNode bool = null;
        JacksonNode text = null;
        JacksonNode em = null;
        try {
            map = new JacksonNode(Node.ROOT_PATH, mapper.readTree("{\"a\": 1}"));
            list = new JacksonNode(Node.ROOT_PATH, mapper.readTree("[1,2]"));
            inByte = new JacksonNode(Node.ROOT_PATH, new IntNode(1));
            inShort = new JacksonNode(Node.ROOT_PATH, new IntNode(256));
            inInt = new JacksonNode(Node.ROOT_PATH, new IntNode(Short.MAX_VALUE + 1));
            dbl = new JacksonNode(Node.ROOT_PATH, new DoubleNode(1.0));
            inLong = new JacksonNode(Node.ROOT_PATH, new LongNode(Integer.MAX_VALUE + 1L));
            bool = new JacksonNode(Node.ROOT_PATH, BooleanNode.valueOf(false));
            text = new JacksonNode(Node.ROOT_PATH, new TextNode("text"));
            em = new JacksonNode(Node.ROOT_PATH, new TextNode("A"));
        } catch (JsonProcessingException e) {
            //ignore
        }
        MAP = map;
        LIST = list;
        IN_BYTE = inByte;
        IN_SHORT = inShort;
        IN_INT = inInt;
        IN_LONG = inLong;
        DOUBLE = dbl;
        BOOL = bool;
        TEXT = text;
        ENUM = em;
    }

    @Test
    void isContainer() {
        Assertions.assertTrue(MAP.isContainer());
        Assertions.assertTrue(LIST.isContainer());
        Assertions.assertFalse(TEXT.isContainer());
    }

    @Test
    void size() {
        Assertions.assertEquals(1, MAP.size());
        Assertions.assertEquals(2, LIST.size());
        Assertions.assertEquals(0, TEXT.size());
    }

    @Test
    void getChildOnFieldName() {
        Assertions.assertFalse(MAP.getChild("a").isMissing());
        Assertions.assertTrue(MAP.getChild("b").isMissing());
        Assertions.assertTrue(LIST.getChild("a").isMissing());
        Assertions.assertTrue(IN_BYTE.getChild("1").isMissing());
    }

    @Test
    void getChildOnIndex() {
        Assertions.assertFalse(LIST.getChild(0).isMissing());
        Assertions.assertTrue(MAP.getChild(0).isMissing());
        Assertions.assertTrue(LIST.getChild(4).isMissing());
        Assertions.assertTrue(IN_BYTE.getChild(0).isMissing());
    }

    @Test
    void iteratorMap() {
        Set<Integer> s = new HashSet<>();
        for (Node n : MAP) {
            s.add(n.intValue());
        }

        Set<Integer> actual = new HashSet<>();
        actual.add(1);
        Assertions.assertEquals(actual, s);
    }

    @Test
    void iteratorList() {
        List<Integer> a = new ArrayList<>();
        for (Node n : LIST) {
            a.add(n.intValue());
        }
        Assertions.assertArrayEquals(new Integer[] {1, 2}, a.toArray());
    }

    @Test
    void iteratorNotContainer() {
        Assertions.assertFalse(BOOL.iterator().hasNext());
    }

    @Test
    void textValue() {
        Assertions.assertEquals("text", TEXT.textValue());
    }

    @Test
    void textValueExceptionCausedByContainer() {
        Assertions.assertThrows(ConvertException.class, MAP::textValue);
    }

    @Test
    void byteValue() {
        Assertions.assertEquals((byte) 1, IN_BYTE.byteValue());
    }

    @Test
    void byteValueExceptionCausedByContainer() {
        Assertions.assertThrows(ConvertException.class, LIST::byteValue);
    }

    @Test
    void shortValue() {
        Assertions.assertEquals((short) 256, IN_SHORT.shortValue());
    }

    @Test
    void shortValueExceptionCausedByContainer() {
        Assertions.assertThrows(ConvertException.class, LIST::shortValue);
    }

    @Test
    void intValue() {
        Assertions.assertEquals(Short.MAX_VALUE + 1, IN_INT.intValue());
    }

    @Test
    void intValueExceptionCausedByContainer() {
        Assertions.assertThrows(ConvertException.class, MAP::intValue);
    }

    @Test
    void longValue() {
        Assertions.assertEquals(Integer.MAX_VALUE + 1L, IN_LONG.longValue());
    }

    @Test
    void longValueExceptionCausedByContainer() {
        Assertions.assertThrows(ConvertException.class, MAP::longValue);
    }

    @Test
    void boolValue() {
        Assertions.assertFalse(BOOL.boolValue());
    }

    @Test
    void boolValueExceptionCausedByContainer() {
        Assertions.assertThrows(ConvertException.class, MAP::boolValue);
    }

    @Test
    void doubleValue() {
        Assertions.assertEquals(1.0, DOUBLE.doubleValue());
    }

    @Test
    void doubleValueExceptionCausedByContainer() {
        Assertions.assertThrows(ConvertException.class, MAP::doubleValue);
    }

    private enum Standard {
        A, B, C;
    }

    @Test
    void enumValue() {
        Assertions.assertEquals(Standard.A, ENUM.enumValue(Standard.class));
    }

    @Test
    void enumValueExceptionCausedByContainer() {
        Assertions.assertThrows(ConvertException.class, () -> LIST.enumValue(Standard.class));
    }
}