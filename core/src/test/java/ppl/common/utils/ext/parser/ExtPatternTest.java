package ppl.common.utils.ext.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ppl.common.utils.ext.Ext;
import ppl.common.utils.ext.Name;

import java.util.regex.Pattern;
import java.util.stream.Stream;

class ExtPatternTest {

    private static ExtPattern prt = ExtPattern.builder()
            .name("prt")
            .position(ExtPatternPosition.RIGHT)
            .pattern(Pattern.compile("\\.prt(?:\\.[0-9]+)?$"))
            .build();
    private static ExtPattern prepin = ExtPattern.builder()
            .name("prepin")
            .position(ExtPatternPosition.LEFT)
            .pattern(Pattern.compile("^prepin\\."))
            .build();
    private static ExtPattern bsd = ExtPattern.builder()
            .name("bsd")
            .position(ExtPatternPosition.RIGHT)
            .exact(false)
            .pattern(Pattern.compile("\\.bsd[0-9]*$"))
            .build();
    private static ExtPattern flsgrf = ExtPattern.builder()
            .name("flsgrf")
            .position(ExtPatternPosition.LEFT)
            .pattern(Pattern.compile("^flsgrf[0-9]*\\."))
            .exact(false)
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
    void parse(ExtParser parser, String name, Ext ext) {
        Ext actual = parser.parse(name);
        Assertions.assertEquals(actual.getExt(), ext.getExt());
        Assertions.assertEquals(actual.getName().getBase(), ext.getName().getBase());
        Assertions.assertEquals(actual.getName().toString(), ext.getName().toString());
    }

    private static Stream<Arguments> parserProvider() {
        return Stream.of(
                Arguments.of(prt, "a.prt", new Ext("prt", new Name(new String[] {"a", ".prt"}, 0))),
                Arguments.of(prt, "a.prt.987", new Ext("prt", new Name(new String[] {"a", ".prt.987"}, 0))),
                Arguments.of(prepin, "prepin.hua", new Ext("prepin", new Name(new String[] {"prepin.", "hua"}, 1))),
                Arguments.of(bsd, "a.bsd", new Ext("bsd", new Name(new String[] {"a", ".bsd"}, 0))),
                Arguments.of(bsd, "a.bsd98", new Ext("bsd", new Name(new String[] {"a", ".bsd98"}, 0))),
                Arguments.of(flsgrf, "flsgrf.a", new Ext("flsgrf", new Name(new String[] {"flsgrf.", "a"}, 1))),
                Arguments.of(flsgrf, "flsgrf78.97", new Ext("flsgrf", new Name(new String[] {"flsgrf78.", "97"}, 1)))
                );
    }

    @Test
    void orderBy() {
        Assertions.assertEquals(9, prt.orderBy(9).getOrder());
    }

}