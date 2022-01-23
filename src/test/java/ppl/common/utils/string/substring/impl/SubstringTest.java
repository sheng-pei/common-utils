package ppl.common.utils.string.substring.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ppl.common.utils.helper.EqualsTester;
import ppl.common.utils.string.substring.PositionalArguments;

import java.util.stream.Stream;

public class SubstringTest {

    @Test
    public void testConstructSubstring() {
        new Substring("jjfhi".toCharArray(), 0, 5);
    }

    @Test
    public void testConstructSubstringNullSource() {
        Assertions.assertThrows(NullPointerException.class,
                () -> new Substring(null, 0, 0));
    }

    @ParameterizedTest
    @MethodSource("constructSubstringOutOfIndexProvider")
    public void testConstructSubstringOutOfIndex(String source, int start, int end) {
        Assertions.assertThrows(StringIndexOutOfBoundsException.class,
                () -> new Substring(source.toCharArray(), start, end));
    }

    private static Stream<Arguments> constructSubstringOutOfIndexProvider() {
        return Stream.of(
                Arguments.of("aaaa", -1, -1),
                Arguments.of("aaaa", -1, 1),
                Arguments.of("aaaa", 1, -1),
                Arguments.of("aaaa", 2, 1),
                Arguments.of("aaaa", 5, 5),
                Arguments.of("aaaa", 5, 4),
                Arguments.of("aaaa", 4, 5)
        );
    }

    @Test
    public void testSubstringEquals() {
        Substring s1 = new Substring("fnewjfeih".toCharArray(), 3, 8);
        Substring s2 = new Substring("fnewjfeih".toCharArray(), 3, 8);
        Substring s3 = new Substring("fnewjfeih".toCharArray(), 3, 8);

        EqualsTester tester = new EqualsTester();
        tester.setAssertMethod(Assertions::assertTrue);
        tester.addGroup(s1, s2, s3);
        tester.addGroup(new Substring("fnewjfei".toCharArray(), 3, 8));
        tester.addGroup(new Substring("fnewjfeih".toCharArray(), 2, 8));
        tester.addGroup(new Substring("fnewjfeih".toCharArray(), 3, 9));

        tester.test();
    }

    @ParameterizedTest
    @MethodSource("toStringProvider")
    public void testToString(String source, int start, int end, String expected) {
        Substring s = new Substring(source.toCharArray(), start, end);
        Assertions.assertEquals(expected, s.string());
    }

    private static Stream<Arguments> toStringProvider() {
        return Stream.of(
                Arguments.of("fnewjfeih", 0, 9, "fnewjfeih"),
                Arguments.of("fnewjfeih", 3, 8, "wjfei")
        );
    }

    @ParameterizedTest
    @MethodSource("stringProvider")
    public void testString(String source, int start, int end, String expected) {
        Substring s = new Substring(source.toCharArray(), start, end);
        Assertions.assertEquals(expected, s.string());
    }

    private static Stream<Arguments> stringProvider() {
        return Stream.of(
                Arguments.of("fnewjfeih", 0, 9, "fnewjfeih"),
                Arguments.of("fnewjfeih", 3, 8, "wjfei")
        );
    }

    @ParameterizedTest
    @MethodSource("stringOffsetProvider")
    public void testStringOffset(int offset, String expected) {
        Substring s = new Substring("fnewjfeih".toCharArray(), 0, 9);
        Assertions.assertEquals(expected, s.string(offset));
    }

    private static Stream<Arguments> stringOffsetProvider() {
        return Stream.of(
                Arguments.of(9, ""),
                Arguments.of(1, "newjfeih"),
                Arguments.of(0, "fnewjfeih")
        );
    }

    @ParameterizedTest
    @MethodSource("stringOffsetOutOfIndexProvider")
    public void testStringOffsetOutOfIndex(int offset) {
        Substring s = new Substring("fnewjfeih".toCharArray(), 0, 9);
        Assertions.assertThrows(StringIndexOutOfBoundsException.class,
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
        Substring s = new Substring("fnewjfeih".toCharArray(), 0, 9);
        Assertions.assertEquals(expected, s.string(offset, length));
    }

    private static Stream<Arguments> stringOffsetLengthProvider() {
        return Stream.of(
                Arguments.of(0, 9, "fnewjfeih"),
                Arguments.of(1, 3, "new"),
                Arguments.of(9, 0, ""),
                Arguments.of(0, 0, "")
        );
    }

    @ParameterizedTest
    @MethodSource("stringOffsetLengthOutOfIndexProvider")
    public void testStringOffsetLengthOutOfIndex(int offset, int length) {
        Substring s = new Substring("fnewjfeih".toCharArray(), 0, 9);
        Assertions.assertThrows(StringIndexOutOfBoundsException.class,
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
    public void testAppendSubstringString() {
        Substring c = new Substring("aa".toCharArray(), 0, 2);
        StringBuilder builder = new StringBuilder();
        c.append(builder, "a");
        Assertions.assertEquals("a", builder.toString());
    }

    @Test
    public void testAppendSubstringPositionalArguments() {
        PositionalArguments targets = new ToStringArguments("a");
        Substring c = new Substring("aa".toCharArray(), 0, 2);
        StringBuilder builder = new StringBuilder();
        c.append(builder, targets);
        Assertions.assertEquals("a", builder.toString());
    }

    @Test
    public void testAppendSubstringPositionalArgumentsNotAvailable() {
        PositionalArguments targets = new ToStringArguments();
        Substring c = new Substring("aa".toCharArray(), 0, 2);
        StringBuilder builder = new StringBuilder();
        Assertions.assertThrows(IllegalArgumentException.class, () -> c.append(builder, targets));
    }

    @Test
    public void testLength() {
        Substring s = new Substring("aaa".toCharArray(), 0, 3);
        Assertions.assertEquals(3, s.length());
    }

    @Test
    public void testIsEmpty() {
        Substring s = new Substring("aaa".toCharArray(), 1, 1);
        Assertions.assertTrue(s.isEmpty());
    }

    @Test
    public void testIsNotEmpty() {
        Substring s = new Substring("aaa".toCharArray(), 1, 3);
        Assertions.assertFalse(s.isEmpty());
    }

    @Test
    public void testLengthIsZeroWhenEmpty() {
        Substring s = new Substring("aaa".toCharArray(), 1, 1);
        Assertions.assertEquals(0, s.length());
    }

    @Test
    public void testLengthIsNotZeroWhenPositive() {
        Substring s = new Substring("aaa".toCharArray(), 1, 2);
        Assertions.assertTrue(s.length() > 0);
    }

}
