package ppl.common.utils.config.convert;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ppl.common.utils.config.ConvertException;
import ppl.common.utils.enumerate.EnumEncoder;

import java.util.Objects;
import java.util.stream.Stream;

class ConvertersTest {

    @Test
    void addConverter() {
        class Internal {
            private final String s;
            public Internal(String s) {
                this.s = s;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Internal internal = (Internal) o;
                return Objects.equals(s, internal.s);
            }

            @Override
            public int hashCode() {
                return Objects.hash(s);
            }
        }

        Converters.getInstance().addConverter(new Converter<>(Internal.class,
                c -> c.equals(Internal.class),
                (o, c) -> new Internal((String) o)));

        Assertions.assertEquals(new Internal("s"), Converters.convert("s", Internal.class));
    }

    @Test
    void charValue() {
        Assertions.assertEquals('c', Converters.charValue('c'));
    }

    @Test
    void charValueException() {
        Assertions.assertThrows(ConvertException.class, () -> Converters.charValue(1));
    }

    private static Stream<Arguments> integerProvider() {
        return Stream.of(
                Arguments.of(Long.class, 1L),
                Arguments.of(Integer.class, 1),
                Arguments.of(Short.class, (short) 1),
                Arguments.of(Byte.class, (byte) 1)
        );
    }

    @ParameterizedTest(name = "convert {1}({0}) to byte")
    @MethodSource("integerProvider")
    void byteValue(Class<?> sourceType, Object source) {
        Assertions.assertEquals((byte) 1, Converters.byteValue(source));
    }

    private static Stream<Arguments> outOfByteProvider() {
        return Stream.of(
                Arguments.of(Long.class, 256L),
                Arguments.of(Integer.class, 256),
                Arguments.of(Short.class, (short) 256)
        );
    }

    @ParameterizedTest(name = "could not to convert {1}({0}) to byte")
    @MethodSource("outOfByteProvider")
    void byteValueException(Class<?> sourceType, Object source) {
        Assertions.assertThrows(ConvertException.class, () -> Converters.byteValue(source));
    }

    @Test
    void byteValueExceptionCausedByNonWholeNumber() {
        Assertions.assertThrows(ConvertException.class, () -> Converters.byteValue(new Object()));
    }

    @ParameterizedTest(name = "convert {1}({0}) to short")
    @MethodSource("integerProvider")
    void shortValue(Class<?> sourceType, Object source) {
        Assertions.assertEquals((short) 1, Converters.shortValue(source));
    }

    private static Stream<Arguments> outOfShortProvider() {
        return Stream.of(
                Arguments.of(Long.class, Short.MAX_VALUE + 1),
                Arguments.of(Integer.class, Short.MAX_VALUE + 1)
        );
    }

    @ParameterizedTest(name = "could not to convert {1}({0}) to short")
    @MethodSource("outOfShortProvider")
    void shortValueException(Class<?> sourceType, Object source) {
        Assertions.assertThrows(ConvertException.class, () -> Converters.shortValue(source));
    }

    @Test
    void shortValueExceptionCausedByNonWholeNumber() {
        Assertions.assertThrows(ConvertException.class, () -> Converters.shortValue(new Object()));
    }

    @ParameterizedTest(name = "convert {1}({0}) to int")
    @MethodSource("integerProvider")
    void intValue(Class<?> sourceType, Object source) {
        Assertions.assertEquals(1, Converters.intValue(source));
    }

    private static Stream<Arguments> outOfIntProvider() {
        return Stream.of(
                Arguments.of(Long.class, Integer.MAX_VALUE + 1L)
        );
    }

    @ParameterizedTest(name = "could not to convert {1}({0}) to int")
    @MethodSource("outOfIntProvider")
    void intValueException(Class<?> sourceType, Object source) {
        Assertions.assertThrows(ConvertException.class, () -> Converters.intValue(source));
    }

    @Test
    void intValueExceptionCausedByNonWholeNumber() {
        Assertions.assertThrows(ConvertException.class, () -> Converters.intValue(new Object()));
    }

    @ParameterizedTest(name = "convert {1}({0}) to long")
    @MethodSource("integerProvider")
    void longValue(Class<?> sourceType, Object source) {
        Assertions.assertEquals(1L, Converters.longValue(source));
    }

    @Test
    void longValueExceptionCausedByNonWholeNumber() {
        Assertions.assertThrows(ConvertException.class, () -> Converters.longValue(new Object()));
    }

    private static Stream<Arguments> floatProvider() {
        return Stream.of(
                Arguments.of(Float.class, 1.0f),
                Arguments.of(Double.class, 1.0)
        );
    }

    @Test
    void floatValue() {
        Assertions.assertEquals(1.0f, Converters.floatValue(1.0f));
    }

    @Test
    void floatValueException() {
        Assertions.assertThrows(ConvertException.class, () -> Converters.floatValue(1.0));
        Assertions.assertThrows(ConvertException.class, () -> Converters.floatValue(new Object()));
    }

    @ParameterizedTest(name = "convert {1}({0}) to double")
    @MethodSource("floatProvider")
    void doubleValue(Class<?> sourceType, Object source) {
        Assertions.assertEquals(1.0, Converters.doubleValue(source));
    }

    @Test
    void doubleValueExceptionCausedByNonRealNumber() {
        Assertions.assertThrows(ConvertException.class, () -> Converters.doubleValue(new Object()));
    }

    @Test
    void stringValue() {
        Assertions.assertEquals("", Converters.stringValue(""));
    }

    @Test
    void stringValueException() {
        Assertions.assertThrows(ConvertException.class, () -> Converters.stringValue(1L));
    }

    private enum Standard {
        A, B, C;
    }

    private enum Coder {

        A(3),
        B(4);

        private final int code;

        Coder(int code) {
            this.code = code;
        }

        @EnumEncoder
        public int getCode() {
            return code;
        }

    }

    @Test
    void enumValueByName() {
        Assertions.assertEquals(Standard.A, Converters.enumValue("A", Standard.class));
    }

    @Test
    void enumValueByOrdinal() {
        Assertions.assertEquals(Standard.B, Converters.enumValue(1, Standard.class));
    }

    @Test
    void enumValueExceptionCausedByNotFound() {
        Assertions.assertThrows(ConvertException.class, () -> Converters.enumValue("J", Standard.class));
    }

    @Test
    void enumValueByEnumEncoder() {
        Assertions.assertEquals(Coder.A, Converters.enumValue(3, Coder.class));
    }

    @Test
    void enumValueByEnumEncoderExceptionCausedByNotFound() {
        Assertions.assertThrows(ConvertException.class, () -> Converters.enumValue(9, Coder.class));
    }

    @Test
    void convert() {
        Converters.convert(1, Number.class);
    }

}