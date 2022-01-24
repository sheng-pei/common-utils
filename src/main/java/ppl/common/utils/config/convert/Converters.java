package ppl.common.utils.config.convert;

import ppl.common.utils.Condition;
import ppl.common.utils.logging.Logger;
import ppl.common.utils.logging.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Converters {

    private static final Logger logger = LoggerFactory.getLogger(Converters.class);

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
//    private final Converter<?> DEFAULT_CONVERTER;

    private Converters() {
        Converter<Character> charConverter = new Converter<Character>("char",
                Character.class::cast);
        SYSTEM_CONVERTERS.put(Character.class, charConverter);
        SYSTEM_CONVERTERS.put(char.class, charConverter);

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
        if (SYSTEM_CONVERTERS.containsKey(targetClazz)) {
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
        if (SYSTEM_CONVERTERS.containsKey(targetClazz)) {
            return (Converter<T>) SYSTEM_CONVERTERS.get(targetClazz);
        }
        return (Converter<T>) CUSTOM_CONVERTERS.get(targetClazz);
    }

}
