package ppl.common.utils.string.substring.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ppl.common.utils.string.substring.PositionedParameters;

import java.util.stream.Stream;

public class SubstringTest {

    private static final Object[] TARGETS = new Object[] {"tt", "vv"};

    @Test
    public void testConsistentReplace() {
        PositionedParameters targets = new PositionedParameters(TARGETS);
        ConsistentSubstring c = new ConsistentSubstring("aa");
        Assertions.assertEquals("tt", c.replace(targets));
        Assertions.assertEquals("vv", targets.get());
    }

    @ParameterizedTest
    @MethodSource({"escapableProvider"})
    public void testEscapableReplace(EscapableSubString substring, String target, String next) {
        PositionedParameters targets = new PositionedParameters(TARGETS);
        Assertions.assertEquals(target, substring.replace(targets));
        Assertions.assertEquals(next, targets.get());
    }

    private static Stream<Arguments> escapableProvider() {
        return Stream.of(
                Arguments.of(new EscapableSubString("aa\\\\{}bb", 2, 4, 6), "\\\\tt", "vv"),
                Arguments.of(new EscapableSubString("aa\\\\\\{}bb", 2, 5, 7), "\\\\{}", "tt")
        );
    }

}
