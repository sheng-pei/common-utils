package ppl.common.utils.variable;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import ppl.common.utils.exception.VariablePatternException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StringReplacerTest {

    private Map<String, Object> map;

    @BeforeAll
    void init() {
        Map<String, Object> m = new HashMap<>();
        m.put("a", "a");
        m.put("{", "{");
        this.map = Collections.unmodifiableMap(m);
    }

    @ParameterizedTest
    @MethodSource({"patternAndExpectProvider"})
    public void test(String pattern, String expect) {
        StringReplacer replacer = VariableParser.parse(pattern);
        Assertions.assertEquals(expect, replacer.replace(this.map));
    }

    private static Stream<Arguments> patternAndExpectProvider() {
        return Stream.of(
                Arguments.of("", ""),
                Arguments.of("${a}", "a"),
                Arguments.of("${{}", "{"),
                Arguments.of("${a}}", "a}"),
                Arguments.of("ab${a}ab", "abaab"),
                Arguments.of("${aaa", "${aaa")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"${}", "${$}"})
    public void test(String pattern) {
        Assertions.assertThrows(VariablePatternException.class, () -> VariableParser.parse(pattern));
    }

    @AfterAll
    static void cleanup() {

    }

}