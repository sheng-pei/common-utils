package ppl.common.utils.character.ascii;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ppl.common.utils.character.Characters;
import ppl.common.utils.string.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

class MaskTest {
    @Test
    void errorAsciiMask() {
        Assertions.assertThrows(RuntimeException.class, () -> Mask.asciiMask("中"));
        Assertions.assertThrows(RuntimeException.class, () -> Mask.asciiMask("\200"));
        Assertions.assertThrows(RuntimeException.class, () -> Mask.asciiMask('a', '中'));
        Assertions.assertThrows(RuntimeException.class, () -> Mask.asciiMask('a', '\200'));
        Assertions.assertThrows(RuntimeException.class, () -> Mask.asciiMask('\200', '中'));
    }

    @ParameterizedTest(name = "[{index}] {3}.")
    @MethodSource("bitNotProvider")
    void bitNot(Mask mask, char c, boolean expected, @SuppressWarnings("unused") String display) {
        Assertions.assertEquals(expected, mask.bitNot().isSet(c));
    }

    private static Stream<Arguments> bitNotProvider() {
        return Stream.of(
                Arguments.of(Mask.asciiMask('a', 'z'), '中', true, "The character '中' is in 'not a~z'"),
                Arguments.of(Mask.asciiMask('a', 'z'), 'a', false, "The character 'a' is not in 'not a~z'"),
                Arguments.of(Mask.asciiMask('a', 'z'), 'A', true, "The character 'A' is in 'not a~z'"),
                Arguments.of(Mask.OCTET, 'a', false, "The character 'a' is not in 'not OCTET'"),
                Arguments.of(Mask.OCTET, '\277', false, "The character '\\277' is not in 'not OCTET'"),
                Arguments.of(Mask.OCTET, '中', true, "The character '中' is in 'not OCTET'")
        );
    }

    @ParameterizedTest(name = "[{index}] {4}.")
    @MethodSource("bitOrProvider")
    void bitOr(Mask m1, Mask m2, @SuppressWarnings("unused") char c, boolean expected, @SuppressWarnings("unused") String display) {
        Assertions.assertEquals(expected, m1.bitOr(m2).isSet('^'));
        Assertions.assertEquals(expected, m2.bitOr(m1).isSet('^'));
        Assertions.assertEquals(m1.bitOr(m2).hashCode(), m2.bitOr(m1).hashCode());
        Assertions.assertEquals(m1.bitOr(m2), m2.bitOr(m1));
    }

    private static Stream<Arguments> bitOrProvider() {
        return Stream.of(
                Arguments.of(Mask.asciiMask('A', 'Z'), Mask.asciiMask('a', 'z'), '^', false, "The character '^' is not in 'A~Z | a~z'"),
                Arguments.of(Mask.asciiMask('A', 'b'), Mask.asciiMask('a', 'z'), '^', true, "The character '^' is in 'A~Z | a~z'"),
                Arguments.of(Mask.OCTET, Mask.asciiMask('a', 'z'), 'b', true, "The character 'b' is in 'OCTET | a~z'"),
                Arguments.of(Mask.OCTET, Mask.asciiMask('a', 'z'), '\277', true, "The character '\\277' is in 'OCTET | a~z'")
        );
    }

    @ParameterizedTest(name = "[{index}] {4}.")
    @MethodSource("bitAndProvider")
    void bitAnd(Mask m1, Mask m2, char c, boolean expected, @SuppressWarnings("unused") String display) {
        Assertions.assertEquals(expected, m1.bitAnd(m2).isSet(c));
        Assertions.assertEquals(expected, m2.bitAnd(m1).isSet(c));
        Assertions.assertEquals(m1.bitOr(m2).hashCode(), m2.bitOr(m1).hashCode());
        Assertions.assertEquals(m1.bitAnd(m2), m2.bitAnd(m1));
    }


    private static Stream<Arguments> bitAndProvider() {
        return Stream.of(
                Arguments.of(Mask.asciiMask('A', 'Z'), Mask.asciiMask('a', 'z'), 'b', false, "The character 'b' is not in 'A~Z & a~z'"),
                Arguments.of(Mask.asciiMask('A', 'b'), Mask.asciiMask('a', 'z'), 'b', true, "The character 'b' is in 'A~b & a~z'"),
                Arguments.of(Mask.OCTET, Mask.asciiMask('a', 'z'), 'b', true, "The character 'b' is in 'OCTET & a~z'")
        );
    }

    @ParameterizedTest(name = "[{index}] {3}.")
    @MethodSource("isSetProvider")
    void isSet(Mask mask, char c, boolean expected, @SuppressWarnings("unused") String display) {
        Assertions.assertEquals(expected, mask.isSet(c));
    }

