package ppl.common.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConfigReaderTest {

    Map<Object, Object> configs = new HashMap<Object, Object>() {
        {
            this.put("int", 1);
            this.put("double", 1.0);
            this.put("list", new ArrayList<>());
        }
    };

    @Test
    public void testReadInteger() {
        ConfigReader reader = new ConfigReaderImpl(configs);
        Integer i = reader.getInteger("int");
        Assertions.assertEquals(1, i);
    }
}
