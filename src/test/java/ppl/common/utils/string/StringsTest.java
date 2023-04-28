package ppl.common.utils.string;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class StringsTest {

    private static final String[] EMPTY_ARRAY = new String[0];

    @ParameterizedTest
    @MethodSource("emptySplitProvider")
    public void testSplitEmpty(String empty, String regex) {
        String[] actual = Strings.split(empty, regex);
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
        String[] actual = Strings.split(input, regex);
        Assertions.assertArrayEquals(expected, actual);
    }

    private static Stream<Arguments> noMatchingProvider() {
        return Stream.of(
                Arguments.of("hello", "", new String[] {"h", "e", "l", "l", "o"}),
                Arguments.of("a", ";*", new String[] {"a"})
        );
    }

    @ParameterizedTest
    @MethodSource({"matchingProvider"})
    public void testSplitWithMatching(String input, String regex, String[] expected) {
        String[] actual = Strings.split(input, regex);
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
        Assertions.assertThrows(NullPointerException.class, () -> Strings.split("", null));
    }

    @ParameterizedTest
    @MethodSource("uniqueProvider")
    public void testUniqueOfNull(String[] source, String[] expected) {
        String[] actual = Strings.removeDuplicate(source);
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
        Assertions.assertEquals(expected, Strings.isEmpty(str));
        Assertions.assertEquals(!expected, Strings.isNotEmpty(str));
    }

    private static Stream<Arguments> emptyProvider() {
        return Stream.of(
                Arguments.of(null, true),
                Arguments.of("", true),
                Arguments.of("a", false)
        );
    }

    @ParameterizedTest
    @MethodSource("blankProvider")
    public void testIsBlank(String input, boolean expected) {
        Assertions.assertEquals(!expected, Strings.isNotBlank(input));
        Assertions.assertEquals(expected, Strings.isBlank(input));
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
        Assertions.assertEquals(expected, Strings.toSnakeCase(input));
    }

    private static Stream<Arguments> snakeCaseProvider() {
        return Stream.of(
                Arguments.of("MM", "mm"),
                Arguments.of("mM", "m_m"),
                Arguments.of("m_M", "m_m")
        );
    }

    @Test
    public void testFormatWithNotEnoughArguments() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Strings.format("{}abc\\\\{}\\\\\\{}aaa{}", "ab", "cd"));
    }

    @ParameterizedTest
    @MethodSource("formatProvider")
    public void testFormat(String reference, Object[] parameters, String expected) {
        String replaced = Strings.format(reference, parameters);
        Assertions.assertEquals(expected, replaced);
    }

    private static Stream<Arguments> formatProvider() {
        return Stream.of(
                Arguments.of("{}", new String[] {"ab"}, "ab"),
                Arguments.of("\\{}", new String[] {}, "{}"),
                Arguments.of("ab\\\\{", new String[] {}, "ab\\\\{"),
                Arguments.of("abc\\\\\\{}", new String[] {}, "abc\\{}"),
                Arguments.of("{} abc\\\\{}\\\\\\{}aaa {}", new String[] {"ab", "cd", "ef", "kl"},
                        "ab abc\\cd\\{}aaa ef")
        );
    }

    @ParameterizedTest
    @MethodSource({"literallyEqualityProvider", "nullEqualsEmptyProvider"})
    public void testEqualsLiterally(String str1, String str2, boolean expected) {
        Assertions.assertEquals(expected, Strings.equalsLiterally(str1, str2));
    }

    private static Stream<Arguments> literallyEqualityProvider() {
        return Stream.of(
                Arguments.of("\rakj ", "akj", false)
        );
    }

    @ParameterizedTest
    @MethodSource({"onContentEqualityProvider", "nullEqualsEmptyProvider"})
    public void testEqualsOnContentNullEmpty(String str1, String str2, boolean expected) {
        Assertions.assertEquals(expected, Strings.equalsOnContent(str1, str2));
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
        Assertions.assertEquals(expected, Strings.equals(str1, str2));
    }

    private static Stream<Arguments> equalityProvider() {
        return Stream.of(
                Arguments.of(null, "", false),
                Arguments.of("", null, false),
                Arguments.of(null, null, true),
                Arguments.of("akj", "akj", true)
        );
    }

    @ParameterizedTest
    @MethodSource("trimWithDefaultPositionProvider")
    void testTrimWithDefaultPosition(String source, char c, String expected) {
        Assertions.assertEquals(expected, Strings.trim(source, c));
    }

    private static Stream<Arguments> trimWithDefaultPositionProvider() {
        return Stream.of(
                Arguments.of(null, '_', null),
                Arguments.of("", '_', ""),
                Arguments.of("__aa__", '_', "aa")
        );
    }

    @ParameterizedTest
    @MethodSource("trimProvider")
    void testTrim(String source, char c, TrimPosition position, String expected) {
        Assertions.assertEquals(expected, Strings.trim(source, c, position));
    }

    private static Stream<Arguments> trimProvider() {
        return Stream.of(
                Arguments.of(null, '_', TrimPosition.ALL, null),
                Arguments.of("", '_', TrimPosition.ALL, ""),
                Arguments.of("__aa__", '_', TrimPosition.ALL, "aa"),
                Arguments.of("__aa__", "_", TrimPosition.NO, "__aa__"),
                Arguments.of("__aa__", "_", TrimPosition.BEFORE, "aa__"),
                Arguments.of("__aa__", "_", TrimPosition.END, "__aa")
        );
    }

    @ParameterizedTest
    @MethodSource("errorIndexAndStringProvider")
    void testLastIndexOfNot(char c, String string, int begin, int end) {
        Assertions.assertThrows(StringIndexOutOfBoundsException.class,
                () -> Strings.lastIndexOfNot(c, string, begin, end));
        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> Strings.lastIndexOfNot(c, string.toCharArray(), begin, end));
    }

    private static Stream<Arguments> errorIndexAndStringProvider() {
        return Stream.of(
                Arguments.of('_', "hello", -1, 1),
                Arguments.of('_', "hello", 6, 1),
                Arguments.of('_', "hello", 1, -1),
                Arguments.of('_', "hello", 1, 6)
        );
    }

    @ParameterizedTest
    @MethodSource("indexAndStringProvider")
    void testLastIndexOfNot(char c, String string, int begin, int end, int expected) {
        Assertions.assertEquals(expected, Strings.lastIndexOfNot(c, string, begin, end));
        Assertions.assertEquals(expected, Strings.lastIndexOfNot(c, string.toCharArray(), begin, end));
    }

    private static Stream<Arguments> indexAndStringProvider() {
        return Stream.of(
                Arguments.of('_', "hello", 4, 3, -1),
                Arguments.of('_', "hello", 5, 5, -1)
        );
    }

    @ParameterizedTest
    @MethodSource("lastIndexOfNotProvider")
    void testLastIndexOfNot(char c, String string, int expected) {
        Assertions.assertEquals(expected, Strings.lastIndexOfNot(c, string));
    }

    private static Stream<Arguments> lastIndexOfNotProvider() {
        return Stream.of(
                Arguments.of('_', "hello", 4),
                Arguments.of('_', "hello_", 4),
                Arguments.of('_', "____", -1)
        );
    }

    @ParameterizedTest
    @MethodSource("errorIndexAndStringProvider")
    void testLastIndexOf(char c, String string, int begin, int end) {
        Assertions.assertThrows(StringIndexOutOfBoundsException.class,
                () -> Strings.lastIndexOf(c, string, begin, end));
        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> Strings.lastIndexOf(c, string.toCharArray(), begin, end));
    }

    @ParameterizedTest
    @MethodSource("indexAndStringProvider")
    void testLastIndexOf(char c, String string, int begin, int end, int expected) {
        Assertions.assertEquals(expected, Strings.lastIndexOf(c, string, begin, end));
        Assertions.assertEquals(expected, Strings.lastIndexOf(c, string.toCharArray(), begin, end));
    }

    @ParameterizedTest
    @MethodSource("lastIndexOfProvider")
    void testLastIndexOf(char c, String string, int expected) {
        Assertions.assertEquals(expected, Strings.lastIndexOf(c, string));
    }

    private static Stream<Arguments> lastIndexOfProvider() {
        return Stream.of(
                Arguments.of('_', "hello", -1),
                Arguments.of('_', "hello_", 5),
                Arguments.of('_', "____", 3)
        );
    }

    @ParameterizedTest
    @MethodSource("errorIndexAndStringProvider")
    void testIndexOfNot(char c, String string, int begin, int end) {
        Assertions.assertThrows(StringIndexOutOfBoundsException.class,
                () -> Strings.indexOfNot(c, string, begin, end));
        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> Strings.indexOfNot(c, string.toCharArray(), begin, end));
    }

    @ParameterizedTest
    @MethodSource("indexAndStringProvider")
    void testIndexOfNot(char c, String string, int begin, int end, int expected) {
        Assertions.assertEquals(expected, Strings.indexOfNot(c, string, begin, end));
        Assertions.assertEquals(expected, Strings.indexOfNot(c, string.toCharArray(), begin, end));
    }

    @ParameterizedTest
    @MethodSource("indexOfNotProvider")
    void testIndexOfNot(char c, String string, int expected) {
        Assertions.assertEquals(expected, Strings.indexOfNot(c, string));
    }

    private static Stream<Arguments> indexOfNotProvider() {
        return Stream.of(
                Arguments.of('_', "hello", 0),
                Arguments.of('_', "hello_", 0),
                Arguments.of('_', "____", -1)
        );
    }

    @ParameterizedTest
    @MethodSource("errorIndexAndStringProvider")
    void testIndexOf(char c, String string, int begin, int end) {
        Assertions.assertThrows(StringIndexOutOfBoundsException.class,
                () -> Strings.indexOf(c, string, begin, end));
        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> Strings.indexOf(c, string.toCharArray(), begin, end));
    }

    @ParameterizedTest
    @MethodSource("indexAndStringProvider")
    void testIndexOf(char c, String string, int begin, int end, int expected) {
        Assertions.assertEquals(expected, Strings.indexOf(c, string, begin, end));
        Assertions.assertEquals(expected, Strings.indexOf(c, string.toCharArray(), begin, end));
    }

    @ParameterizedTest
    @MethodSource("indexOfProvider")
    void testIndexOf(char c, String string, int expected) {
        Assertions.assertEquals(expected, Strings.indexOf(c, string));
    }

    private static Stream<Arguments> indexOfProvider() {
        return Stream.of(
                Arguments.of('_', "hello", -1),
                Arguments.of('_', "_hello", 0),
                Arguments.of('_', "____", 0)
        );
    }

    @ParameterizedTest
    @MethodSource("stringsProvider")
    void testJoin(String delimiter, String expected, String... strings) {
        Assertions.assertEquals(expected, Strings.join(delimiter, strings));
    }

    private static Stream<Arguments> stringsProvider() {
        return Stream.of(
                Arguments.of("_", "a_b", new String[] {"a", "b"}),
                Arguments.of("_", "", new String[0])
        );
    }

    @ParameterizedTest
    @MethodSource("errorOffsetAndLengthProvider")
    void testJoin(String delimiter, String[] strings, int offset, int length) {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Strings.join(delimiter, strings, offset, length));
    }

    private static Stream<Arguments> errorOffsetAndLengthProvider() {
        return Stream.of(
                Arguments.of("_", new String[] {"a", "b"}, -1, 1),
                Arguments.of("_", new String[] {"a", "b"}, 1, -1),
                Arguments.of("_", new String[] {"a", "b"}, 1, 3)
        );
    }
}