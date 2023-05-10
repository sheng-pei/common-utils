package ppl.common.utils.string.substring.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ppl.common.utils.helper.EqualsTester;
import ppl.common.utils.string.substring.Substring;
import ppl.common.utils.string.substring.SubstringIndexOutOfBoundsException;

import java.util.stream.Stream;

public class SubstringTest {

    @Test
    public void testConstructSubstring() {
        new Substring("hello".toCharArray(), 0, 5);
    }

    @Test
    public void testConstructSubstringNullSource() {
        Assertions.assertThrows(NullPointerException.class,
                () -> new Substring(null, 0, 0));
    }

    @ParameterizedTest
    @MethodSource("constructSubstringOutOfIndexProvider")
    public void testConstructSubstringOutOfIndex(String source, int start, int end) {
        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> new Substring(source.toCharArray(), start, end));
    }

    private static Stream<Arguments> constructSubstringOutOfIndexProvider() {
        return Stream.of(
                Arguments.of("todo", -1, -1),
                Arguments.of("todo", -1, 1),
                Arguments.of("todo", 1, -1),
                Arguments.of("todo", 2, 1),
                Arguments.of("todo", 5, 5),
                Arguments.of("todo", 5, 4),
                Arguments.of("todo", 4, 5)
        );
    }

    @Test
    public void testSubstringEquals() {
        Substring s1 = new Substring("___terminal".toCharArray(), 3, 11);
        Substring s2 = new Substring("___terminal".toCharArray(), 3, 11);
        Substring s3 = new Substring("___terminal".toCharArray(), 3, 11);

        EqualsTester tester = new EqualsTester();
        tester.addGroup(s1, s2, s3);
        tester.addGroup(new Substring("___terminal".toCharArray(), 3, 8));
        tester.test();
    }

    @ParameterizedTest
    @MethodSource("stringProvider")
    public void testToString(String source, int start, int end, String expected) {
        Substring s = new Substring(source.toCharArray(), start, end);
        Assertions.assertEquals(expected, s.string());
    }

    private static Stream<Arguments> stringProvider() {
        return Stream.of(
                Arguments.of("terminal", 0, 8, "terminal"),
                Arguments.of("Expression", 2, 7, "press")
        );
    }

    @ParameterizedTest
    @MethodSource("stringProvider")
    public void testString(String source, int start, int end, String expected) {
        Substring s = new Substring(source.toCharArray(), start, end);
        Assertions.assertEquals(expected, s.string());
    }

    @ParameterizedTest
    @MethodSource("stringOffsetProvider")
    public void testStringOffset(int offset, String expected) {
        Substring s = new Substring("Expression".toCharArray(), 0, 10);
        Assertions.assertEquals(expected, s.string(offset));
    }

    private static Stream<Arguments> stringOffsetProvider() {
        return Stream.of(
                Arguments.of(10, ""),
                Arguments.of(8, "on"),
                Arguments.of(0, "Expression")
        );
    }

    @ParameterizedTest
    @MethodSource("stringOffsetOutOfIndexProvider")
    public void testStringOffsetOutOfIndex(int offset) {
        Substring s = new Substring("terminal".toCharArray(), 0, 8);
        Assertions.assertThrows(SubstringIndexOutOfBoundsException.class,
                () -> s.string(offset));
    }

    private static Stream<Arguments> stringOffsetOutOfIndexProvider() {
        return Stream.of(
                Arguments.of(-1),
                Arguments.of(10)
        );
    }

    @ParameterizedTest
    @MethodSource("stringOffsetLengthProvider")
    public void testStringOffsetLength(int offset, int length, String expected) {
        Substring s = new Substring("Expression".toCharArray(), 0, 10);
        Assertions.assertEquals(expected, s.string(offset, length));
    }

    private static Stream<Arguments> stringOffsetLengthProvider() {
        return Stream.of(
                Arguments.of(0, 10, "Expression"),
                Arguments.of(2, 5, "press"),
                Arguments.of(9, 0, ""),
                Arguments.of(0, 0, "")
        );
    }

    @ParameterizedTest
    @MethodSource("stringOffsetLengthOutOfIndexProvider")
    public void testStringOffsetLengthOutOfIndex(int offset, int length) {
        Substring s = new Substring("terminal".toCharArray(), 0, 8);
        Assertions.assertThrows(SubstringIndexOutOfBoundsException.class,
                () -> s.string(offset, length));
    }

    private static Stream<Arguments> stringOffsetLengthOutOfIndexProvider() {
        return Stream.of(
                Arguments.of(-1, 3),
                Arguments.of(1, 9),
                Arguments.of(9, 1),
                Arguments.of(10, 1)
        );
    }

    @Test
    public void testLength() {
        Substring s = new Substring("aaa".toCharArray(), 0, 3);
        Assertions.assertEquals(3, s.length());
        Assertions.assertFalse(s.isEmpty());
    }

    @Test
    public void testIsEmpty() {
        Substring s = new Substring("aaa".toCharArray(), 1, 1);
        Assertions.assertTrue(s.isEmpty());
        Assertions.assertEquals(0, s.length());
    }

    @Test
    public void testStartEnd() {
        Substring s = new Substring("expression".toCharArray(), 2, 7);
        Assertions.assertEquals("press", s.toString());
        Assertions.assertEquals(2, s.start());
        Assertions.assertEquals(7, s.end());
    }

}
