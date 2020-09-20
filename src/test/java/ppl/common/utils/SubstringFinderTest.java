package ppl.common.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

class SubstringFinderTest {

    @ParameterizedTest
    @ValueSource(classes = {SundaySubstringFinder.class, KMPSubstringFinder.class})
    public void testConstructorWithEmptyPattern(Class<?> clazz) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> newInstance(clazz, ""));
    }

    @ParameterizedTest
    @ValueSource(classes = {SundaySubstringFinder.class, KMPSubstringFinder.class})
    public void testConstructorWithNullPattern(Class<?> clazz) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> newInstance(clazz, null));
    }

    private SubstringFinder newInstance(Class<?> clazz, String param) throws Throwable {
        try {
            return (SubstringFinder) clazz.getConstructor(String.class).newInstance(param);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    @ParameterizedTest
    @MethodSource({"subclassAndPatternProvider"})
    public void testFindInputSmallerThanPattern(Class<?> finderClazz, String pattern) throws Throwable {
        SubstringFinder finder = newInstance(finderClazz, pattern);
        Assertions.assertTrue(finder.find("").isEmpty());
    }

    @ParameterizedTest
    @MethodSource({"subclassAndPatternProvider"})
    public void testFindInputEqualsToPattern(Class<?> finderClazz, String pattern) throws Throwable {
        SubstringFinder finder = newInstance(finderClazz, pattern);

        Substring actual = finder.find(pattern);
        Substring expected = new Substring(pattern);

        assertSubstring(expected, actual);
    }

    @ParameterizedTest
    @MethodSource({"subclassAndPatternProvider"})
    public void testFindPrefixOfInput(Class<?> finderClazz, String pattern) throws Throwable {
        SubstringFinder finder = newInstance(finderClazz, pattern);

        String input = pattern + "nfsiefj";
        Substring actual = finder.find(input);
        Substring expected = new Substring(input, 0, pattern.length());

        assertSubstring(expected, actual);
    }

    @ParameterizedTest
    @MethodSource({"subclassAndPatternProvider"})
    public void testFindSuffixOfInput(Class<?> finderClazz, String pattern) throws Throwable {
        SubstringFinder finder = newInstance(finderClazz, pattern);

        String input = "nfsiefj" + pattern;
        Substring actual = finder.find(input);
        Substring expected = new Substring(input, input.length() - pattern.length(), input.length());

        assertSubstring(expected, actual);
    }

    @ParameterizedTest
    @MethodSource({"subclassAndPatternProvider"})
    public void testFindInnerOfInput(Class<?> finderClazz, String pattern) throws Throwable {
        SubstringFinder finder = newInstance(finderClazz, pattern);

        String input = pattern.substring(0, 2) + pattern + pattern.substring(3);
        Substring actual = finder.find(input);
        Substring expected = new Substring(input, 2, input.length() - 3);

        assertSubstring(expected, actual);
    }

    @ParameterizedTest
    @MethodSource({"subclassAndPatternProvider"})
    public void testFindNoPatternInInput(Class<?> finderClazz, String pattern) throws Throwable {

        SubstringFinder finder = newInstance(finderClazz, pattern);

        String input = pattern.substring(0, 2) + pattern.substring(0, pattern.length() - 1) + pattern.substring(3);
        Assertions.assertTrue(finder.find(input, 1, pattern.length() + 3).isEmpty());

    }

    @ParameterizedTest
    @MethodSource({"subclassAndPatternProvider"})
    public void testFindInnerOfSubstringOfInput(Class<?> finderClazz, String pattern) throws Throwable {
        SubstringFinder finder = newInstance(finderClazz, pattern);

        String input = pattern.substring(0, 2) + pattern + pattern.substring(3);
        Substring actual = finder.find(input, 1, pattern.length() + 3);
        Substring expected = new Substring(input, 2, input.length() - 3);

        assertSubstring(expected, actual);
    }

    private void assertSubstring(Substring expected, Substring actual) {
        Assertions.assertEquals(expected.getStart(), actual.getStart());
        Assertions.assertEquals(expected.getEnd(), actual.getEnd());
        Assertions.assertArrayEquals(expected.getSource(), actual.getSource());
        Assertions.assertEquals(expected, actual);
    }

    private static Stream<Arguments> subclassAndPatternProvider() {
        return Stream.of(
                Arguments.of(SundaySubstringFinder.class, "abccda"),
                Arguments.of(KMPSubstringFinder.class, "GCGCGT")
        );
    }

}