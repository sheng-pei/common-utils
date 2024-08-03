package ppl.common.utils.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ppl.common.utils.config.Node;
import ppl.common.utils.config.Nodes;

import java.util.*;

public class ConfigReaderTest {

    Map<Object, Object> configs = new HashMap<Object, Object>() {
        {
            this.put("int", 1);
            this.put("double", 1.0);
            List<String> list = new ArrayList<>();
            list.add("1");
            list.add("2");
            this.put("list", list);
        }
    };

    @Test
    public void testGetInteger() {
        Node reader = Nodes.root(configs);
        Assertions.assertEquals(1, reader.getChild("int").intValue());
    }

    @Test
    public void testGetLong() {
        Node reader = Nodes.root(configs);
        Assertions.assertEquals(1L, reader.getChild("int").longValue());
    }

    @Test
    public void testGetShort() {
        Node reader = Nodes.root(configs);
        Assertions.assertEquals((short) 1, reader.getChild("int").shortValue());
    }

    @Test
    public void testGetByte() {
        Node reader = Nodes.root(configs);
        Assertions.assertEquals((byte) 1, reader.getChild("int").byteValue());
    }

    @Test
    @SuppressWarnings("all")
    public void testMapIterator() {
        Node reader = Nodes.root(configs);
        Iterator<Node> itrMap = reader.iterator();
        while (itrMap.hasNext()) {
            Node itrNxt = itrMap.next();
            switch (itrNxt.fieldName()) {
                case "double":
                    Assertions.assertEquals(".double", itrNxt.path());
                    break;
                case "int":
                    Assertions.assertEquals(".int", itrNxt.path());
                    break;
                case "list":
                    Assertions.assertEquals(".list", itrNxt.path());
                    break;
                default:
                    Assertions.fail();
                    break;
            }
        }
    }

    @Test
    public void testListIterator() {
        Node reader = Nodes.root(configs);
        Node listNode = reader.getChild("list");
        Iterator<Node> itr = listNode.iterator();
        int i = 0;
        while (itr.hasNext()) {
            Node nxt = itr.next();
            int curr = i++;
            Assertions.assertEquals(".list.[" + curr + "]", nxt.path());
            Assertions.assertEquals(curr, nxt.index());
        }
    }

}
