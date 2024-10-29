package ppl.common.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ppl.common.utils.ext.ExtPatternParser;
import ppl.common.utils.ext.Exts;

import java.util.stream.Stream;

public class ExtsTest {
    @ParameterizedTest
    @MethodSource("extParsingArgumentsProvider")
    public void testParseExtension(Exts exts, String filename, String ext, boolean known) {
        Exts.ParsedName e = exts.parse(filename);
        Assertions.assertEquals(ext, e.getParsedExt().getExt());
        Assertions.assertEquals(known, e.getParsedExt().isKnown());
    }

    private static Stream<Arguments> extParsingArgumentsProvider() {
        Exts exts = Exts.builder()
                .add(ExtPatternParser.compile("rar"))
                .add(ExtPatternParser.compile("txt"))
                .add(ExtPatternParser.compile("exe")).build();
        return Stream.of(Arguments.of(exts, "a.txt", "txt", true),
                Arguments.of(exts, "a.exe", "exe", true),
                Arguments.of(exts, "a.rar", "rar", true),
                Arguments.of(exts, "a.tar", "tar", false),
                Arguments.of(exts, "a.tar.gz", "gz", false));
    }
}
