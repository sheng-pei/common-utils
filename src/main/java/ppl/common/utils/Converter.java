package ppl.common.utils;

import ppl.common.utils.exception.ConvertException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class Converter<C> {

    private static final Map<Class<?>, Converter<?>> CONVERTERS = new HashMap<>();

    static {
        CONVERTERS.put(Integer.class, new Converter<>(o -> {
            if (o == null) {
                return null;
            }
            if (isCompatible(o, Integer.class)) {
                if (o instanceof Integer) {
                    return (Integer) o;
                }
                return ((Number) o).intValue();
            }
            throw new ConvertException("Incompatible with int");
        }));
        CONVERTERS.put(Long.class, new Converter<>(o -> {
            if (o == null) {
                return null;
            }
            if (isCompatible(o, Long.class)) {
                if (o instanceof Long) {
                    return (Long) o;
                }
                return ((Number) o).longValue();
            }
            throw new ConvertException("Incompatible with long");
        }));
        CONVERTERS.put(Double.class, new Converter<>(o -> {
            if (o == null) {
               return null;
            }
            if (o instanceof Float) {
                return ((Float) o).doubleValue();
            } else if (o instanceof Double) {
                return (Double) o;
            }
            throw new ConvertException("Incompatible with double");
        }));
    }

    private final static Map<Class<?>, Integer> wrapperToSize = new HashMap<Class<?>, Integer>() {
        {
            put(Byte.class, 1);
            put(Short.class, 2);
            put(Integer.class, 4);
            put(Long.class, 8);
        }
    };

    private static boolean isCompatible(Object source, Class<?> target) {
        Integer srcSize = wrapperToSize.get(source.getClass());
        Integer tgtSize = wrapperToSize.get(target);
        if (srcSize != null && tgtSize != null) {
            return srcSize <= tgtSize;
        }
        return false;
    }

    private static class CastConverter<C> extends Converter<C> {
        private CastConverter(Class<C> targetClass) {
            super(o -> {
                if (o == null) {
                    return null;
                }
                try {
                    return targetClass.cast(o);
                } catch (ClassCastException e) {
                    String name;
                    if (targetClass.equals(Boolean.class)) {
                        name = "bool";
                    } else if (targetClass.equals(String.class)) {
                        name = "string";
                    } else if (targetClass.equals(List.class)) {
                        name = "list";
                    } else if (targetClass.equals(Map.class)) {
                        name = "map";
                    } else {
                        name = targetClass.getCanonicalName() == null ? "unknown class" : targetClass.getCanonicalName();
                    }
                    throw new ConvertException(StringUtils.format("Incompatible with class: {}", name), e);
                }
            });
        }
    }

    public static <C> Converter<C> getInstance(Class<C> targetClass) {
        Converter<?> converter = CONVERTERS.get(targetClass);
        if (converter == null) {
            converter = new CastConverter<>(targetClass);
        }
        @SuppressWarnings("unchecked")
        Converter<C> res = (Converter<C>) converter;
        return res;
    }

    private Function<Object, C> convertFunc;

    private Converter(Function<Object, C> convertFunc) {
        Objects.requireNonNull(convertFunc, "Converter function is required");
        this.convertFunc = convertFunc;
    }

    public C convert(Object obj) {
        return this.convertFunc.apply(obj);
    }

}