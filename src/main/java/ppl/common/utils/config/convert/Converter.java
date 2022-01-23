package ppl.common.utils.config.convert;

import ppl.common.utils.Condition;
import ppl.common.utils.enumerate.EnumUtils;
import ppl.common.utils.StringUtils;
import ppl.common.utils.TypeUtils;
import ppl.common.utils.enumerate.EnumEncoderNotSupportedException;
import ppl.common.utils.enumerate.UnknownEnumException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class Converter<C> {

    private static final String INCOMPATIBLE_TYPE_MESSAGE = "Incompatible with {}";
    private static final Map<Class<?>, Converter<?>> SYSTEM_CONVERTERS = new HashMap<>();
    private static final Map<Class<?>, Converter<?>> CUSTOM_CONVERTERS = new HashMap<>();

    static {
        Converter<Byte> byteConverter = new Converter<>("byte", o -> {
            if (o == null) {
                return null;
            }

            if (isInteger(o)) {
                long v = ((Number) o).longValue();
                if (inByte(v)) {
                    return (byte) v;
                }
            }
            throw new IllegalArgumentException();
        });
        SYSTEM_CONVERTERS.put(Byte.class, byteConverter);
        SYSTEM_CONVERTERS.put(byte.class, byteConverter);

        Converter<Short> shortConverter = new Converter<>("short", o -> {
            if (o == null) {
                return null;
            }

            if (isInteger(o)) {
                long v = ((Number) o).longValue();
                if (inShort(v)) {
                    return (short) v;
                }
            }
            throw new IllegalArgumentException();
        });
        SYSTEM_CONVERTERS.put(Short.class, shortConverter);
        SYSTEM_CONVERTERS.put(short.class, shortConverter);

        Converter<Integer> intConverter = new Converter<>("int", o -> {
            if (o == null) {
                return null;
            }
            if (isInteger(o)) {
                if (o instanceof Integer) {
                    return (Integer) o;
                } else if (!(o instanceof Long) || inInt((Long) o)) {
                    return ((Number) o).intValue();
                }
            }
            throw new IllegalArgumentException();
        });
        SYSTEM_CONVERTERS.put(Integer.class, intConverter);
        SYSTEM_CONVERTERS.put(int.class, intConverter);

        Converter<Long> longConverter = new Converter<>("long", o -> {
            if (o == null) {
                return null;
            }
            if (isInteger(o)) {
                if (o instanceof Long) {
                    return (Long) o;
                }
                return ((Number) o).longValue();
            }
            throw new IllegalArgumentException();
        });
        SYSTEM_CONVERTERS.put(Long.class, longConverter);
        SYSTEM_CONVERTERS.put(long.class, longConverter);

        Converter<Double> doubleConverter = new Converter<>("double", o -> {
            if (o == null) {
                return null;
            }
            if (o instanceof Float) {
                return ((Float) o).doubleValue();
            } else if (o instanceof Double) {
                return (Double) o;
            }
            throw new IllegalArgumentException();
        });
        SYSTEM_CONVERTERS.put(Double.class, doubleConverter);
        SYSTEM_CONVERTERS.put(double.class, doubleConverter);

        Converter<String> stringConverter = new Converter<>("string",
                String.class::cast);
        SYSTEM_CONVERTERS.put(String.class, stringConverter);
    }

    private final static Set<Class<?>> INTEGER_TYPE = new HashSet<Class<?>>() {
        {
            add(Byte.class);
            add(Short.class);
            add(Integer.class);
            add(Long.class);
        }
    };

    private static boolean isInteger(Object source) {
        return INTEGER_TYPE.contains(source.getClass());
    }

    private static boolean inInt(Long l) {
        return Condition.in(l, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
    private static boolean inShort(Long i) {
        return Condition.in(i, Short.MIN_VALUE, Short.MAX_VALUE);
    }
    private static boolean inByte(Long i) {
        return Condition.in(i, Byte.MIN_VALUE, Byte.MAX_VALUE);
    }

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

    private Converter(String name, Function<Object, C> convertFunc) {
        this.name = name;
        this.convertFunc = convertFunc;
    }

    private Converter(Class<?> targetClazz, Function<Object, C> convertFunc) {
        this.name = targetClazz.getCanonicalName();
        this.convertFunc = convertFunc;
    }

    public C convert(Object obj) {
        try {
            return this.convertFunc.apply(obj);
        } catch (Throwable t) {
            throw new ConvertException(StringUtils.format(INCOMPATIBLE_TYPE_MESSAGE, this.name));
        }
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
        Converter<T> system = getConverter(SYSTEM_CONVERTERS, clazz);
        if (system != null) {
            if (CUSTOM_CONVERTERS.containsKey(clazz)) {

            }
        }
        return null;
    }

    private static <T> T internalConvert(Object obj, Class<T> clazz) {
        Converter<T> converter = getConverter(SYSTEM_CONVERTERS, clazz);
        assert converter != null : "No " + TypeUtils.unbox(clazz).getSimpleName() + " converter.";
        return converter.convert(obj);
    }

    private static <T> Converter<T> getConverter(Map<?, Converter<?>> converters, Class<T> clazz) {
        @SuppressWarnings("unchecked")
        Converter<T> converter = (Converter<T>) converters.get(clazz);
        return converter;
    }

}