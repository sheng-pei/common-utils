package ppl.common.utils.enumerate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.util.stream.Stream;

public class EnumKeyTest {
    @ParameterizedTest
    @MethodSource("unwrapProvider")
    public void testUnwrap(EnumKey key, Class<?> clazz, Object expected) {
        Assertions.assertEquals(expected, EnumKey.unwrap(key, clazz));
    }

    private static Stream<Arguments> unwrapProvider() {
        Object emptyObj = new Object();
        return Stream.of(
                Arguments.of(EnumKey.wrap(BigInteger.ONE), BigInteger.class, BigInteger.ONE),
                Arguments.of(EnumKey.wrap((byte) 1), Long.class, 1L),
                Arguments.of(EnumKey.wrap((short) 1), Long.class, 1L),
                Arguments.of(EnumKey.wrap(1), Long.class, 1L),
                Arguments.of(EnumKey.wrap(1L), Long.class, 1L),
                Arguments.of(EnumKey.wrap((byte) 1), Integer.class, 1),
                Arguments.of(EnumKey.wrap((short) 1), Integer.class, 1),
                Arguments.of(EnumKey.wrap(1), Integer.class, 1),
                Arguments.of(EnumKey.wrap(1L), Integer.class, 1),
                Arguments.of(EnumKey.wrap((byte) 1), Short.class, (short) 1),
                Arguments.of(EnumKey.wrap((short) 1), Short.class, (short) 1),
                Arguments.of(EnumKey.wrap(1), Short.class, (short) 1),
                Arguments.of(EnumKey.wrap(1L), Short.class, (short) 1),
                Arguments.of(EnumKey.wrap((byte) 1), Byte.class, (byte) 1),
                Arguments.of(EnumKey.wrap((short) 1), Byte.class, (byte) 1),
                Arguments.of(EnumKey.wrap(1), Byte.class, (byte) 1),
                Arguments.of(EnumKey.wrap(1L), Byte.class, (byte) 1),
                Arguments.of(EnumKey.wrap('c'), char.class, 'c'),
                Arguments.of(EnumKey.wrap('c'), Character.class, 'c'),
                Arguments.of(EnumKey.wrap("c"), String.class, "c"),
                Arguments.of(EnumKey.wrap(emptyObj), Object.class, emptyObj)
        );
    }

    @ParameterizedTest
    @MethodSource("errorUnwrapProvider")
    public void testErrorUnwrap(EnumKey key, Class<?> clazz) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> EnumKey.unwrap(key, clazz));
    }

    private static Stream<Arguments> errorUnwrapProvider() {
        BigInteger outLong = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE);
        return Stream.of(
                Arguments.of(EnumKey.wrap(outLong), Long.class),
                Arguments.of(EnumKey.wrap(outLong), Integer.class),
                Arguments.of(EnumKey.wrap(outLong), Short.class),
                Arguments.of(EnumKey.wrap(outLong), Byte.class),
                Arguments.of(EnumKey.wrap(outLong), long.class),
                Arguments.of(EnumKey.wrap(outLong), int.class),
                Arguments.of(EnumKey.wrap(outLong), short.class),
                Arguments.of(EnumKey.wrap(outLong), byte.class),
                Arguments.of(EnumKey.wrap(Long.MAX_VALUE), Integer.class),
                Arguments.of(EnumKey.wrap(Long.MAX_VALUE), Short.class),
                Arguments.of(EnumKey.wrap(Long.MAX_VALUE), Byte.class),
                Arguments.of(EnumKey.wrap(Long.MAX_VALUE), int.class),
                Arguments.of(EnumKey.wrap(Long.MAX_VALUE), short.class),
                Arguments.of(EnumKey.wrap(Long.MAX_VALUE), byte.class),
                Arguments.of(EnumKey.wrap(Integer.MAX_VALUE), Short.class),
                Arguments.of(EnumKey.wrap(Integer.MAX_VALUE), Byte.class),
                Arguments.of(EnumKey.wrap(Integer.MAX_VALUE), short.class),
                Arguments.of(EnumKey.wrap(Integer.MAX_VALUE), byte.class),
                Arguments.of(EnumKey.wrap(Short.MAX_VALUE), Byte.class),
                Arguments.of(EnumKey.wrap(Short.MAX_VALUE), byte.class),
                Arguments.of(EnumKey.wrap('c'), String.class),
                Arguments.of(EnumKey.wrap("c"), Character.class),
                Arguments.of(EnumKey.wrap(new Object()), String.class),
                Arguments.of(EnumKey.wrap(BigInteger.ONE), int.class)
        );
    }

    @ParameterizedTest
    @MethodSource("equalsProvider")
    public void testHashCode(EnumKey key, EnumKey key1) {
        Assertions.assertEquals(key.hashCode(), key1.hashCode());
    }

    @ParameterizedTest
    @MethodSource("equalsProvider")
    public void testEquals(EnumKey key, EnumKey key1) {
        Assertions.assertEquals(key, key1);
    }

    private static Stream<Arguments> equalsProvider() {
        Object emptyObj = new Object();
        EnumKey outLong = EnumKey.wrap(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE));
        EnumKey outLong1 = EnumKey.wrap(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE));
        EnumKey outInteger = EnumKey.wrap(Long.MAX_VALUE);
        EnumKey outInteger1 = EnumKey.wrap(Long.MAX_VALUE);
        return Stream.of(
                Arguments.of(outLong, outLong),
                Arguments.of(outLong, outLong1),
                Arguments.of(outInteger, outInteger1),
                Arguments.of(EnumKey.wrap(BigInteger.ONE), EnumKey.wrap(BigInteger.ONE)),
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
                Arguments.of(EnumKey.wrap("c"), EnumKey.wrap("c")),
                Arguments.of(EnumKey.wrap(emptyObj), EnumKey.wrap(emptyObj))
        );
    }

    @ParameterizedTest
    @MethodSource("notEqualsProvider")
    public void testNotEquals(Object key, Object key1) {
        Assertions.assertNotEquals(key, key1);
    }

    private static Stream<Arguments> notEqualsProvider() {
        return Stream.of(
                Arguments.of(EnumKey.wrap(BigInteger.ONE), EnumKey.wrap((byte) 1)),
                Arguments.of(EnumKey.wrap(BigInteger.ONE), EnumKey.wrap((short) 1)),
                Arguments.of(EnumKey.wrap(BigInteger.ONE), EnumKey.wrap(1)),
                Arguments.of(EnumKey.wrap((byte) 1), EnumKey.wrap(BigInteger.ONE)),
                Arguments.of(EnumKey.wrap((short) 1), EnumKey.wrap(BigInteger.ONE)),
                Arguments.of(EnumKey.wrap(1), EnumKey.wrap(BigInteger.ONE)),
                Arguments.of(EnumKey.wrap(1L), EnumKey.wrap(BigInteger.ONE)),
                Arguments.of(EnumKey.wrap(1), new Object()),
                Arguments.of(EnumKey.wrap(1), EnumKey.wrap(BigInteger.TEN)),
                Arguments.of(EnumKey.wrap('c'), EnumKey.wrap(1)),
                Arguments.of(EnumKey.wrap("c"), EnumKey.wrap('c')),
                Arguments.of(EnumKey.wrap(BigInteger.ONE), EnumKey.wrap(1L))
        );
    }

}
