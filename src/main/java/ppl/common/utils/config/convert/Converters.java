package ppl.common.utils.config.convert;

import com.fasterxml.jackson.databind.JsonNode;
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

    private static long primitiveIntegerToLong(Object o) {
        if (o instanceof JsonNode) {
            JsonNode jNode = (JsonNode) o;
            if (jNode.isInt() || jNode.isLong() || jNode.isShort()) {
                return jNode.longValue();
            }
        } else if (isPrimitiveInteger(o.getClass())) {
            return ((Number) o).longValue();
        }
        throw new IllegalArgumentException("Couldn't be represented as byte short int long (java primitive).");
    }
    private static Object toEnumKey(Object o) {
        if (o instanceof JsonNode) {
            JsonNode jNode = (JsonNode) o;
            if (jNode.isTextual()) {
                return jNode.textValue();
            } else if (jNode.isInt() || jNode.isShort() || jNode.isLong()) {
                return jNode.longValue();
            }
        } else if (o instanceof String || isPrimitiveInteger(o.getClass())) {
            return o;
        }
        throw new IllegalArgumentException("Couldn't be converted to enum.");
    }
    private static boolean isPrimitiveInteger(Class<?> clazz) {
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

        systemConverters.put(char.class, Converter.castConverter());
        systemConverters.put(Character.class, Converter.castConverter());

        Converter<Byte> byteConverter = new Converter<>("byte", Byte.class::equals, (o, c) -> {
            if (o == null) {
                return null;
            }

            long v = primitiveIntegerToLong(o);
            if (inByte(v)) {
                return (byte) v;
            }
            throw new IllegalArgumentException("Out of byte range.");
        });
        systemConverters.put(Byte.class, byteConverter);
        systemConverters.put(byte.class, byteConverter);

        Converter<Short> shortConverter = new Converter<>("short", Short.class::equals, (o, c) -> {
            if (o == null) {
                return null;
            }

            long v = primitiveIntegerToLong(o);
            if (inShort(v)) {
                return (short) v;
            }
            throw new IllegalArgumentException("Out of short range.");
        });
        systemConverters.put(Short.class, shortConverter);
        systemConverters.put(short.class, shortConverter);

        Converter<Integer> intConverter = new Converter<>("int", Integer.class::equals, (o, c) -> {
            if (o == null) {
                return null;
            }

            long v = primitiveIntegerToLong(o);
            if (inInt(v)) {
                return (int) v;
            }
            throw new IllegalArgumentException("Out of int range.");
        });
        systemConverters.put(Integer.class, intConverter);
        systemConverters.put(int.class, intConverter);

        Converter<Long> longConverter = new Converter<>("long", Long.class::equals, (o, c) -> {
            if (o == null) {
                return null;
            }
            return primitiveIntegerToLong(o);
        });
        systemConverters.put(Long.class, longConverter);
        systemConverters.put(long.class, longConverter);

        Converter<Float> floatConverter = new Converter<>("float", Float.class::equals, (o, c) -> {
            if (o == null) {
                return null;
            }
            if (o instanceof JsonNode) {
                JsonNode jNode = (JsonNode) o;
                if (jNode.isFloat()) {
                    return jNode.floatValue();
                }
            } else if (o instanceof Float) {
                return (Float) o;
            }
            throw new IllegalArgumentException("Couldn't be represented as float (java primitive).");
        });
        systemConverters.put(float.class, floatConverter);
        systemConverters.put(Float.class, floatConverter);

        Converter<Double> doubleConverter = new Converter<>("double", Double.class::equals, (o, c) -> {
            if (o == null) {
                return null;
            }
            if (o instanceof JsonNode) {
                JsonNode jNode = (JsonNode) o;
                if (jNode.isFloat() || jNode.isDouble()) {
                    return jNode.doubleValue();
                }
            } else if (o instanceof Float) {
                return ((Float) o).doubleValue();
            } else if (o instanceof Double) {
                return (Double) o;
            }
            throw new IllegalArgumentException("Couldn't be represented as float double (java primitive).");
        });
        systemConverters.put(Double.class, doubleConverter);
        systemConverters.put(double.class, doubleConverter);

        Converter<Boolean> boolConverter = new Converter<>("bool", Boolean.class::equals, (o, c) -> {
            if (o == null) {
                return null;
            }
            if (o instanceof JsonNode) {
                JsonNode jNode = (JsonNode) o;
                if (jNode.isBoolean()) {
                    return jNode.booleanValue();
                }
            } else if (o instanceof Boolean) {
                return (Boolean) o;
            }
            throw new IllegalArgumentException("Couldn't be represented as boolean (java primitive).");
        });
        systemConverters.put(Boolean.class, boolConverter);
        systemConverters.put(boolean.class, boolConverter);

        Converter<String> stringConverter = new Converter<>("string", String.class::equals, (o, c) -> {
            if (o == null) {
                return null;
            }
            if (o instanceof JsonNode) {
                JsonNode jNode = (JsonNode) o;
                if (jNode.isTextual()) {
                    return jNode.textValue();
                }
            } else if (o instanceof String) {
                return (String) o;
            }
            throw new IllegalArgumentException("Couldn't be represented as string.");
        });
        systemConverters.put(String.class, stringConverter);

        Converter<Enum<?>> enumConverter = new Converter<>("enum", Enum.class::isAssignableFrom, (o, c) -> {

            if (o == null) {
                return null;
            }

            o = toEnumKey(o);

            @SuppressWarnings("rawtypes")
            Class<? extends Enum> enumClass = c;
            try {
                @SuppressWarnings("unchecked")
                Enum<?> res = EnumUtils.enumOf(enumClass, o);
                return res;
            } catch (EnumEncoderNotSupportedException e) {
                logger.debug(String.format(
                        "The enum class %s is not support enum encoder. " +
                        "Use named or ordinal instead.", enumClass.getName()
                ), e);
            }

            if (isPrimitiveInteger(o.getClass())) {
                long value = ((Number) o).longValue();
                if (inInt(value)) {
                    return c.getEnumConstants()[(int) value];
                }
            } else if (o instanceof String) {
                @SuppressWarnings("unchecked")
                Enum<?> res = Enum.valueOf(enumClass, (String) o);
                return res;
            }
            throw new IllegalArgumentException("Unreachable code.");
        });
        systemConverters.put(Enum.class, enumConverter);

        this.systemConverters = systemConverters;
    }

    public <T> void addConverter(Converter<T> converter) {
        customConverters.add(converter);
    }

    /**
     * method for getting Converter used to do conversion from an object to the specified target.
     * Find in the following order:
     * 1. system converter
     * 2. custom converter
     * 3. default cast converter
     * @param targetClazz java reflect class represented as actual type required.
     * @param <T> actual type required
     * @return converter used to do conversion from an object to the specified target.
     */
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

    /**
     * Use system converter to do conversion from char (java primitive) to char (java primitive).
     * @param obj object whose type is unknown.
     * @throws ppl.common.utils.config.ConvertException, if the specified object is not java char.
     * @return data of char (java primitive)
     */
    public static Character charValue(Object obj) {
        return internalConvert(obj, Character.class);
    }

    /**
     * Use system converter to do conversion from byte short int long (java primitive) or
     * IntNode ShortNode LongNode (jackson node) to byte (java primitive).
     * @param obj object whose type is unknown.
     * @throws ppl.common.utils.config.ConvertException, if the specified object is neither
     * byte short int long (java primitive) nor IntNode ShortNode LongNode (jackson node);
     * if the number is out of byte range.
     * @return data of byte (java primitive)
     */
    public static Byte byteValue(Object obj) {
        return internalConvert(obj, Byte.class);
    }

    /**
     * Use system converter to do conversion from byte short int long (java primitive) or
     * IntNode ShortNode LongNode (jackson node) to short (java primitive).
     * @param obj object whose type is unknown.
     * @throws ppl.common.utils.config.ConvertException, if the specified object is neither
     * byte short int long (java primitive) nor IntNode ShortNode LongNode (jackson node);
     * if the number is out of short range.
     * @return data of short (java primitive).
     */
    public static Short shortValue(Object obj) {
        return internalConvert(obj, Short.class);
    }

    /**
     * Use system converter to do conversion from byte short int long (java primitive) or
     * IntNode ShortNode LongNode (jackson node) to int (java primitive).
     * @param obj object whose type is unknown.
     * @throws ppl.common.utils.config.ConvertException, if the specified object is neither
     * byte short int long (java primitive) nor IntNode ShortNode LongNode (jackson node);
     * if the number is out of int range.
     * @return data of int (java primitive)
     */
    public static Integer intValue(Object obj) {
        return internalConvert(obj, Integer.class);
    }

    /**
     * Use system converter to do conversion from byte short int long (java primitive) or
     * IntNode ShortNode LongNode (jackson node) to long (java primitive).
     * @param obj object whose type is unknown.
     * @throws ppl.common.utils.config.ConvertException, if the specified object is neither
     * byte short int long (java primitive) nor IntNode ShortNode LongNode (jackson node).
     * @return data of long (java primitive)
     */
    public static Long longValue(Object obj) {
        return internalConvert(obj, Long.class);
    }

    /**
     * Use system converter to do conversion from float (java primitive) or
     * FloatNode (jackson node) to float (java primitive).
     * @param obj object whose type is unknown.
     * @throws ppl.common.utils.config.ConvertException, if the specified object is neither
     * float (java primitive) nor FloatNode (jackson node).
     * @return data of float (java primitive)
     */
    public static Float floatValue(Object obj) {
        return internalConvert(obj, Float.class);
    }

    /**
     * Use system converter to do conversion from float double (java primitive) or
     * FloatNode DoubleNode (jackson node) to double (java primitive).
     * @param obj object whose type is unknown.
     * @throws ppl.common.utils.config.ConvertException, if the specified object is neither
     * float double (java primitive) nor DoubleNode (jackson node).
     * @return data of double (java primitive)
     */
    public static Double doubleValue(Object obj) {
        return internalConvert(obj, Double.class);
    }

    /**
     * Use system converter to do conversion from boolean (java primitive) or
     * BooleanNode (jackson node) to boolean (java primitive).
     * @param obj object whose type is unknown.
     * @throws ppl.common.utils.config.ConvertException, if the specified object is neither
     * boolean (java primitive) nor BooleanNode (jackson node).
     * @return data of boolean (java primitive)
     */
    public static Boolean boolValue(Object obj) {
        return internalConvert(obj, Boolean.class);
    }

    /**
     * Use system converter to do conversion from String (java) or 'Textual Node' (jackson node)
     * to String (java).
     * @param obj object whose type is unknown.
     * @throws ppl.common.utils.config.ConvertException, if the specified object is neither
     * String (java) nor 'Textual Node' (jackson node).
     * @return data of String (java)
     */
    public static String stringValue(Object obj) {
        return internalConvert(obj, String.class);
    }

    /**
     * Use system converter to do conversion from byte short int long (java primitive) String (java) or
     * IntNode ShortNode LongNode 'Textual Node' (jackson node) to enum (java).
     * @param obj object whose type is unknown.
     * @param enumClass java reflect class represented as actual type required.
     * @param <E> actual type required.
     * @throws ppl.common.utils.config.ConvertException, if the specified object is neither
     * byte short int long (java primitive) nor IntNode ShortNode LongNode (jackson node);
     * if the specified object is not in the enumeration.
     * @return data of type E
     */
    public static <E extends Enum<E>> E enumValue(Object obj, Class<E> enumClass) {
        return internalConvert(obj, enumClass);
    }

    /**
     * Find converter to do conversion from an object to the specified class and do conversion
     * from the specified object.
     * @param obj object whose type is unknown.
     * @param clazz java reflect class represented as actual type required.
     * @param <T> actual type required.
     * @throws ppl.common.utils.config.ConvertException, if the specified object couldn't be converted.
     * @return data of type T.
     */
    public static <T> T convert(Object obj, Class<T> clazz) {
        return internalConvert(obj, clazz);
    }

    private static <T> T internalConvert(Object obj, Class<T> clazz) {
        Converter<T> converter = Converters.getInstance().getConverter(clazz);
        assert converter != null : "No converter to " + clazz.getName();
        return converter.convert(obj, clazz);
    }

}
