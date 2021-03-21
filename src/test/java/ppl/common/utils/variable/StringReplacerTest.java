package ppl.common.utils.variable;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StringReplacerTest {

    @Test
    public void test() {
        StringReplacer replacer = VariableParser.parse("");
        Map<String, Object> map = new HashMap<>();
        map.put("a{a", "a");
        System.out.println(replacer.replace(map));
    }

}