package ppl.common.utils.string.substring.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ppl.common.utils.string.substring.Substring;
import ppl.common.utils.string.substring.SubstringFinder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

class SubstringFinderTest {

    private final static Map<String, Function<String, SubstringFinder>> FINDERS = new HashMap<>();

    static {
        FINDERS.put("sunday", SundaySubstringFinder::new);
        FINDERS.put("kmp", KMPSubstringFinder::new);
    }

    @ParameterizedTest
    @MethodSource("instantiationProvider")
    public void testConstructor(String name, String pattern) {
        FINDERS.get(name).apply(pattern);
    }

    private static Stream<Arguments> instantiationProvider() {
        return Stream.of(
                Arguments.of("sunday", "a"),
                Arguments.of("sunday", "acveaew"),
                Arguments.of("kmp", "d"),
                Arguments.of("kmp", "daefawe")
        );
    }

    @ParameterizedTest
    @MethodSource("instantiationExceptionProvider")
    public void testConstructorThrowsIllegalArgumentException(String name, String pattern) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FINDERS.get(name).apply(pattern));
    }

    private static Stream<Arguments> instantiationExceptionProvider() {
        return Stream.of(
                Arguments.of("sunday", ""),
                Arguments.of("sunday", null),
                Arguments.of("kmp", ""),
                Arguments.of("kmp", null)
        );
    }

    @ParameterizedTest
    @MethodSource({"notFoundProvider"})
    public void testNotFound(String name, String pattern, String input) {
        SubstringFinder finder = FINDERS.get(name).apply(pattern);
        Assertions.assertNull(finder.find(input));
    }

    private static Stream<Arguments> notFoundProvider() {
        return Stream.of(
                //len(input) < len(pattern)
                Arguments.of("sunday", "jfieak", "jf"),
                Arguments.of("kmp", "jfieak", "jf"),
                //len(input) == len(pattern)
                Arguments.of("sunday", "jfieak", "jfieaj"),
                Arguments.of("kmp", "jfieak", "jfieam"),
                //len(input) > len(pattern)
                Arguments.of("sunday", "nfmei", "ahifeklaiwej"),
                Arguments.of("kmp", "nfmei", "ahifeklaiwej")
        );
    }

    @ParameterizedTest
    @MethodSource({"findInputProvider"})
    public void testFindInput(String name, String pattern, String input, ppl.common.utils.string.substring.Substring expected) {
        SubstringFinder finder = FINDERS.get(name).apply(pattern);
        Assertions.assertEquals(expected, finder.find(input));
    }

    private static Stream<Arguments> findInputProvider() {
        return Stream.of(
                //input == pattern
                Arguments.of("sunday", "abccda", "abccda", new Substring("abccda".toCharArray(), 0, 6)),
                Arguments.of("kmp", "GCGCGT", "GCGCGT", new Substring("GCGCGT".toCharArray(), 0, 6)),
                //pattern is prefix of input
                Arguments.of("sunday", "abccda", "abccdanma", new Substring("abccdanma".toCharArray(), 0, 6)),
                Arguments.of("kmp", "GCGCGT", "GCGCGTnma", new Substring("GCGCGTnma".toCharArray(), 0, 6)),
                //pattern is suffix of input
                Arguments.of("sunday", "abccda", "afewfabccda", new Substring("afewfabccda".toCharArray(), 5, 11)),
                Arguments.of("kmp", "GCGCGT", "afewfGCGCGT", new Substring("afewfGCGCGT".toCharArray(), 5, 11)),
                //pattern is in the middle of input
                Arguments.of("sunday", "abccda", "afewfabccda.n;k", new Substring("afewfabccda.n;k".toCharArray(), 5, 11)),
                Arguments.of("kmp", "GCGCGT", "afewfGCGCGT.n;k", new Substring("afewfGCGCGT.n;k".toCharArray(), 5, 11))
        );
    }

    @ParameterizedTest
    @MethodSource({"findInputStartEndProvider"})
    public void testFindInputStartEnd(String name, String pattern, String input, int start, int end, ppl.common.utils.string.substring.Substring expected) {
        SubstringFinder finder = FINDERS.get(name).apply(pattern);
        Assertions.assertEquals(expected, finder.find(input, start, end));
    }

    private static Stream<Arguments> findInputStartEndProvider() {
        return Stream.of(
                Arguments.of("sunday", "abccda", "ababccdacda", 1, 10, new Substring("ababccdacda".toCharArray(), 2, 8)),
                Arguments.of("kmp", "GCGCGT", "GCGCGCGTCGT", 1, 10, new Substring("GCGCGCGTCGT".toCharArray(), 2, 8))
        );
    }

}