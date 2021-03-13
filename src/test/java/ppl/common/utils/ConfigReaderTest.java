package ppl.common.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ppl.common.utils.config.Reader;
import ppl.common.utils.config.Readers;

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
    public void testMapIterator() {
        Reader reader = Readers.root(configs);
        Iterator<Reader> itrMap = reader.iterator();
        while (itrMap.hasNext()) {
            Reader itrNxt = itrMap.next();
            switch (itrNxt.keyString()) {
                case "double":
                    Assertions.assertEquals(".double", itrNxt.absolutePath());
                    break;
                case "int":
                    Assertions.assertEquals(".int", itrNxt.absolutePath());
                    break;
                case "list":
                    Assertions.assertEquals(".list", itrNxt.absolutePath());
                    break;
                default:
                    Assertions.fail();
                    break;
            }
        }
    }

    @Test
    public void testListIterator() {
        Reader reader = Readers.root(configs);
        Reader listReader = reader.getChild("list");
        Iterator<Reader> itr = listReader.iterator();
        int i = 0;
        while (itr.hasNext()) {
            Reader nxt = itr.next();
            int curr = i++;
            Assertions.assertEquals(".list.L[" + curr + "]", nxt.absolutePath());
            Assertions.assertEquals(curr, nxt.keyInteger());
        }
    }

}
