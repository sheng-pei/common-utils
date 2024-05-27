package ppl.common.utils.enumerate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.util.stream.Stream;

public class EnumUtilsTest {
    enum ByteEnum {
        U((byte) 1);

        private final byte code;
        ByteEnum(byte code) {
            this.code = code;
        }

        @EnumEncoder
        private byte getCode() {
            return this.code;
        }
    }

    enum ShortEnum {
        U((short) 1);

        private final short code;
        ShortEnum(short code) {
            this.code = code;
        }

        @EnumEncoder
        private short getCode() {
            return this.code;
        }
    }

    enum IntEnum {
        U(1);

        private final int code;
        IntEnum(int code) {
            this.code = code;
        }

        @EnumEncoder
        private int getCode() {
            return this.code;
        }
    }

    enum LongEnum {
        U(1L);

        private final long code;
        LongEnum(long code) {
            this.code = code;
        }

        @EnumEncoder
        private long getCode() {
            return this.code;
        }
    }

    enum CharEnum {
        U('c');

        private final char code;
        CharEnum(char code) {
            this.code = code;
        }

        @EnumEncoder
        private char getCode() {
            return this.code;
        }
    }

    enum StringEnum {
        U("c");

        private final String code;
        StringEnum(String code) {
            this.code = code;
        }

        @EnumEncoder
        private String getCode() {
            return this.code;
        }
    }

    enum WByteEnum {
        U((byte) 1);

        private final Byte code;
        WByteEnum(Byte code) {
            this.code = code;
        }

        @EnumEncoder
        private Byte getCode() {
            return this.code;
        }
    }

    enum WShortEnum {
        U((short) 1);

        private final Short code;
        WShortEnum(Short code) {
            this.code = code;
        }

        @EnumEncoder
        private Short getCode() {
            return this.code;
        }
    }

    enum WIntEnum {
        U(1);

        private final Integer code;
        WIntEnum(Integer code) {
            this.code = code;
        }

        @EnumEncoder
        private Integer getCode() {
            return this.code;
        }
    }

    enum WLongEnum {
        U(1L);

        private final Long code;
        WLongEnum(Long code) {
            this.code = code;
        }

        @EnumEncoder
        private Long getCode() {
            return this.code;
        }
    }

    enum WCharEnum {
        U('c');

        private final Character code;
        WCharEnum(Character code) {
            this.code = code;
        }

        @EnumEncoder
        private Character getCode() {
            return this.code;
        }
    }

    @Test
    public void testUnknownEnum() {
        Assertions.assertThrows(UnknownEnumException.class, () -> EnumUtils.enumOf(IntEnum.class, 2));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @ParameterizedTest
    @MethodSource("enumOfProvider")
    public void testEnumOf(Class<? extends Enum> clazz, Object key, Enum e) {
        Assertions.assertEquals(e, EnumUtils.enumOf(clazz, key));
    }

    private static Stream<Arguments> enumOfProvider() {
        return Stream.of(
                Arguments.of(ByteEnum.class, (byte) 1, ByteEnum.U),
                Arguments.of(ShortEnum.class, (short) 1, ShortEnum.U),
                Arguments.of(IntEnum.class, 1, IntEnum.U),
                Arguments.of(LongEnum.class, 1L, LongEnum.U),
                Arguments.of(CharEnum.class, 'c', CharEnum.U),
                Arguments.of(StringEnum.class, "c", StringEnum.U),
                Arguments.of(WByteEnum.class, (byte) 1, WByteEnum.U),
                Arguments.of(WShortEnum.class, (short) 1, WShortEnum.U),
                Arguments.of(WIntEnum.class, 1, WIntEnum.U),
                Arguments.of(WLongEnum.class, 1L, WLongEnum.U),
                Arguments.of(WCharEnum.class, 'c', WCharEnum.U)
        );
    }

    @Test
    public void testFailedEncode() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> EnumUtils.encode(IntEnum.U, char.class));
    }

    @SuppressWarnings("rawtypes")
    @ParameterizedTest
    @MethodSource("encodeProvider")
    public void testEncode(Object expected, Enum e, Class<?> type) {
        Assertions.assertEquals(expected, EnumUtils.encode(e, type));
    }

    private static Stream<Arguments> encodeProvider() {
        return Stream.of(
                Arguments.of(1, ByteEnum.U, Integer.class),
                Arguments.of(1, ShortEnum.U, Integer.class),
                Arguments.of(1, IntEnum.U, Integer.class),
                Arguments.of(1, LongEnum.U, Integer.class),
                Arguments.of('c', CharEnum.U, Character.class),
                Arguments.of("c", StringEnum.U, String.class),
                Arguments.of(1, WByteEnum.U, int.class),
                Arguments.of(1, WShortEnum.U, int.class),
                Arguments.of(1, WIntEnum.U, int.class),
                Arguments.of(1, WLongEnum.U, int.class),
                Arguments.of('c', WCharEnum.U, char.class)
        );
    }

}
