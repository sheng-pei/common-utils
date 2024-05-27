package ppl.common.utils.character.ascii;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class MaskCharPredicateTest {
    @ParameterizedTest(name = "[{index}] {3}.")
    @MethodSource("testProvider")
    void test(MaskCharPredicate predicate, Character c, boolean expected, String display) {
        Assertions.assertEquals(expected, predicate.test(c));
    }

    private static Stream<Arguments> testProvider() {
        return Stream.of(
                Arguments.of(AsciiGroup.ALPHA, null, false, "The character NULL is not an ALPHA"),
                Arguments.of(AsciiGroup.ALPHA, 'a', true, "The character 'a' is an ALPHA"),
                Arguments.of(AsciiGroup.ALPHA, 'A', true, "The character 'A' is an ALPHA"),
                Arguments.of(AsciiGroup.ALPHA, ',', false, "The character ',' is not an ALPHA")
        );
    }

    @Test
    void or() {
        Assertions.assertTrue(AsciiGroup.ALPHA.or(AsciiGroup.HEX_DIGIT).test('9'));
        Assertions.assertTrue(AsciiGroup.ALPHA.or(c -> c == '9').test('9'));
    }

    @Test
    void and() {
        Assertions.assertTrue(AsciiGroup.ALPHA.and(AsciiGroup.HEX_DIGIT).test('a'));
        Assertions.assertFalse(AsciiGroup.ALPHA.and(c -> c == '9').test('a'));
    }

    @Test
    void negate() {
        Assertions.assertTrue(AsciiGroup.ALPHA.negate().test('中'));
        Assertions.assertFalse(AsciiGroup.ALPHA.negate().test('a'));
        Assertions.assertTrue(AsciiGroup.ALPHA.negate().test('9'));
    }
}