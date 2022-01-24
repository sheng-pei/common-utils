package ppl.common.utils.config.convert;

import ppl.common.utils.Condition;
import ppl.common.utils.enumerate.EnumUtils;
import ppl.common.utils.StringUtils;
import ppl.common.utils.TypeUtils;
import ppl.common.utils.enumerate.EnumEncoderNotSupportedException;
import ppl.common.utils.enumerate.UnknownEnumException;
import ppl.common.utils.logging.Logger;
import ppl.common.utils.logging.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class Converter<C> {

    private static final Logger logger = LoggerFactory.getLogger(Converter.class);

    private static final String INCOMPATIBLE_TYPE_MESSAGE = "Incompatible with {}";

//    private static class EnumConverter<C extends Enum<C>> extends Converter<C> {
//        private EnumConverter(Class<C> targetClass) {
//            super(o -> {
//                if (o == null) {
//                    return null;
//                }
//
//                try {
//                    return EnumUtils.enumOf(targetClass, o);
//                } catch (UnknownEnumException e) {
//                    throw new ConvertException(Converter.incompatibleWith(targetClass), e);
//                }
//            });
//        }
//    }

//    public static <C> Converter<C> getInstance(Class<C> targetClass) {
//
//        if (targetClass == null) {
//            throw new IllegalArgumentException("TargetClass must not be null.");
//        }
//
//        if (targetClass == void.class) {
//            throw new IllegalArgumentException("TargetClass must not be void.");
//        }
//
//        if (Enum.class.isAssignableFrom(targetClass)) {
//            @SuppressWarnings({"unchecked", "rawtypes"})
//            Class<? extends Enum> enumClass = (Class<? extends Enum>) targetClass;
//            try {
//                EnumUtils.checkEncodeSupport(enumClass);
//            } catch (EnumEncoderNotSupportedException e) {
//                throw new IllegalArgumentException("TargetClass is not support enum encoder.", e);
//            }
//        }
//
//        Converter<?> converter = CONVERTERS.get(targetClass);
//        if (converter == null) {
//            if (Enum.class.isAssignableFrom(targetClass)) {
//                @SuppressWarnings({"rawtypes", "unchecked"})
//                EnumConverter<?> enumConverter = new EnumConverter(targetClass);
//                converter = enumConverter;
//            } else {
//                converter = new CastConverter<>(targetClass);
//            }
//        }
//        @SuppressWarnings("unchecked")
//        Converter<C> res = (Converter<C>) converter;
//        return res;
//    }

    private final String name;
    private final Function<Object, C> convertFunc;

    public Converter(String name, Function<Object, C> convertFunc) {
        this.name = name;
        this.convertFunc = convertFunc;
    }

    public Converter(Class<?> targetClazz, Function<Object, C> convertFunc) {
        this.name = targetClazz.getCanonicalName();
        this.convertFunc = convertFunc;
    }

    public String name() {
        return this.name;
    }

    public C convert(Object obj) {
        try {
            return this.convertFunc.apply(obj);
        } catch (Throwable t) {
            throw new ConvertException(StringUtils.format(INCOMPATIBLE_TYPE_MESSAGE, this.name));
        }
    }

    public static Character charValue(Object obj) {
        return internalConvert(obj, Character.class);
    }

    public static Byte byteValue(Object obj) {
        return internalConvert(obj, Byte.class);
    }

    public static Short shortValue(Object obj) {
        return internalConvert(obj, Short.class);
    }

    public static Integer intValue(Object obj) {
        return internalConvert(obj, Integer.class);
    }

    public static Long longValue(Object obj) {
        return internalConvert(obj, Long.class);
    }

    public static Double doubleValue(Object obj) {
        return internalConvert(obj, Double.class);
    }

    public static String stringValue(Object obj) {
        return internalConvert(obj, String.class);
    }

    public static <T> T convert(Object obj, Class<T> clazz) {
        Converter<T> converter = Converters.getInstance().getConverter(clazz);
        if (converter == null) {
            return clazz.cast(obj);
        }
        return converter.convert(obj);
    }

    private static <T> T internalConvert(Object obj, Class<T> clazz) {
        Converter<T> converter = Converters.getInstance().getConverter(clazz);
        assert converter != null : "No " + converter.name() + " converter.";
        return converter.convert(obj);
    }

}