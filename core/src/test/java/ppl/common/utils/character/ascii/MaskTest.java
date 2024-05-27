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
    @ParameterizedTest(name = "[{index}] {3}.")
    @MethodSource("bitNotProvider")
    void bitNot(Mask mask, char c, boolean expected, String display) {
        Assertions.assertEquals(expected, mask.bitNot().isSet(c));
    }

    private static Stream<Arguments> bitNotProvider() {
        return Stream.of(
                Arguments.of(Mask.mask('a', 'z'), '中', true, "The character '中' is in 'not a~z'"),
                Arguments.of(Mask.mask('a', 'z'), 'a', false, "The character 'a' is not in 'not a~z'"),
                Arguments.of(Mask.mask('a', 'z'), 'A', true, "The character 'A' is in 'not a~z'"),
                Arguments.of(Mask.OCTET, 'a', false, "The character 'a' is not in 'not OCTET'"),
                Arguments.of(Mask.OCTET, '\277', false, "The character '\\277' is not in 'not OCTET'"),
                Arguments.of(Mask.OCTET, '中', true, "The character '中' is in 'not OCTET'")
        );
    }

    @ParameterizedTest(name = "[{index}] {4}.")
    @MethodSource("bitOrProvider")
    void bitOr(Mask m1, Mask m2, char c, boolean expected, String display) {
        Assertions.assertEquals(expected, m1.bitOr(m2).isSet('^'));
        Assertions.assertEquals(expected, m2.bitOr(m1).isSet('^'));
        Assertions.assertEquals(m1.bitOr(m2).hashCode(), m2.bitOr(m1).hashCode());
        Assertions.assertEquals(m1.bitOr(m2), m2.bitOr(m1));
    }

    private static Stream<Arguments> bitOrProvider() {
        return Stream.of(
                Arguments.of(Mask.mask('A', 'Z'), Mask.mask('a', 'z'), '^', false, "The character '^' is not in 'A~Z | a~z'"),
                Arguments.of(Mask.mask('A', 'b'), Mask.mask('a', 'z'), '^', true, "The character '^' is in 'A~Z | a~z'"),
                Arguments.of(Mask.OCTET, Mask.mask('a', 'z'), 'b', true, "The character 'b' is in 'OCTET | a~z'"),
                Arguments.of(Mask.OCTET, Mask.mask('a', 'z'), '\277', true, "The character '\\277' is in 'OCTET | a~z'")
        );
    }

    @ParameterizedTest(name = "[{index}] {4}.")
    @MethodSource("bitAndProvider")
    void bitAnd(Mask m1, Mask m2, char c, boolean expected, String display) {
        Assertions.assertEquals(expected, m1.bitAnd(m2).isSet(c));
        Assertions.assertEquals(expected, m2.bitAnd(m1).isSet(c));
        Assertions.assertEquals(m1.bitOr(m2).hashCode(), m2.bitOr(m1).hashCode());
        Assertions.assertEquals(m1.bitAnd(m2), m2.bitAnd(m1));
    }


    private static Stream<Arguments> bitAndProvider() {
        return Stream.of(
                Arguments.of(Mask.mask('A', 'Z'), Mask.mask('a', 'z'), 'b', false, "The character 'b' is not in 'A~Z & a~z'"),
                Arguments.of(Mask.mask('A', 'b'), Mask.mask('a', 'z'), 'b', true, "The character 'b' is in 'A~b & a~z'"),
                Arguments.of(Mask.OCTET, Mask.mask('a', 'z'), 'b', true, "The character 'b' is in 'OCTET & a~z'")
        );
    }

    @Test
    void isNonOctet() {
        Assertions.assertFalse(Mask.NON_OCTET.isSet('a'));
        Assertions.assertFalse(Mask.NON_OCTET.isSet('\277'));
        Assertions.assertTrue(Mask.NON_OCTET.isSet('中'));
    }

    @Test
    void isNonAscii() {
        Assertions.assertFalse(Mask.NON_ASCII.isSet('a'));
        Assertions.assertTrue(Mask.NON_ASCII.isSet('\277'));
        Assertions.assertTrue(Mask.NON_ASCII.isSet('中'));
    }

    @Test
    void isOctet() {
        Assertions.assertTrue(Mask.OCTET.isSet('a'));
        Assertions.assertTrue(Mask.OCTET.isSet('\277'));
        Assertions.assertFalse(Mask.OCTET.isSet('中'));
    }

    @ParameterizedTest(name = "[{index}] {3}.")
    @MethodSource("isSetProvider")
    void isSet(Mask mask, char c, boolean expected, String display) {
        Assertions.assertEquals(expected, mask.isSet(c));
    }

    private static Stream<Arguments> isSetProvider() {
        Object[][] arguments = new Object[][]{
                {"\\000 ~ \\077", "\\000", true},
                {"\\000 ~ \\077", " ", true},
                {"\\000 ~ \\077", "\\100", false},
                {"\\000 ~ \\077", "a", false},
                {"\\000 ~ \\077", "中", false},
                {"\\100 ~ \\177", "\\000", false},
                {"\\100 ~ \\177", " ", false},
                {"\\100 ~ \\177", "\\100", true},
                {"\\100 ~ \\177", "a", true},
                {"\\100 ~ \\177", "中", false},
                {"\\200 ~ 中", "\\000", false},
                {"\\200 ~ 中", " ", false},
                {"\\200 ~ 中", "\\100", false},
                {"\\200 ~ 中", "a", false},
                {"\\200 ~ 中", "中", false},
                {"\\000 ~ \\177", "\\000", true},
                {"\\000 ~ \\177", " ", true},
                {"\\000 ~ \\177", "\\100", true},
                {"\\000 ~ \\177", "a", true},
                {"\\000 ~ \\177", "中", false},
                {"\\000 ~ 中", "\\000", true},
                {"\\000 ~ 中", " ", true},
                {"\\000 ~ 中", "\\100", true},
                {"\\000 ~ 中", "a", true},
                {"\\000 ~ 中", "中", false},
                {"\\100 ~ 中", "\\000", false},
                {"\\100 ~ 中", " ", false},
                {"\\100 ~ 中", "\\100", true},
                {"\\100 ~ 中", "a", true},
                {"\\100 ~ 中", "中", false},
                {"\\000\\077\\177a 中", "\\000", true},
                {"\\000\\077\\177a 中", "\\077", true},
                {"\\000\\077\\177a 中", "\\177", true},
                {"\\000\\077\\177a 中", "a", true},
                {"\\000\\077\\177a 中", " ", true},
                {"\\000\\077\\177a 中", "A", false},
                {"\\000\\077\\177a 中", "中", false}
        };
        List<Arguments> list = new ArrayList<>();
        for (Object[] argument : arguments) {
            Mask mask;
            String[] s = Strings.split((String) argument[0], "~");
            if (s.length == 2) {
                char begin = Characters.parse(s[0].trim()).charAt(0);
                char end = Characters.parse(s[1].trim()).charAt(0);
                mask = Mask.mask(begin, end);
            } else {
                mask = Mask.mask(Characters.parse(s[0]));
            }

            char c = Characters.parse((String) argument[1]).charAt(0);
            list.add(Arguments.of(mask, c, argument[2],
                    "The character '" + argument[1] + "' is " +
                            ((boolean) argument[2] ? "" : "not ") + "in " + argument[0]));
        }

        return Stream.of(list.toArray(new Arguments[0]));
    }

}