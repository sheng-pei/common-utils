package ppl.common.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class StringUtilsTest {

    private static final String[] EMPTY_ARRAY = new String[0];

    @ParameterizedTest
    @MethodSource("emptySplitProvider")
    public void testSplitEmpty(String empty, String regex) {
        String[] actual = StringUtils.split(empty, regex);
        Assertions.assertArrayEquals(EMPTY_ARRAY, actual);
    }

    private static Stream<Arguments> emptySplitProvider() {
        return Stream.of(
                Arguments.of("", ""),
                Arguments.of(null, ""),
                Arguments.of("", ";*"),
                Arguments.of(null, ";*"),
                Arguments.of("", "a"),
                Arguments.of(null, "a")
        );
    }

    @ParameterizedTest
    @MethodSource({"noMatchingProvider"})
    public void testSplitWithNoMatching(String input, String regex, String[] expected) {
        String[] actual = StringUtils.split(input, regex);
        Assertions.assertArrayEquals(expected, actual);
    }

    private static Stream<Arguments> noMatchingProvider() {
        return Stream.of(
                Arguments.of("abndc", "", new String[] {"a", "b", "n", "d", "c"}),
                Arguments.of("a", ";*", new String[] {"a"})
        );
    }

    @ParameterizedTest
    @MethodSource({"matchingProvider"})
    public void testSplitWithMatching(String input, String regex, String[] expected) {
        String[] actual = StringUtils.split(input, regex);
        Assertions.assertArrayEquals(expected, actual);
    }

    private static Stream<Arguments> matchingProvider() {
        return Stream.of(
                Arguments.of(";", ";*", new String[] {""}),
                Arguments.of(";a", ";*", new String[] {"", "a"}),
                Arguments.of("a;", ";*", new String[] {"a"}),
                Arguments.of("a;bfd;;;;d;;", ";*", new String[] {"a", "b", "f", "d", "d"})
        );
    }

    @Test
    public void testSplitByNull() {
        Assertions.assertThrows(NullPointerException.class, () -> StringUtils.split("", null));
    }

    @ParameterizedTest
    @MethodSource("uniqueProvider")
    public void testUniqueOfNull(String[] source, String[] expected) {
        String[] actual = StringUtils.removeDuplicate(source);
        Assertions.assertArrayEquals(expected, actual);
    }

    private static Stream<Arguments> uniqueProvider() {
        return Stream.of(
                Arguments.of(null, EMPTY_ARRAY),
                Arguments.of(new String[] {"a", "b", "a"}, new String[] {"a", "b"})
        );
    }

    @ParameterizedTest
    @MethodSource("emptyProvider")
    public void testIsEmpty(String str, boolean expected) {
        Assertions.assertEquals(expected, StringUtils.isEmpty(str));
    }

    private static Stream<Arguments> emptyProvider() {
        return Stream.of(
                Arguments.of(null, true),
                Arguments.of("", true),
                Arguments.of("a", false)
        );
    }

    @ParameterizedTest
    @MethodSource("notEmptyProvider")
    public void testIsNotEmptyOfNull(String str, boolean expected) {
        Assertions.assertEquals(expected, StringUtils.isNotEmpty(str));
    }

    private static Stream<Arguments> notEmptyProvider() {
        return Stream.of(
                Arguments.of(null, false),
                Arguments.of("", false),
                Arguments.of("a", true)
        );
    }

    @ParameterizedTest
    @MethodSource("blankProvider")
    public void testIsBlank(String input, boolean expected) {
        Assertions.assertEquals(expected, StringUtils.isBlank(input));
    }

    private static Stream<Arguments> blankProvider() {
        return Stream.of(
                Arguments.of(null, true),
                Arguments.of("", true),
                Arguments.of(" \t\n\r", true),
                Arguments.of(" \t\n\rc\r\n\t ", false),
                Arguments.of("a", false)
        );
    }

    @ParameterizedTest
    @MethodSource("snakeCaseProvider")
    public void testToSnakeCase(String input, String expected) {
        Assertions.assertEquals(expected, StringUtils.toSnakeCase(input));
    }

    private static Stream<Arguments> snakeCaseProvider() {
        return Stream.of(
                Arguments.of("MM", "mm"),
                Arguments.of("mM", "m_m"),
                Arguments.of("m_M", "m_m")
        );
    }

    @ParameterizedTest
    @MethodSource("formatProvider")
    public void testFormatJustSingleReference(String reference, Object[] parameters, String expected) {
        String replaced = StringUtils.format(reference, parameters);
        Assertions.assertEquals(expected, replaced);
    }

    private static Stream<Arguments> formatProvider() {
        return Stream.of(
                Arguments.of("{}", new String[] {"ab"}, "ab"),
                Arguments.of("\\{}", new String[] {}, "{}"),
                Arguments.of("ab\\\\{", new String[] {}, "ab\\\\{"),
                Arguments.of("abc\\\\\\{}", new String[] {}, "abc\\{}"),
                Arguments.of("{}abc\\\\{}\\\\\\{}aaaa{}", new String[] {"ab", "cd", "ef"}, "ababc\\cd\\{}aaaaef")
        );
    }

    @ParameterizedTest
    @MethodSource({"literallyEqualityProvider", "nullEqualsEmptyProvider"})
    public void testEqualsLiterally(String str1, String str2, boolean expected) {
        Assertions.assertEquals(expected, StringUtils.equalsLiterally(str1, str2));
    }

    private static Stream<Arguments> literallyEqualityProvider() {
        return Stream.of(
                Arguments.of("\rakj ", "akj", false)
        );
    }

    @ParameterizedTest
    @MethodSource({"onContentEqualityProvider", "nullEqualsEmptyProvider"})
    public void testEqualsOnContentNullEmpty(String str1, String str2, boolean expected) {
        Assertions.assertEquals(expected, StringUtils.equalsOnContent(str1, str2));
    }

    private static Stream<Arguments> onContentEqualityProvider() {
        return Stream.of(
                Arguments.of("\rakj ", "akj", true)
        );
    }

    private static Stream<Arguments> nullEqualsEmptyProvider() {
        return Stream.of(
                Arguments.of(null, "", true),
                Arguments.of("", null, true),
                Arguments.of(null, null, true),
                Arguments.of("akj", "akj", true)
        );
    }

    @ParameterizedTest
    @MethodSource("equalityProvider")
    public void testEquals(String str1, String str2, boolean expected) {
        Assertions.assertEquals(expected, StringUtils.equals(str1, str2));
    }

    private static Stream<Arguments> equalityProvider() {
        return Stream.of(
                Arguments.of(null, "", false),
                Arguments.of("", null, false),
                Arguments.of(null, null, true),
                Arguments.of("akj", "akj", true)
        );
    }

}