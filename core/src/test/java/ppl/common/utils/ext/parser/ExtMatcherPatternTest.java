package ppl.common.utils.ext.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ppl.common.utils.ext.ExtMatcher;
import ppl.common.utils.ext.Name;

import java.util.regex.Pattern;
import java.util.stream.Stream;

class ExtMatcherPatternTest {

    private static ExtPattern prt = ExtPattern.builder()
            .ext("prt")
            .position(ExtPatternPosition.RIGHT)
            .pattern(Pattern.compile("\\.prt(?:\\.[0-9]+)?$"))
            .build();
    private static ExtPattern prepin = ExtPattern.builder()
            .ext("prepin")
            .position(ExtPatternPosition.LEFT)
            .pattern(Pattern.compile("^prepin\\."))
            .build();
    private static ExtPattern bsd = ExtPattern.builder()
            .ext("bsd")
            .position(ExtPatternPosition.RIGHT)
            .pattern(Pattern.compile("\\.bsd[0-9]*$"))
            .build();
    private static ExtPattern flsgrf = ExtPattern.builder()
            .ext("flsgrf")
            .position(ExtPatternPosition.LEFT)
            .pattern(Pattern.compile("^flsgrf[0-9]*\\."))
            .build();

    @ParameterizedTest
    @MethodSource("parserFailedProvider")
    void parseFailed(ExtParser parser, String name) {
        Assertions.assertNull(parser.parse(name));
    }

    private static Stream<Arguments> parserFailedProvider() {
        return Stream.of(
                Arguments.of(prt, "a.prt987"),
                Arguments.of(prepin, "prepin"),
                Arguments.of(flsgrf, "flsgrf7j.97")
        );
    }

    @ParameterizedTest
    @MethodSource("parserProvider")
    void parse(ExtParser parser, String name, ExtMatcher ext) {
        ExtMatcher actual = parser.parse(name);
        Assertions.assertEquals(actual.getExt(), ext.getExt());
        Assertions.assertEquals(actual.getName().getBase(), ext.getName().getBase());
        Assertions.assertEquals(actual.getName().getExt(), ext.getName().getExt());
        Assertions.assertEquals(actual.getName().getPosition(), ext.getName().getPosition());
        Assertions.assertEquals(actual.getName().toString(), ext.getName().toString());
    }

    private static Stream<Arguments> parserProvider() {
        return Stream.of(
                Arguments.of(prt, "a.prt", new ExtMatcher("prt", new Name("a", ".prt", ExtPatternPosition.RIGHT))),
                Arguments.of(prt, "a.prt.987", new ExtMatcher("prt", new Name("a", ".prt.987", ExtPatternPosition.RIGHT))),
                Arguments.of(prepin, "prepin.hua", new ExtMatcher("prepin", new Name("hua", "prepin.", ExtPatternPosition.LEFT))),
                Arguments.of(bsd, "a.bsd", new ExtMatcher("bsd", new Name("a", ".bsd", ExtPatternPosition.RIGHT))),
                Arguments.of(bsd, "a.bsd98", new ExtMatcher("bsd", new Name("a", ".bsd98", ExtPatternPosition.RIGHT))),
                Arguments.of(flsgrf, "flsgrf.a", new ExtMatcher("flsgrf", new Name("a", "flsgrf.", ExtPatternPosition.LEFT))),
                Arguments.of(flsgrf, "flsgrf78.97", new ExtMatcher("flsgrf", new Name("97", "flsgrf78.", ExtPatternPosition.LEFT)))
                );
    }

    @Test
    void orderBy() {
        Assertions.assertEquals(9, prt.orderBy(9).getOrder());
    }

}