    private static Stream<Arguments> isSetProvider() {
        return Stream.of(
                Arguments.of(Mask.asciiMask('\0', '\077'), '\0', true, "The character '\\000' is in '\\000 ~ \\077'."),
                Arguments.of(Mask.asciiMask('\0', '\077'), ' ', true, "The character ' ' is in '\\000 ~ \\077'."),
                Arguments.of(Mask.asciiMask('\0', '\077'), '\100', false, "The character '\\100' is not in '\\000 ~ \\077'."),
                Arguments.of(Mask.asciiMask('\0', '\077'), 'a', false, "The character 'a' is not in '\\000 ~ \\077'."),
                Arguments.of(Mask.asciiMask('\0', '\077'), '\200', false, "The character '\\200' is not in '\\000 ~ \\077'."),
                Arguments.of(Mask.asciiMask('\0', '\077'), '中', false, "The character '中' is not in '\\000 ~ \\077'."),
                Arguments.of(Mask.asciiMask('\100', '\177'), '\0', false, "The character '\\000' is not in '\\100 ~ \\177'."),
                Arguments.of(Mask.asciiMask('\100', '\177'), ' ', false, "The character ' ' is not in '\\100 ~ \\177'."),
                Arguments.of(Mask.asciiMask('\100', '\177'), '\100', true, "The character '\\100' is in '\\100 ~ \\177'."),
                Arguments.of(Mask.asciiMask('\100', '\177'), 'a', true, "The character 'a' is in '\\100 ~ \\177'."),
                Arguments.of(Mask.asciiMask('\100', '\177'), '\200', false, "The character '\\200' is not in '\\100 ~ \\177'."),
                Arguments.of(Mask.asciiMask('\100', '\177'), '中', false, "The character '中' is not in '\\100 ~ \\177'."),
                Arguments.of(Mask.asciiMask('\000', '\177'), '\0', true, "The character '\\000' is in '\\000 ~ \\177'."),
                Arguments.of(Mask.asciiMask('\000', '\177'), ' ', true, "The character ' ' is in '\\000 ~ \\177'."),
                Arguments.of(Mask.asciiMask('\000', '\177'), '\100', true, "The character '\\100' is in '\\000 ~ \\177'."),
                Arguments.of(Mask.asciiMask('\000', '\177'), 'a', true, "The character 'a' is in '\\000 ~ \\177'."),
                Arguments.of(Mask.asciiMask('\000', '\177'), '\200', false, "The character '\\200' is not in '\\000 ~ \\177'."),
                Arguments.of(Mask.asciiMask('\000', '\177'), '中', false, "The character '中' is not in '\\000 ~ \\177'."),
                Arguments.of(Mask.asciiMask("\0\077\177 a"), '\0', true, "The character '\\000' is in '\\000\\077\\177 a'."),
                Arguments.of(Mask.asciiMask("\0\077\177 a"), '\077', true, "The character '\\077' is in '\\000\\077\\177 a'."),
                Arguments.of(Mask.asciiMask("\0\077\177 a"), '\177', true, "The character '\\177' is in '\\000\\077\\177 a'."),
                Arguments.of(Mask.asciiMask("\0\077\177 a"), ' ', true, "The character ' ' is in '\\000\\077\\177 a'."),
                Arguments.of(Mask.asciiMask("\0\077\177 a"), 'a', true, "The character 'a' is in '\\000\\077\\177 a'."),
                Arguments.of(Mask.asciiMask("\0\077\177 a"), 'A', false, "The character 'A' is not in '\\000\\077\\177 a'."),
                Arguments.of(Mask.asciiMask("\0\077\177 a"), '\200', false, "The character '\\200' is not in '\\000\\077\\177 a'."),
                Arguments.of(Mask.asciiMask("\0\077\177 a"), '中', false, "The character '中' is not in '\\000\\077\\177 a'."),
                Arguments.of(Mask.NON_ASCII, 'a', false, "The character 'a' is ASCII."),
                Arguments.of(Mask.NON_ASCII, '\200', true, "The character '\200' is NON-ASCII."),
                Arguments.of(Mask.NON_ASCII, '中', true, "The character '中' is NON-ASCII."),
                Arguments.of(Mask.NON_OCTET, 'a', false, "The character 'a' is OCTET."),
                Arguments.of(Mask.NON_OCTET, '\200', false, "The character '\200' is OCTET."),
                Arguments.of(Mask.NON_OCTET, '中', true, "The character '中' is NON-OCTET."),
                Arguments.of(Mask.OCTET, 'a', true, "The character 'a' is OCTET."),
                Arguments.of(Mask.OCTET, '\200', true, "The character 'a' is OCTET."),
                Arguments.of(Mask.OCTET, '中', false, "The character 'a' is NON-OCTET.")
        );
    }

}