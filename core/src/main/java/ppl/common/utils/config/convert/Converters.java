package ppl.common.utils.config.convert;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppl.common.utils.Numbers;
import ppl.common.utils.cache.ConcurrentReferenceValueCache;
import ppl.common.utils.cache.ReferenceType;
import ppl.common.utils.config.nodes.scalar.ScalarNode;
import ppl.common.utils.exception.UnreachableCodeException;
import ppl.common.utils.reflect.Types;
import ppl.common.utils.string.Strings;
import ppl.common.utils.cache.Cache;
import ppl.common.utils.enumerate.EnumEncoderNotSupportedException;
import ppl.common.utils.enumerate.EnumUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class Converters {

    private static final Logger logger = LoggerFactory.getLogger((Converters.class));
    private static final Map<Class<?>, Converter<?>> SYSTEM_CONVERTERS;

    static {
        Map<Class<?>, Converter<?>> systemConverters = new HashMap<>();

        Converter<Character> charConverter = new Converter<>("char", Character.class::equals, (o, c) -> {
            if (o == null) {
                return null;
            }

            if (o instanceof String) {
                String s = (String) o;
                if (s.length() == 1) {
                    return s.charAt(0);
                }
            } else if (o instanceof Character) {
                return (Character) o;
            }
            throw new IllegalArgumentException("Not character or single char string.");
        });
        systemConverters.put(char.class, charConverter);
        systemConverters.put(Character.class, charConverter);

        Converter<Byte> byteConverter = new Converter<>("byte", Byte.class::equals, (o, c) -> {
            if (o == null) {
                return null;
            }

            if (!isInteger(o)) {
                throw new IllegalArgumentException("Couldn't be represented as whole number.");
            }

            try {
                long v = toLong(o);
                if (Numbers.inByte(v)) {
                    return (byte) v;
                }
            } catch (ArithmeticException e) {
                //ignore
            }
            throw new IllegalArgumentException("Out of byte range.");
        });
        systemConverters.put(Byte.class, byteConverter);
        systemConverters.put(byte.class, byteConverter);

        Converter<Short> shortConverter = new Converter<>("short", Short.class::equals, (o, c) -> {
            if (o == null) {
                return null;
            }

            if (!isInteger(o)) {
                throw new IllegalArgumentException("Couldn't be represented as whole number.");
            }

            try {
                long v = toLong(o);
                if (Numbers.inShort(v)) {
                    return (short) v;
                }
            } catch (ArithmeticException e) {
                //ignore
            }
            throw new IllegalArgumentException("Out of short range.");
        });
        systemConverters.put(Short.class, shortConverter);
        systemConverters.put(short.class, shortConverter);

        Converter<Integer> intConverter = new Converter<>("int", Integer.class::equals, (o, c) -> {
            if (o == null) {
                return null;
            }

            if (!isInteger(o)) {
                throw new IllegalArgumentException("Couldn't be represented as whole number.");
            }

            try {
                long v = toLong(o);
                if (Numbers.inInt(v)) {
                    return (int) v;
                }
            } catch (ArithmeticException e) {
                //ignore
            }
            throw new IllegalArgumentException("Out of int range.");
        });
        systemConverters.put(Integer.class, intConverter);
        systemConverters.put(int.class, intConverter);

        Converter<Long> longConverter = new Converter<>("long", Long.class::equals, (o, c) -> {
            if (o == null) {
                return null;
            }

            if (!isInteger(o)) {
                throw new IllegalArgumentException("Couldn't be represented as whole number.");
            }

            try {
                return toLong(o);
            } catch (ArithmeticException e) {
                throw new IllegalArgumentException("Out of long range.", e);
            }
        });
        systemConverters.put(Long.class, longConverter);
        systemConverters.put(long.class, longConverter);

        Converter<BigInteger> bigintConverter = new Converter<>("bigint", BigInteger.class::equals, (o, c) -> {
            if (o == null) {
                return null;
            }

            if (!isInteger(o)) {
                throw new IllegalArgumentException("Couldn't be represented as whole number.");
            }

            if (o instanceof BigInteger) {
                return (BigInteger) o;
            } else {
                return BigInteger.valueOf(toLong(o));
            }
        });
        systemConverters.put(BigInteger.class, bigintConverter);

        Converter<Float> floatConverter = new Converter<>("float", Float.class::equals, (o, c) -> {
            if (o == null) {
                return null;
            }

            if (isFloat(o)) {
                return toFloat(o);
            }

            if (isInteger(o)) {
                try {
                    long v = toLong(o);
                    if (Numbers.inShort(v)) {
                        return (float) v;
                    }
                } catch (ArithmeticException e) {
                    //ignore
                }
                throw new IllegalArgumentException("Out of short range, to float maybe loss of accuracy.");
            }

            throw new IllegalArgumentException("Couldn't be represented as float (java primitive).");
        });
        systemConverters.put(float.class, floatConverter);
        systemConverters.put(Float.class, floatConverter);

        Converter<Double> doubleConverter = new Converter<>("double", Double.class::equals, (o, c) -> {
            if (o == null) {
                return null;
            }

            if (isFloat(o)) {
                return toDouble(o);
            }

            if (isInteger(o)) {
                try {
                    long v = toLong(o);
                    if (Numbers.inInt(v)) {
                        return (double) v;
                    }
                } catch (ArithmeticException e) {
                    //ignore
                }
                throw new IllegalArgumentException("Out of int range, to double maybe loss of accuracy.");
            }

            throw new IllegalArgumentException("Couldn't be represented as double (java primitive).");
        });
        systemConverters.put(Double.class, doubleConverter);
        systemConverters.put(double.class, doubleConverter);

        Converter<BigDecimal> decimalConverter = new Converter<>("decimal", BigDecimal.class::equals, (o, c) -> {
            if (o == null) {
                return null;
            }

            if (isFloat(o)) {
                if (o instanceof BigDecimal) {
                    return (BigDecimal) o;
                } else {
                    return BigDecimal.valueOf(toDouble(o));
                }
            }

            if (isInteger(o)) {
                if (o instanceof BigInteger) {
                    return new BigDecimal((BigInteger) o);
                } else {
                    return new BigDecimal(toLong(o));
                }
            }

            throw new IllegalArgumentException("Couldn't be represented as real number.");
        });
        systemConverters.put(BigDecimal.class, decimalConverter);

        Converter<Boolean> boolConverter = new Converter<>("bool", Boolean.class::equals, (o, c) -> {
            if (o == null) {
                return null;
            }
            try {
                if (o instanceof JsonNode) {
                    JsonNode jNode = (JsonNode) o;
                    if (jNode.isBoolean()) {
                        return jNode.booleanValue();
                    }
                }
            } catch (LinkageError e) {
                //ignore jackson
            }
            if (o instanceof Boolean) {
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
            try {
                if (o instanceof JsonNode) {
                    JsonNode jNode = (JsonNode) o;
                    if (jNode.isTextual()) {
                        return jNode.textValue();
                    }
                }
            } catch (LinkageError e) {
                //ignore jackson
            }
            if (o instanceof String) {
                return (String) o;
            } else if (ScalarNode.isScalar(o)) {
                return o.toString();
            }
            throw new IllegalArgumentException("Couldn't be represented as string");
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
                return EnumUtils.enumOf(enumClass, o);
            } catch (EnumEncoderNotSupportedException e) {
                logger.debug(Strings.format(
                        "The enum class '{}' is not support enum encoder. " +
                                "Use named or ordinal instead.", enumClass.getName()
                ), e);
            }

            if (Types.isBaseInteger(o)) {
                long value = ((Number) o).longValue();
                if (Numbers.inInt(value)) {
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

        SYSTEM_CONVERTERS = systemConverters;
    }

    private static final Converters DEFAULT = new Converters() {
        @Override
        public <T> void addConverter(Converter<T> converter) {
            throw new UnsupportedOperationException("Failed to add custom converter.");
        }
    };

    private static boolean isFloat(Object o) {
        try {
            if (o instanceof JsonNode) {
                JsonNode jNode = (JsonNode) o;
                return jNode.isFloat() || jNode.isDouble() || jNode.isBigDecimal();
            }
        } catch (LinkageError e) {
            //ignore jackson
        }
        return Types.isFloat(o);
    }

    private static float toFloat(Object o) {
        try {
            if (o instanceof JsonNode) {
                JsonNode jNode = (JsonNode) o;
                if (jNode.isFloat()) {
                    return jNode.floatValue();
                }
            }
        } catch (LinkageError e) {
            //ignore jackson
        }
        if (o instanceof Float) {
            return (Float) o;
        }
        throw new IllegalArgumentException("Reject to be represented as float, maybe loss of accuracy.");
    }

    private static double toDouble(Object o) {
        try {
            if (o instanceof JsonNode) {
                JsonNode jNode = (JsonNode) o;
                if (jNode.isFloat() || jNode.isDouble()) {
                    return jNode.doubleValue();
                }
            }
        } catch (LinkageError e) {
            //ignore jackson
        }

        if (o instanceof Float || o instanceof Double) {
            return ((Number) o).doubleValue();
        }
        throw new IllegalArgumentException("Reject to be represented as double, maybe loss of accuracy.");
    }

    private static boolean isInteger(Object o) {
        try {
            if (o instanceof JsonNode) {
                JsonNode jNode = (JsonNode) o;
                return jNode.isInt() || jNode.isLong() || jNode.isShort() || jNode.isBigInteger();
            }
        } catch (LinkageError e) {
            //ignore jackson
        }
        return Types.isInteger(o);
    }

    private static long toLong(Object o) {
        try {
            if (o instanceof JsonNode) {
                JsonNode jNode = (JsonNode) o;
                return jNode.longValue();
            }
        } catch (LinkageError e) {
            //ignore jackson
        }
        if (o instanceof BigInteger) {
            BigInteger bi = (BigInteger) o;
            return bi.longValueExact();
        }
        if (o instanceof Number) {
            return ((Number) o).longValue();
        }
        throw new IllegalArgumentException("Reject to be represented as long, not a number.");
    }

    private static Object toEnumKey(Object o) {
        try {
            if (o instanceof JsonNode) {
                JsonNode jNode = (JsonNode) o;
                if (jNode.isTextual()) {
                    return jNode.textValue();
                } else if (jNode.isInt() || jNode.isShort() || jNode.isLong()) {
                    return jNode.longValue();
                }
            }
        } catch (LinkageError e) {
            //ignore jackson
        }
        if (o instanceof String || Types.isBaseInteger(o)) {
            return o;
        }
        throw new IllegalArgumentException("Couldn't be converted to enum.");
    }

    public static Converters def() {
        return DEFAULT;
    }

    private final List<Converter<?>> customConverters = new CopyOnWriteArrayList<>();
    private final Cache<Class<?>, Converter<?>> customConverterCache = new Cache<Class<?>, Converter<?>>() {

        @Override
        public Converter<?> get(Class<?> key, Callable<? extends Converter<?>> loader) throws ExecutionException {
            return cache.get(key, loader);
        }

        @Override
        public Converter<?> get(Class<?> key, Function<Class<?>, ? extends Converter<?>> mapperFunction) throws ExecutionException {
            return cache.get(key, mapperFunction);
        }

        @Override
        public Converter<?> getIfPresent(Class<?> key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Converter<?> putIfAbsent(Class<?> key, Converter<?> value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void invalid(Class<?> key) {
            throw new UnsupportedOperationException();
        }

        private final Cache<Class<?>, Converter<?>> cache = new ConcurrentReferenceValueCache<>(ReferenceType.SOFT);

    };

    public Converters() {
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
     *
     * @param targetClazz java reflect class represented as actual type required.
     * @param <T>         actual type required
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
            return (Converter<T>) SYSTEM_CONVERTERS.get(Enum.class);
        } else if (SYSTEM_CONVERTERS.containsKey(targetClazz)) {
            return (Converter<T>) SYSTEM_CONVERTERS.get(targetClazz);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> Converter<T> getCustomConverter(Class<T> targetClazz) {
        try {
            return (Converter<T>) this.customConverterCache.get(targetClazz,
                    () -> findCustomConverter(targetClazz));
        } catch (ExecutionException e) {
            throw new UnreachableCodeException(e);
        }
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
     *
     * @param obj object whose type is unknown.
     * @return data of char (java primitive)
     * @throws ppl.common.utils.config.ConvertException, if the specified object is not java char.
     */
    public static Character charValue(Object obj) {
        return defaultConvert(obj, Character.class);
    }

    /**
     * Use system converter to do conversion from byte short int long (java primitive) or
     * IntNode ShortNode LongNode (jackson node) to byte (java primitive).
     *
     * @param obj object whose type is unknown.
     * @return data of byte (java primitive)
     * @throws ppl.common.utils.config.ConvertException, if the specified object is neither
     * byte short int long (java primitive) nor IntNode ShortNode LongNode (jackson node);
     * or the number is out of byte range.
     */
    public static Byte byteValue(Object obj) {
        return defaultConvert(obj, Byte.class);
    }

    /**
     * Use system converter to do conversion from byte short int long (java primitive) or
     * IntNode ShortNode LongNode (jackson node) to short (java primitive).
     *
     * @param obj object whose type is unknown.
     * @return data of short (java primitive).
     * @throws ppl.common.utils.config.ConvertException, if the specified object is neither
     * byte short int long (java primitive) nor IntNode ShortNode LongNode (jackson node)
     * or the number is out of short range.
     */
    public static Short shortValue(Object obj) {
        return defaultConvert(obj, Short.class);
    }

    /**
     * Use system converter to do conversion from byte short int long (java primitive) or
     * IntNode ShortNode LongNode (jackson node) to int (java primitive).
     *
     * @param obj object whose type is unknown.
     * @return data of int (java primitive)
     * @throws ppl.common.utils.config.ConvertException, if the specified object is neither
     * byte short int long (java primitive) nor IntNode ShortNode LongNode (jackson node)
     * or the number is out of int range.
     */
    public static Integer intValue(Object obj) {
        return defaultConvert(obj, Integer.class);
    }

    /**
     * Use system converter to do conversion from byte short int long (java primitive) or
     * IntNode ShortNode LongNode (jackson node) to long (java primitive).
     *
     * @param obj object whose type is unknown.
     * @return data of long (java primitive)
     * @throws ppl.common.utils.config.ConvertException, if the specified object is neither
     * byte short int long (java primitive) nor IntNode ShortNode LongNode (jackson node).
     */
    public static Long longValue(Object obj) {
        return defaultConvert(obj, Long.class);
    }

    /**
     * Use system converter to do conversion from byte short int long (java primitive) BigInteger or
     * IntNode ShortNode LongNode BigIntegerNode (jackson node) to bigint (java primitive).
     *
     * @param obj object whose type is unknown.
     * @return data of BigInteger (java primitive)
     * @throws ppl.common.utils.config.ConvertException, if the specified object is neither
     * byte short int long (java primitive) BigInteger nor IntNode ShortNode LongNode BigIntegerNode (jackson node).
     */
    public static BigInteger bigintValue(Object obj) {
        return defaultConvert(obj, BigInteger.class);
    }

    /**
     * Use system converter to do conversion from float (java primitive) or
     * FloatNode (jackson node) to float (java primitive).
     *
     * @param obj object whose type is unknown.
     * @return data of float (java primitive)
     * @throws ppl.common.utils.config.ConvertException, if the specified object is neither
     * float (java primitive) nor FloatNode (jackson node).
     */
    public static Float floatValue(Object obj) {
        return defaultConvert(obj, Float.class);
    }

    /**
     * Use system converter to do conversion from float double (java primitive) or
     * FloatNode DoubleNode (jackson node) to double (java primitive).
     *
     * @param obj object whose type is unknown.
     * @return data of double (java primitive)
     * @throws ppl.common.utils.config.ConvertException, if the specified object is neither
     * float double (java primitive) nor DoubleNode (jackson node).
     */
    public static Double doubleValue(Object obj) {
        return defaultConvert(obj, Double.class);
    }

    /**
     * Use system converter to do conversion from float double (java primitive) or
     * FloatNode DoubleNode (jackson node) to double (java primitive). And round
     * with scale given.
     *
     * @param obj object whose type is unknown.
     * @param scale scale rounded with.
     * @return data of double (java primitive)
     * @throws ppl.common.utils.config.ConvertException, if the specified object is neither
     * float double (java primitive) nor DoubleNode (jackson node).
     */
    public static Double doubleValue(Object obj, int scale) {
        Double ret = defaultConvert(obj, Double.class);
        return BigDecimal.valueOf(ret)
                .setScale(scale, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * Use system converter to do conversion from float double (java primitive) BigDecimal or
     * FloatNode DoubleNode BigDecimalNode (jackson node) to BigDecimal.
     *
     * @param obj object whose type is unknown.
     * @return data of BigDecimal
     * @throws ppl.common.utils.config.ConvertException, if the specified object is neither
     * float double (java primitive) BigDecimal nor FloatNode DoubleNode BigDecimalNode (jackson node).
     */
    public static BigDecimal decimalValue(Object obj) {
        return defaultConvert(obj, BigDecimal.class);
    }

    /**
     * Use system converter to do conversion from boolean (java primitive) or
     * BooleanNode (jackson node) to boolean (java primitive).
     *
     * @param obj object whose type is unknown.
     * @return data of boolean (java primitive)
     * @throws ppl.common.utils.config.ConvertException, if the specified object is neither
     * boolean (java primitive) nor BooleanNode (jackson node).
     */
    public static Boolean boolValue(Object obj) {
        return defaultConvert(obj, Boolean.class);
    }

    /**
     * Use system converter to do conversion from String (java) or 'Textual Node' (jackson node)
     * to String (java).
     *
     * @param obj object whose type is unknown.
     * @return data of String (java)
     * @throws ppl.common.utils.config.ConvertException, if the specified object is neither
     * String (java) nor 'Textual Node' (jackson node).
     */
    public static String stringValue(Object obj) {
        return defaultConvert(obj, String.class);
    }

    /**
     * Use system converter to do conversion from byte short int long (java primitive) String or
     * IntNode ShortNode LongNode 'Textual Node' (jackson node) to enum (java).
     *
     * @param obj       object whose type is unknown.
     * @param enumClass java reflect class represented as actual type required.
     * @param <E>       actual type required.
     * @return data of type E
     * @throws ppl.common.utils.config.ConvertException, if the specified object is neither
     * byte short int long (java primitive) String nor IntNode ShortNode LongNode 'Textual Node' (jackson node)
     * or the specified object is not in the enumeration.
     */
    public static <E extends Enum<E>> E enumValue(Object obj, Class<E> enumClass) {
        return defaultConvert(obj, enumClass);
    }

    public static <T> T defaultConvert(Object obj, Class<T> clazz) {
        return Converters.def().internalConvert(obj, clazz);
    }

    /**
     * Find converter to do conversion from an object to the specified class and do conversion
     * from the specified object.
     *
     * @param obj   object whose type is unknown.
     * @param clazz java reflect class represented as actual type required.
     * @param <T>   actual type required.
     * @return data of type T.
     * @throws ppl.common.utils.config.ConvertException, if the specified object couldn't be converted.
     */
    public <T> T convert(Object obj, Class<T> clazz) {
        return internalConvert(obj, clazz);
    }

    private <T> T internalConvert(Object obj, Class<T> clazz) {
        Converter<T> converter = getConverter(clazz);
        assert converter != null : "No converter to " + clazz.getName();
        return converter.convert(obj, clazz);
    }

}
