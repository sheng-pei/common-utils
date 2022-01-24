package ppl.common.utils.config.convert;

import ppl.common.utils.Condition;
import ppl.common.utils.StringUtils;
import ppl.common.utils.enumerate.EnumEncoderNotSupportedException;
import ppl.common.utils.enumerate.EnumUtils;
import ppl.common.utils.logging.Logger;
import ppl.common.utils.logging.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Converters {

    private static final Logger logger = LoggerFactory.getLogger(Converters.class);

    private static final String INCOMPATIBLE_TYPE_MESSAGE = "Incompatible with {}";

    private static final Converters INSTANCE = new Converters();

    public static Converters getInstance() {
        return INSTANCE;
    }

    private final Set<Class<?>> INTEGER_TYPE = new HashSet<Class<?>>() {
        {
            add(Byte.class);
            add(Short.class);
            add(Integer.class);
            add(Long.class);
        }
    };
    private final Map<Class<?>, Converter<?>> SYSTEM_CONVERTERS = new HashMap<>();
    private final Map<Class<?>, Converter<?>> CUSTOM_CONVERTERS = new HashMap<>();

    private Converters() {
        Converter<Character> charConverter = new Converter<>("char", (o, c) -> {
            return Character.class.cast(o);
        });
        SYSTEM_CONVERTERS.put(Character.class, charConverter);
        SYSTEM_CONVERTERS.put(char.class, charConverter);

        Converter<Byte> byteConverter = new Converter<>("byte", (o, c) -> {
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

        Converter<Short> shortConverter = new Converter<>("short", (o, c) -> {
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

        Converter<Integer> intConverter = new Converter<>("int", (o, c) -> {
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

        Converter<Long> longConverter = new Converter<>("long", (o, c) -> {
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

        Converter<Double> doubleConverter = new Converter<>("double", (o, c) -> {
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

        Converter<String> stringConverter = new Converter<>("string", (o, c) -> {
            return String.class.cast(o);
        });
        SYSTEM_CONVERTERS.put(String.class, stringConverter);

        Converter<Enum<?>> enumConverter = new Converter<Enum<?>>("enum", (o, c) -> {

            if (o == null) {
                return null;
            }

            @SuppressWarnings({"unchecked", "rawtypes"})
            Class<? extends Enum> enumClass = (Class) c;
            try {
                EnumUtils.checkEncodeSupport(enumClass);
                return EnumUtils.enumOf(enumClass, o);
            } catch (EnumEncoderNotSupportedException e) {
                logger.debug("The enum class {} is not support enum encoder. Use named or ordinal instead.", e);
            }

            if (isInteger(o)) {
                long value = ((Number) o).longValue();
                if (inInt(value)) {
                    return (Enum<?>) c.getEnumConstants()[(int) value];
                }
            } else if (o instanceof String) {
                return Enum.valueOf(enumClass, (String) o);
            }
            throw new IllegalArgumentException();
        });
        SYSTEM_CONVERTERS.put(Enum.class, enumConverter);
    }

    private boolean isInteger(Object source) {
        return INTEGER_TYPE.contains(source.getClass());
    }
    private boolean inInt(Long l) {
        return Condition.in(l, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
    private boolean inShort(Long i) {
        return Condition.in(i, Short.MIN_VALUE, Short.MAX_VALUE);
    }
    private boolean inByte(Long i) {
        return Condition.in(i, Byte.MIN_VALUE, Byte.MAX_VALUE);
    }

    public <T> void pushCustom(Class<T> targetClazz, Converter<T> converter) {
        if (targetClazz.equals(Enum.class)) {
            throw new IllegalArgumentException("java.lang.Enum is not supported.");
        }

        if (SYSTEM_CONVERTERS.containsKey(targetClazz) || targetClazz.isEnum()) {
            Converter<?> system = SYSTEM_CONVERTERS.get(targetClazz);
            logger.warn("Ignore this custom converter for {}, use system converter instead.", system.name());
            return;
        }

        if (CUSTOM_CONVERTERS.containsKey(targetClazz)) {
            logger.warn("Remove old custom converter for {}.", targetClazz.getName());
        }
        CUSTOM_CONVERTERS.put(targetClazz, converter);
    }

    public <T> Converter<T> getConverter(Class<T> targetClazz) {
        if (targetClazz.isEnum()) {
            return (Converter<T>) SYSTEM_CONVERTERS.get(Enum.class);
        } else if (SYSTEM_CONVERTERS.containsKey(targetClazz)) {
            return (Converter<T>) SYSTEM_CONVERTERS.get(targetClazz);
        }
        return (Converter<T>) CUSTOM_CONVERTERS.get(targetClazz);
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

    public static <E extends Enum<E>> E enumValue(Object obj, Class<E> enumClass) {
        return internalConvert(obj, enumClass);
    }

    public static <T> T convert(Object obj, Class<T> clazz) {
        Converter<T> converter = Converters.getInstance().getConverter(clazz);
        if (converter == null) {
            try {
                return clazz.cast(obj);
            } catch (ClassCastException e) {
                throw new ConvertException(StringUtils.format(INCOMPATIBLE_TYPE_MESSAGE, clazz.getName()), e);
            }
        }
        return converter.convert(obj, clazz);
    }

    private static <T> T internalConvert(Object obj, Class<T> clazz) {
        Converter<T> converter = Converters.getInstance().getConverter(clazz);
        assert converter != null : "No " + converter.name() + " converter.";
        return converter.convert(obj);
    }

}
