package ppl.common.utils.character.ascii;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MaskCharacterPredicateTest {
    @ParameterizedTest(name = "[{index}] {3}.")
    @MethodSource("testProvider")
    void test(MaskCharacterPredicate predicate, Character c, boolean expected, String display) {
        Assertions.assertEquals(expected, predicate.test(c));
    }

    private static Stream<Arguments> testProvider() {
        return Stream.of(
                Arguments.of(AsciiPredicates.ALPHA, null, false, "The character NULL is not an ALPHA"),
                Arguments.of(AsciiPredicates.ALPHA, 'a', true, "The character 'a' is an ALPHA"),
                Arguments.of(AsciiPredicates.ALPHA, 'A', true, "The character 'A' is an ALPHA"),
                Arguments.of(AsciiPredicates.ALPHA, ',', false, "The character ',' is not an ALPHA")
        );
    }

    @Test
    void or() {
        Assertions.assertTrue(AsciiPredicates.ALPHA.or(AsciiPredicates.HEX).test('9'));
        Assertions.assertTrue(AsciiPredicates.ALPHA.or(c -> c == '9').test('9'));
    }

    @Test
    void and() {
        Assertions.assertTrue(AsciiPredicates.ALPHA.and(AsciiPredicates.HEX).test('a'));
        Assertions.assertFalse(AsciiPredicates.ALPHA.and(c -> c == '9').test('a'));
    }

    @Test
    void negate() {
        Assertions.assertTrue(AsciiPredicates.ALPHA.negate().test('中'));
        Assertions.assertFalse(AsciiPredicates.ALPHA.negate().test('a'));
        Assertions.assertTrue(AsciiPredicates.ALPHA.negate().test('9'));
    }
}