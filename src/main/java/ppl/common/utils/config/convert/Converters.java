package ppl.common.utils.config.convert;

import ppl.common.utils.Condition;
import ppl.common.utils.cache.ConcurrentCache;
import ppl.common.utils.config.convert.cache.Cache;
import ppl.common.utils.enumerate.EnumEncoderNotSupportedException;
import ppl.common.utils.enumerate.EnumUtils;
import ppl.common.utils.logging.Logger;
import ppl.common.utils.logging.LoggerFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Converters {

    private static final Logger logger = LoggerFactory.getLogger(Converters.class);

    private static final Converters INSTANCE = new Converters();

    private static final Set<Class<?>> INTEGER_TYPE = new HashSet<Class<?>>() {
        {
            add(Byte.class);
            add(Short.class);
            add(Integer.class);
            add(Long.class);
        }
    };

    private static boolean isInteger(Class<?> clazz) {
        return INTEGER_TYPE.contains(clazz);
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

    public static Converters getInstance() {
        return INSTANCE;
    }

    private final Map<Class<?>, Converter<?>> systemConverters;
    private final List<Converter<?>> customConverters = new CopyOnWriteArrayList<>();
    private final Cache<Class<?>, Converter<?>> customConverterCache = new Cache<Class<?>, Converter<?>>() {

        private final ConcurrentCache<Class<?>, Converter<?>> cache = new ConcurrentCache<>(100);

        @Override
        public Converter<?> get(Class<?> aClass) {
            return cache.get(aClass);
        }

        @Override
        public void put(Class<?> aClass, Converter<?> converter) {
            cache.put(aClass, converter);
        }
    };

    private Converters() {

        Map<Class<?>, Converter<?>> systemConverters = new HashMap<>();

        Converter<Byte> byteConverter = new Converter<>("byte", Byte.class::equals, (o, c) -> {
            if (o == null) {
                return null;
            }

            if (isInteger(o.getClass())) {
                long v = ((Number) o).longValue();
                if (inByte(v)) {
                    return (byte) v;
                }
            }
            throw new IllegalArgumentException();
        });
        systemConverters.put(Byte.class, byteConverter);
        systemConverters.put(byte.class, byteConverter);

        Converter<Short> shortConverter = new Converter<>("short", Short.class::equals, (o, c) -> {
            if (o == null) {
                return null;
            }

            if (isInteger(o.getClass())) {
                long v = ((Number) o).longValue();
                if (inShort(v)) {
                    return (short) v;
                }
            }
            throw new IllegalArgumentException();
        });
        systemConverters.put(Short.class, shortConverter);
        systemConverters.put(short.class, shortConverter);

        Converter<Integer> intConverter = new Converter<>("int", Integer.class::equals, (o, c) -> {
            if (o == null) {
                return null;
            }
            if (isInteger(o.getClass())) {
                if (o instanceof Integer) {
                    return (Integer) o;
                } else if (!(o instanceof Long) || inInt((Long) o)) {
                    return ((Number) o).intValue();
                }
            }
            throw new IllegalArgumentException();
        });
        systemConverters.put(Integer.class, intConverter);
        systemConverters.put(int.class, intConverter);

        Converter<Long> longConverter = new Converter<>("long", Long.class::equals, (o, c) -> {
            if (o == null) {
                return null;
            }
            if (isInteger(o.getClass())) {
                if (o instanceof Long) {
                    return (Long) o;
                }
                return ((Number) o).longValue();
            }
            throw new IllegalArgumentException();
        });
        systemConverters.put(Long.class, longConverter);
        systemConverters.put(long.class, longConverter);

        Converter<Double> doubleConverter = new Converter<>("double", Double.class::equals, (o, c) -> {
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
        systemConverters.put(Double.class, doubleConverter);
        systemConverters.put(double.class, doubleConverter);

        Converter<String> stringConverter = new Converter<>("string", String.class::equals, (o, c) -> (String) o);
        systemConverters.put(String.class, stringConverter);

        Converter<Enum<?>> enumConverter = new Converter<>("enum", Enum.class::isAssignableFrom, (o, c) -> {

            if (o == null) {
                return null;
            }

            @SuppressWarnings("rawtypes")
            Class<? extends Enum> enumClass = c;
            try {
                EnumUtils.checkEncodeSupport(enumClass);
                @SuppressWarnings("unchecked")
                Enum<?> res = EnumUtils.enumOf(enumClass, o);
                return res;
            } catch (EnumEncoderNotSupportedException e) {
                logger.debug("The enum class {} is not support enum encoder. Use named or ordinal instead.", e);
            }

            if (isInteger(o.getClass())) {
                long value = ((Number) o).longValue();
                if (inInt(value)) {
                    return c.getEnumConstants()[(int) value];
                }
            } else if (o instanceof String) {
                @SuppressWarnings("unchecked")
                Enum<?> res = Enum.valueOf(enumClass, (String) o);
                return res;
            }
            throw new IllegalArgumentException();
        });
        systemConverters.put(Enum.class, enumConverter);

        this.systemConverters = systemConverters;
    }

    public <T> void addConverter(Converter<T> converter) {
        customConverters.add(converter);
    }

    private <T> Converter<T> getConverter(Class<T> targetClazz) {
        Converter<T> converter = getSystemConverter(targetClazz);
        if (converter == null) {
            converter = getCustomConverter(targetClazz);
        }
        if (converter == null) {
            converter = Converter.castConverter();
        }
        return converter;
    }

    @SuppressWarnings("unchecked")
    private <T> Converter<T> getSystemConverter(Class<T> targetClazz) {
        if (targetClazz.isEnum()) {
            return (Converter<T>) systemConverters.get(Enum.class);
        } else if (systemConverters.containsKey(targetClazz)) {
            return (Converter<T>) systemConverters.get(targetClazz);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> Converter<T> getCustomConverter(Class<T> targetClazz) {
        Converter<T> converter = (Converter<T>) this.customConverterCache.get(targetClazz);
        if (converter != null) {
            return converter;
        }

        converter = findCustomConverter(targetClazz);
        if (converter != null) {
            this.customConverterCache.put(targetClazz, converter);
            return converter;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> Converter<T> findCustomConverter(Class<T> targetClazz) {
        for (Converter<?> c : customConverters) {
            if (c.accept(targetClazz)) {
                return (Converter<T>) c;
            }
        }
        return null;
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
        return internalConvert(obj, clazz);
    }

    private static <T> T internalConvert(Object obj, Class<T> clazz) {
        Converter<T> converter = Converters.getInstance().getConverter(clazz);
        assert converter != null : "No converter to " + clazz.getName();
        return converter.convert(obj, clazz);
    }

}
