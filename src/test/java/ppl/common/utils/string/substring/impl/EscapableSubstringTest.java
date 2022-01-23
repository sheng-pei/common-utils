package ppl.common.utils.string.substring.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ppl.common.utils.helper.EqualsTester;
import ppl.common.utils.string.substring.PositionalArguments;

import java.util.stream.Stream;

public class EscapableSubstringTest {

    @Test
    public void testConstructEscapableSubstring() {
        new EscapableSubstring("jjfhi".toCharArray(), 0, 4, 5);
    }

    @ParameterizedTest
    @MethodSource("constructEscapableSubstringOutOfIndexProvider")
    public void testConstructEscapableSubstringOutOfIndex(String source, int start, int firstUnescape, int end) {
        Assertions.assertThrows(StringIndexOutOfBoundsException.class,
                () -> new EscapableSubstring(source.toCharArray(), start, firstUnescape, end));
    }

    private static Stream<Arguments> constructEscapableSubstringOutOfIndexProvider() {
        return Stream.of(
                Arguments.of("aaaa", 1, 0, 3),
                Arguments.of("aaaa", 1, 3, 3)
        );
    }

    @Test
    public void testEscapableSubstringEquals() {
        EscapableSubstring s1 = new EscapableSubstring("fnewjfeih".toCharArray(), 3, 4, 8);
        EscapableSubstring s2 = new EscapableSubstring("fnewjfeih".toCharArray(), 3, 4, 8);
        EscapableSubstring s3 = new EscapableSubstring("fnewjfeih".toCharArray(), 3, 4, 8);

        EqualsTester tester = new EqualsTester();
        tester.setAssertMethod(Assertions::assertTrue);
        tester.addGroup(s1, s2, s3);
        tester.addGroup(new EscapableSubstring("fnewjfei".toCharArray(), 3, 4, 8));
        tester.addGroup(new EscapableSubstring("fnewjfeih".toCharArray(), 2, 4, 8));
        tester.addGroup(new EscapableSubstring("fnewjfeih".toCharArray(), 3, 3, 8));
        tester.addGroup(new EscapableSubstring("fnewjfeih".toCharArray(), 3, 4, 9));

        tester.test();
    }

    @ParameterizedTest
    @MethodSource({"appendEscapableSubstringProvider"})
    public void testAppendEscapableSubstring(EscapableSubstring substring, String arg, String expected) {
        StringBuilder builder = new StringBuilder();
        substring.append(builder, arg);
        Assertions.assertEquals(expected, builder.toString());
    }

    private static Stream<Arguments> appendEscapableSubstringProvider() {
        return Stream.of(
                Arguments.of(new EscapableSubstring("aa{}bb".toCharArray(), 2, 2, 4), "a", "a"),
                Arguments.of(new EscapableSubstring("aa\\{}bb".toCharArray(), 2, 3, 5), "a", "{}"),
                Arguments.of(new EscapableSubstring("aa\\\\{}bb".toCharArray(), 2, 4, 6), "a", "\\a"),
                Arguments.of(new EscapableSubstring("aa\\\\\\{}bb".toCharArray(), 2, 5, 7), "a", "\\{}")
        );
    }

}
