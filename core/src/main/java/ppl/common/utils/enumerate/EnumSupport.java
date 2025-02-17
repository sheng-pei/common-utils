package ppl.common.utils.enumerate;

import ppl.common.utils.string.Strings;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

class EnumSupport {
    @SuppressWarnings("rawtypes")
    enum ERROR {
        PARAMETER_UNMATCHED("The encoder required no parameters if the enum({}) support encoder protocol"),
        INVALID_RETURN_TYPE("The return type of the encoder must be in " + classesToString(EnumKey.getSupported()) + " if the enum({}) support encoder protocol"),
        MULTIPLE_ENCODER("Only one encoder is admit if the enum({}) support encoder protocol"),
        ILLEGAL_ACCESS("The encoder of the enum({}) is not accessible"),
        INVOCATION_ERROR("Failed to apply the encoder to some element of enum({})"),
        DUPLICATE_ENUM_KEYS("There are same keys in the enum({})"),
        NO_ENUM_ENCODER("The enum({}) not support encoder protocol"),
        NULL_KEY("The key of enum({}) must not be null");

        ERROR(String cause) {
            this.cause = cause;
        }

        private final String cause;

        public String causeOf(Class<? extends Enum> enumClass) {
            return Strings.format(this.cause, enumClass.getCanonicalName());
        }

    }

    private static String classesToString(Set<Class<?>> classes) {
        StringBuilder descBuilder = new StringBuilder("[");
        for (Class<?> type : classes) {
            descBuilder.append(type.getSimpleName()).append(',');
        }
        descBuilder.setLength(descBuilder.length() - 1);
        descBuilder.append(']');
        return descBuilder.toString();
    }

    static boolean isError(Object encoder) {
        return encoder instanceof ERROR;
    }

    @SuppressWarnings("rawtypes")
    static void check(Object encoder, Class<? extends Enum> enumClass) {
        if (encoder instanceof ERROR) {
            throw new EnumEncoderNotSupportedException((ERROR) encoder, enumClass);
        }
    }

    @SuppressWarnings("rawtypes")
    static Object loadEncodeMethod(Class<? extends Enum> enumClass) {
        List<Method> encoders = new ArrayList<>();
        Method[] methods = enumClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(EnumEncoder.class)) {
                if (method.getParameterCount() != 0) {
                    return ERROR.PARAMETER_UNMATCHED;
                }
                Class<?> returnType = method.getReturnType();
                if (!EnumKey.isSupported(returnType)) {
                    return ERROR.INVALID_RETURN_TYPE;
                }
                if (!encoders.isEmpty()) {
                    return ERROR.MULTIPLE_ENCODER;
                }
                encoders.add(method);
            }
        }

        if (encoders.isEmpty()) {
            return ERROR.NO_ENUM_ENCODER;
        }
        return encoders.get(0);
    }

    static Object applyEncoder(Enum<?>[] enums, Method encoder, Map<EnumKey, Enum<?>> keyToEnum) {
        for (Enum<?> e : enums) {
            try {
                encoder.setAccessible(true);
                Object key = encoder.invoke(e);
                if (key == null) {
                    return ERROR.NULL_KEY;
                }

                EnumEncoder enumEncoder = encoder.getAnnotation(EnumEncoder.class);
                EnumKey enumKey = EnumKey.wrap(key, !enumEncoder.caseSensitive());
                if (keyToEnum.containsKey(enumKey)) {
                    return ERROR.DUPLICATE_ENUM_KEYS;
                }
                keyToEnum.put(enumKey, e);
            } catch (IllegalAccessException exception) {
                return ERROR.ILLEGAL_ACCESS;
            } catch (InvocationTargetException exception) {
                return ERROR.INVOCATION_ERROR;
            }
        }
        return null;
    }

}
