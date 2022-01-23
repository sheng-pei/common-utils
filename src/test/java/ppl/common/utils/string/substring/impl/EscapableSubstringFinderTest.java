package ppl.common.utils.string.substring.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ppl.common.utils.string.substring.SubstringFinder;

import java.util.function.Function;
import java.util.stream.Stream;

class EscapableSubstringFinderTest {

    @ParameterizedTest
    @MethodSource("instantiationExceptionProvider")
    public void testConstructorThrowsIllegalArgumentException(String pattern) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new EscapableSubstringFinder(pattern));
    }

    private static Stream<Arguments> instantiationExceptionProvider() {
        return Stream.of(
                Arguments.of(""),
                Arguments.of((Object) null),
                Arguments.of("\\"),
                Arguments.of("aba")
        );
    }

    @ParameterizedTest
    @MethodSource({"findInputProvider"})
    public void testFindInput(String pattern, String input, EscapableSubstring expected) {
        SubstringFinder finder = new EscapableSubstringFinder(pattern);
        Assertions.assertEquals(expected, finder.find(input));
    }

    private static Stream<Arguments> findInputProvider() {
        return Stream.of(
                Arguments.of("abccd", "abccd", new EscapableSubstring("abccd".toCharArray(), 0, 0, 5)),
                Arguments.of("GCGCGT", "afewf\\\\GCGCGT.n;k", new EscapableSubstring("afewf\\\\GCGCGT.n;k".toCharArray(), 5, 7, 13))
        );
    }

}