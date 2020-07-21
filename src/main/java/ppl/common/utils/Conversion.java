package ppl.common.utils;

import ppl.common.utils.exception.ConversionException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class Conversion {

    private final static Map<Class<?>, Integer> wrapperToSize = new HashMap<Class<?>, Integer>() {
        {
            put(Byte.class, 1);
            put(Short.class, 2);
            put(Integer.class, 4);
            put(Long.class, 8);
            put(BigInteger.class, Integer.MAX_VALUE);
        }
    };

    private static <T> T cast(Object object, Function<Object, T> converter) {
        return Optional.ofNullable(object)
                .map(converter::apply)
                .orElse(null);
    }

    public static Integer castInteger(Object object) {
        return cast(object, nonNull -> {
            if (isCompatible(nonNull, Integer.class)) {
                if (nonNull instanceof Integer) {
                    return (Integer) nonNull;
                } else {
                    return ((Number) nonNull).intValue();
                }
            }
            throw new ConversionException("Incompatible with Integer");
        });
    }

    public static Long castLong(Object object) {
        return cast(object, nonNull -> {
            if (isCompatible(nonNull, Long.class)) {
                if (nonNull instanceof Long) {
                    return (Long) nonNull;
                } else {
                    return ((Number) nonNull).longValue();
                }
            }
            throw new ConversionException("Incompatible with Long");
        });
    }

    public static BigInteger castBigInteger(Object object) {
        return cast(object, nonNull -> {
            if (isCompatible(nonNull, BigInteger.class)) {
                if (nonNull instanceof BigInteger) {
                    return (BigInteger) nonNull;
                } else {
                    return BigInteger.valueOf(((Number) nonNull).longValue());
                }
            }
            throw new ConversionException("Incompatible with BigInteger");
        });
    }

    private static boolean isCompatible(Object source, Class<?> target) {
        Integer srcSize = wrapperToSize.get(source.getClass());
        Integer tgtSize = wrapperToSize.get(target);
        if (srcSize != null && tgtSize != null) {
            return srcSize <= tgtSize;
        }
        return false;
    }

    public static String castString(Object object) {
        return cast(object, nonNull -> {
            if (nonNull instanceof String) {
                return (String) nonNull;
            }
            throw new ConversionException("Incompatible with String");
        });
    }

    public static Boolean castBoolean(Object object) {
        return cast(object, nonNull -> {
            if (nonNull instanceof Boolean) {
                return (Boolean) nonNull;
            }
            throw new ConversionException("Incompatible with Boolean");
        });
    }

    public static BigDecimal castBigDecimal(Object object) {
        return cast(object, nonNull -> {
            if (nonNull instanceof String) {
                try {
                    return new BigDecimal((String) object);
                } catch (NumberFormatException e) {
                    //ignore
                }
            }
            throw new ConversionException("Incompatible with BigDecimal");
        });
    }

    public static List<?> castList(Object object) {
        return cast(object, nonNull -> {
            if (nonNull instanceof List) {
                return (List<?>) nonNull;
            }
            throw new ConversionException("Incompatible with List");
        });
    }

    public static Map<?, ?> castMap(Object object) {
        return cast(object, nonNull -> {
            if (nonNull instanceof Map) {
                return (Map<?, ?>) nonNull;
            }
            throw new ConversionException("Incompatible with List");
        });
    }

    public static Double castDouble(Object object) {
        return cast(object, nonNull -> {
            if (nonNull instanceof Float) {
                return ((Float) nonNull).doubleValue();
            } else if (nonNull instanceof Double) {
                return (Double) nonNull;
            }
            throw new ConversionException("Incompatible with Double");
        });
    }

}
