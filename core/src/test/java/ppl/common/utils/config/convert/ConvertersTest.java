package ppl.common.utils.config.convert;

import com.fasterxml.jackson.databind.node.*;
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

        Converters converters = new Converters();
        converters.addConverter(new Converter<>(Internal.class,
                c -> c.equals(Internal.class),
                (o, c) -> new Internal((String) o)));

        Assertions.assertEquals(new Internal("s"), converters.convert("s", Internal.class));
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
                Arguments.of(Byte.class, (byte) 1),
                Arguments.of(IntNode.class, new IntNode(1)),
                Arguments.of(LongNode.class, new LongNode(1L)),
                Arguments.of(ShortNode.class, new ShortNode((short) 1))
        );
    }

    @ParameterizedTest(name = "convert {1}({0}) to byte")
    @MethodSource("integerProvider")
    void byteValue(@SuppressWarnings("unused") Class<?> sourceType, Object source) {
        Assertions.assertEquals((byte) 1, Converters.byteValue(source));
    }

    private static Stream<Arguments> outOfByteProvider() {
        return Stream.of(
                Arguments.of(Long.class, 256L),
                Arguments.of(Integer.class, 256),
                Arguments.of(Short.class, (short) 256),
                Arguments.of(IntNode.class, new IntNode(256)),
                Arguments.of(LongNode.class, new LongNode(256L)),
                Arguments.of(ShortNode.class, new ShortNode((short) 256))
        );
    }

    @ParameterizedTest(name = "could not to convert {1}({0}) to byte")
    @MethodSource("outOfByteProvider")
    void byteValueException(@SuppressWarnings("unused") Class<?> sourceType, Object source) {
        Assertions.assertThrows(ConvertException.class, () -> Converters.byteValue(source));
    }

    @Test
    void byteValueExceptionCausedByNonWholeNumber() {
        Assertions.assertThrows(ConvertException.class, () -> Converters.byteValue(new Object()));
    }

    @ParameterizedTest(name = "convert {1}({0}) to short")
    @MethodSource("integerProvider")
    void shortValue(@SuppressWarnings("unused") Class<?> sourceType, Object source) {
        Assertions.assertEquals((short) 1, Converters.shortValue(source));
    }

    private static Stream<Arguments> outOfShortProvider() {
        return Stream.of(
                Arguments.of(Long.class, Short.MAX_VALUE + 1),
                Arguments.of(Integer.class, Short.MAX_VALUE + 1),
                Arguments.of(IntNode.class, new IntNode(Short.MAX_VALUE + 1)),
                Arguments.of(LongNode.class, new LongNode(Short.MAX_VALUE + 1))
        );
    }

    @ParameterizedTest(name = "could not to convert {1}({0}) to short")
    @MethodSource("outOfShortProvider")
    void shortValueException(@SuppressWarnings("unused") Class<?> sourceType, Object source) {
        Assertions.assertThrows(ConvertException.class, () -> Converters.shortValue(source));
    }

    @Test
    void shortValueExceptionCausedByNonWholeNumber() {
        Assertions.assertThrows(ConvertException.class, () -> Converters.shortValue(new Object()));
    }

    @ParameterizedTest(name = "convert {1}({0}) to int")
    @MethodSource("integerProvider")
    void intValue(@SuppressWarnings("unused") Class<?> sourceType, Object source) {
        Assertions.assertEquals(1, Converters.intValue(source));
    }

    private static Stream<Arguments> outOfIntProvider() {
        return Stream.of(
                Arguments.of(Long.class, Integer.MAX_VALUE + 1L),
                Arguments.of(LongNode.class, Integer.MAX_VALUE + 1L)
        );
    }

    @ParameterizedTest(name = "could not to convert {1}({0}) to int")
    @MethodSource("outOfIntProvider")
    void intValueException(@SuppressWarnings("unused") Class<?> sourceType, Object source) {
        Assertions.assertThrows(ConvertException.class, () -> Converters.intValue(source));
    }

    @Test
    void intValueExceptionCausedByNonWholeNumber() {
        Assertions.assertThrows(ConvertException.class, () -> Converters.intValue(new Object()));
    }

    @ParameterizedTest(name = "convert {1}({0}) to long")
    @MethodSource("integerProvider")
    void longValue(@SuppressWarnings("unused") Class<?> sourceType, Object source) {
        Assertions.assertEquals(1L, Converters.longValue(source));
    }

    @Test
    void longValueExceptionCausedByNonWholeNumber() {
        Assertions.assertThrows(ConvertException.class, () -> Converters.longValue(new Object()));
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

    private static Stream<Arguments> floatProvider() {
        return Stream.of(
                Arguments.of(Float.class, 1.0f),
                Arguments.of(Double.class, 1.0)
        );
    }

    @ParameterizedTest(name = "convert {1}({0}) to double")
    @MethodSource("floatProvider")
    void doubleValue(@SuppressWarnings("unused") Class<?> sourceType, Object source) {
        Assertions.assertEquals(1.0, Converters.doubleValue(source));
    }

    @Test
    void doubleValueExceptionCausedByNonRealNumber() {
        Assertions.assertThrows(ConvertException.class, () -> Converters.doubleValue(new Object()));
        Assertions.assertThrows(ConvertException.class, () -> Converters.doubleValue(new LongNode(1)));
    }

    @Test
    void boolValue() {
        Assertions.assertEquals(false, Converters.boolValue(false));
        Assertions.assertEquals(false, Converters.boolValue(BooleanNode.valueOf(false)));
    }

    @Test
    void boolValueExceptionCausedByNonBoolean() {
        Assertions.assertThrows(ConvertException.class, () -> Converters.boolValue(new Object()));
        Assertions.assertThrows(ConvertException.class, () -> Converters.boolValue(new IntNode(1)));
    }

    @Test
    void stringValue() {
        Assertions.assertEquals("", Converters.stringValue(""));
        Assertions.assertEquals("", Converters.stringValue(new TextNode("")));
    }

    @Test
    void stringValueException() {
        Assertions.assertThrows(ConvertException.class, () -> Converters.stringValue(1L));
        Assertions.assertThrows(ConvertException.class, () -> Converters.stringValue(new LongNode(1L)));
    }

    private enum Standard {
        A, B, C
    }

    private enum Coder {

        A(3),
        B(4);

        private final int code;

        Coder(int code) {
            this.code = code;
        }

        @EnumEncoder
        @SuppressWarnings("unused")
        public int getCode() {
            return code;
        }

    }

    @Test
    void enumValueByName() {
        Assertions.assertEquals(Standard.A, Converters.enumValue("A", Standard.class));
        Assertions.assertEquals(Standard.A, Converters.enumValue(new TextNode("A"), Standard.class));
    }

    @Test
    void enumValueByOrdinal() {
        Assertions.assertEquals(Standard.B, Converters.enumValue(1, Standard.class));
        Assertions.assertEquals(Standard.B, Converters.enumValue(new IntNode(1), Standard.class));
    }

    @Test
    void enumValueExceptionCausedByNotFound() {
        Assertions.assertThrows(ConvertException.class, () -> Converters.enumValue("J", Standard.class));
    }

    @Test
    void enumValueByEnumEncoder() {
        Assertions.assertEquals(Coder.A, Converters.enumValue(3, Coder.class));
        Assertions.assertEquals(Coder.A, Converters.enumValue(new IntNode(3), Coder.class));
    }

    @Test
    void enumValueByEnumEncoderExceptionCausedByNotFound() {
        Assertions.assertThrows(ConvertException.class, () -> Converters.enumValue(9, Coder.class));
    }

    @Test
    void enumValueExceptionCausedByUnsupportedKey() {
        Assertions.assertThrows(ConvertException.class, () -> Converters.enumValue(new Object(), Coder.class));
        Assertions.assertThrows(ConvertException.class, () -> Converters.enumValue(new DoubleNode(1.0), Coder.class));
    }

    @Test
    void convert() {
        Converters.defaultConvert(1, Number.class);
    }

}