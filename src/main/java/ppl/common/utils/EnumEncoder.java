package ppl.common.utils;

import java.lang.annotation.*;
import java.math.BigInteger;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnumEncoder {

    Set<Class<?>> VALID_ENUM_KEY_TYPE = Collections.unmodifiableSet(new LinkedHashSet<Class<?>>() {
        {
            this.add(byte.class);
            this.add(short.class);
            this.add(int.class);
            this.add(long.class);

            this.add(char.class);

            this.add(Byte.class);
            this.add(Short.class);
            this.add(Integer.class);
            this.add(Long.class);
            this.add(BigInteger.class);

            this.add(Character.class);
            this.add(String.class);
        }
    });

    @SuppressWarnings("rawtypes")
    enum ERROR {
        PARAMETER_UNMATCHED("The encoder required no parameters if the enum({}) support encoder protocol"),
        INVALID_RETURN_TYPE("The return type of the encoder must be in " + validEnumKeyTypeDesc() + " if the enum({}) support encoder protocol"),
        MULTIPLE_ENCODER("Only one encoder is admit if the enum({}) support encoder protocol"),
        ILLEGAL_ACCESS("The encoder of the enum({}) is not accessible"),
        INVOCATION_ERROR("Failed to apply the encoder to some element of enum({})"),
        DUPLICATE_ENUM_KEYS("There are same keys in the enum({})"),
        NO_ENUM_ENCODER("The enum({}) not support encoder protocol");

        ERROR(String cause) {
            this.cause = cause;
        }

        private final String cause;

        public String causeOf(Class<? extends Enum> enumClass) {
            return StringUtils.format(this.cause, enumClass.getCanonicalName());
        }

        private static String validEnumKeyTypeDesc() {
            StringBuilder descBuilder = new StringBuilder("[");
            for (Class<?> type : VALID_ENUM_KEY_TYPE) {
                descBuilder.append(type.getSimpleName()).append(',');
            }
            descBuilder.setLength(descBuilder.length() - 1);
            descBuilder.append(']');
            return descBuilder.toString();
        }

    }

}
