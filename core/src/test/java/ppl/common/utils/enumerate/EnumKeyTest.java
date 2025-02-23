package ppl.common.utils.enumerate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class EnumKeyTest {

    @ParameterizedTest
    @MethodSource("equalsProvider")
    @SuppressWarnings("all")
    public void testHashCode(EnumKey key, EnumKey key1) {
        Assertions.assertEquals(key.hashCode(), key1.hashCode());
    }

    @ParameterizedTest
    @MethodSource("equalsProvider")
    @SuppressWarnings("all")
    public void testEquals(EnumKey key, EnumKey key1) {
        Assertions.assertEquals(key, key1);
    }

    private static Stream<Arguments> equalsProvider() {
        Object emptyObj = new Object();
        EnumKey outInteger = EnumKey.wrap(Long.MAX_VALUE);
        EnumKey outInteger1 = EnumKey.wrap(Long.MAX_VALUE);
        return Stream.of(
                Arguments.of(outInteger, outInteger1),
                Arguments.of(EnumKey.wrap((byte) 1), EnumKey.wrap(1L)),
                Arguments.of(EnumKey.wrap((short) 1), EnumKey.wrap(1L)),
                Arguments.of(EnumKey.wrap(1), EnumKey.wrap(1L)),
                Arguments.of(EnumKey.wrap(1L), EnumKey.wrap(1L)),
                Arguments.of(EnumKey.wrap((byte) 1), EnumKey.wrap(1)),
                Arguments.of(EnumKey.wrap((short) 1), EnumKey.wrap(1)),
                Arguments.of(EnumKey.wrap(1), EnumKey.wrap(1)),
                Arguments.of(EnumKey.wrap(1L), EnumKey.wrap(1)),
                Arguments.of(EnumKey.wrap((byte) 1), EnumKey.wrap((short) 1)),
                Arguments.of(EnumKey.wrap((short) 1), EnumKey.wrap((short) 1)),
                Arguments.of(EnumKey.wrap(1), EnumKey.wrap((short) 1)),
                Arguments.of(EnumKey.wrap(1L), EnumKey.wrap((short) 1)),
                Arguments.of(EnumKey.wrap((byte) 1), EnumKey.wrap((byte) 1)),
                Arguments.of(EnumKey.wrap((short) 1), EnumKey.wrap((byte) 1)),
                Arguments.of(EnumKey.wrap(1), EnumKey.wrap((byte) 1)),
                Arguments.of(EnumKey.wrap(1L), EnumKey.wrap((byte) 1)),
                Arguments.of(EnumKey.wrap('c'), EnumKey.wrap('c')),
                Arguments.of(EnumKey.wrap("c"), EnumKey.wrap("c"))
        );
    }

    @ParameterizedTest
    @MethodSource("notEqualsProvider")
    public void testNotEquals(Object key, Object key1) {
        Assertions.assertNotEquals(key, key1);
    }

    private static Stream<Arguments> notEqualsProvider() {
        return Stream.of(
                Arguments.of(EnumKey.wrap('c'), EnumKey.wrap(1)),
                Arguments.of(EnumKey.wrap("c"), EnumKey.wrap('c'))
        );
    }

}
