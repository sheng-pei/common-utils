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
                Arguments.of(Mask.mask('a', 'z'), 'A', true, "The character 'A' is in 'not a~z'")
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
                Arguments.of(Mask.mask('A', 'b'), Mask.mask('a', 'z'), '^', true, "The character '^' is in 'A~Z | a~z'")
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
                Arguments.of(Mask.mask('A', 'b'), Mask.mask('a', 'z'), 'b', true, "The character 'b' is in 'A~b & a~z'")
        );
    }

    @ParameterizedTest(name = "[{index}] {3}.")
    @MethodSource("isSetProvider")
    void isSet(Mask mask, char c, boolean expected, String display) {
        Assertions.assertEquals(expected, mask.isSet(c));
    }

    private static Stream<Arguments> isSetProvider() {
        Object[][] arguments = new Object[][]{
                {"\\000 ~ \\000", "\\000", false},
                {"\\000 ~ \\000", " ", false},
                {"\\000 ~ \\000", "\\100", false},
                {"\\000 ~ \\000", "a", false},
                {"\\000 ~ \\000", "中", false},
                {"\\001 ~ \\077", "\\000", false},
                {"\\001 ~ \\077", " ", true},
                {"\\001 ~ \\077", "\\100", false},
                {"\\001 ~ \\077", "a", false},
                {"\\001 ~ \\077", "中", false},
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
                {"\\000 ~ \\077", "\\000", false},
                {"\\000 ~ \\077", " ", true},
                {"\\000 ~ \\077", "\\100", false},
                {"\\000 ~ \\077", "a", false},
                {"\\000 ~ \\077", "中", false},
                {"\\000 ~ \\177", "\\000", false},
                {"\\000 ~ \\177", " ", true},
                {"\\000 ~ \\177", "\\100", true},
                {"\\000 ~ \\177", "a", true},
                {"\\000 ~ \\177", "中", false},
                {"\\000 ~ 中", "\\000", false},
                {"\\000 ~ 中", " ", true},
                {"\\000 ~ 中", "\\100", true},
                {"\\000 ~ 中", "a", true},
                {"\\000 ~ 中", "中", false},
                {"\\001 ~ \\177", "\\000", false},
                {"\\001 ~ \\177", " ", true},
                {"\\001 ~ \\177", "\\100", true},
                {"\\001 ~ \\177", "a", true},
                {"\\001 ~ \\177", "中", false},
                {"\\001 ~ 中", "\\000", false},
                {"\\001 ~ 中", " ", true},
                {"\\001 ~ 中", "\\100", true},
                {"\\001 ~ 中", "a", true},
                {"\\001 ~ 中", "中", false},
                {"\\100 ~ 中", "\\000", false},
                {"\\100 ~ 中", " ", false},
                {"\\100 ~ 中", "\\100", true},
                {"\\100 ~ 中", "a", true},
                {"\\100 ~ 中", "中", false},
                {"\\000\\077\\177a 中", "\\000", false},
                {"\\000\\077\\177a 中", "\\077", true},
                {"\\000\\077\\177a 中", "\\177", true},
                {"\\000\\077\\177a 中", "a", true},
                {"\\000\\077\\177a 中", " ", true},
                {"\\000\\077\\177a 中", "A", false},
                {"\\000\\077\\177a 中", "中", false},
                {"CHARACTER_EXCEPT_NON_NUL_ASCII", "\\000", true},
                {"CHARACTER_EXCEPT_NON_NUL_ASCII", "\\077", false},
                {"CHARACTER_EXCEPT_NON_NUL_ASCII", "\\177", false},
                {"CHARACTER_EXCEPT_NON_NUL_ASCII", "a", false},
                {"CHARACTER_EXCEPT_NON_NUL_ASCII", " ", false},
                {"CHARACTER_EXCEPT_NON_NUL_ASCII", "A", false},
                {"CHARACTER_EXCEPT_NON_NUL_ASCII", "中", true},
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
                if (s[0].equals("CHARACTER_EXCEPT_NON_NUL_ASCII")) {
                    mask = Mask.CHARACTER_EXCEPT_NON_NUL_ASCII;
                } else {
                    mask = Mask.mask(Characters.parse(s[0]));
                }
            }

            char c = Characters.parse((String) argument[1]).charAt(0);
            if (mask == Mask.CHARACTER_EXCEPT_NON_NUL_ASCII) {
                list.add(Arguments.of(mask, c, argument[2], "The character '" + argument[1] + "' is " + ((boolean) argument[2] ? "" : "not ") + "a character except nonnull ascii"));
            } else {
                list.add(Arguments.of(mask, c, argument[2],
                        "The character '" + argument[1] + "' is " + ((boolean) argument[2] ? "" : "not ") + "in " + argument[0]));
            }
        }

        return Stream.of(list.toArray(new Arguments[0]));
    }

}