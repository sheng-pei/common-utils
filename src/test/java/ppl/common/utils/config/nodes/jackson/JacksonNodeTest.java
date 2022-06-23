package ppl.common.utils.config.nodes.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JacksonNodeTest {

    @Test
    void isContainer() {
    }

    @Test
    void size() {
    }

    @Test
    void getChild() {
    }

    @Test
    void testGetChild() {
    }

    @Test
    void iterator() {
    }

    @Test
    void textValue() throws JsonProcessingException {
        String str = "{\"a\": null}";
        JsonMapper mapper = new JsonMapper();
        JsonNode root = mapper.readTree(str);
        System.out.println(root.get("a").isTextual());
        System.out.println(root.get("a").textValue());
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
    void doubleValue() throws JsonProcessingException {
        String string = "{\"a\": 1.5}";
        JsonMapper mapper = new JsonMapper();
        JsonNode node = mapper.readTree(string);
        System.out.println(node.get("a").isFloat());
    }

    @Test
    void testDoubleValue() {
    }

    @Test
    void enumValue() {
    }
}