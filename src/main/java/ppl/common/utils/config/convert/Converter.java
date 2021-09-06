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
    private static final Map<Class<?>, Converter<?>> CONVERTERS = new HashMap<>();

    static {
        Converter<Byte> byteConverter = new Converter<>(o -> {
            if (o == null) {
                return null;
            }

            if (isInteger(o)) {
                long v = ((Number) o).longValue();
                if (inByte(v)) {
                    return (byte) v;
                }
            }
            throw new ConvertException(incompatibleWith(Short.class));
        });
        CONVERTERS.put(Byte.class, byteConverter);
        CONVERTERS.put(byte.class, byteConverter);

        Converter<Short> shortConverter = new Converter<>(o -> {
            if (o == null) {
                return null;
            }

            if (isInteger(o)) {
                long v = ((Number) o).longValue();
                if (inShort(v)) {
                    return (short) v;
                }
            }
            throw new ConvertException(incompatibleWith(Short.class));
        });
        CONVERTERS.put(Short.class, shortConverter);
        CONVERTERS.put(short.class, shortConverter);

        Converter<Integer> intConverter = new Converter<>(o -> {
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
            throw new ConvertException(incompatibleWith(Integer.class));
        });
        CONVERTERS.put(Integer.class, intConverter);
        CONVERTERS.put(int.class, intConverter);

        Converter<Long> longConverter = new Converter<>(o -> {
            if (o == null) {
                return null;
            }
            if (isInteger(o)) {
                if (o instanceof Long) {
                    return (Long) o;
                }
                return ((Number) o).longValue();
            }
            throw new ConvertException(incompatibleWith(Long.class));
        });
        CONVERTERS.put(Long.class, longConverter);
        CONVERTERS.put(long.class, longConverter);

        Converter<Double> doubleConverter = new Converter<>(o -> {
            if (o == null) {
                return null;
            }
            if (o instanceof Float) {
                return ((Float) o).doubleValue();
            } else if (o instanceof Double) {
                return (Double) o;
            }
            throw new ConvertException(incompatibleWith(Double.class));
        });
        CONVERTERS.put(Double.class, doubleConverter);
        CONVERTERS.put(double.class, doubleConverter);
    }

    private final static Set<Class<?>> INTEGER_TYPE = new HashSet<Class<?>>() {
        {
            add(Byte.class);
            add(Short.class);
            add(Integer.class);
            add(Long.class);
        }
    };

    private static String incompatibleWith(Class<?> clazz) {
        return StringUtils.format(INCOMPATIBLE_TYPE_MESSAGE, BaseType.nameOf(clazz));
    }

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

    private static class CastConverter<C> extends Converter<C> {
        private CastConverter(Class<C> targetClass) {
            super(o -> {
                if (o == null) {
                    return null;
                }
                Class<?> wrapper = TypeUtils.box(targetClass);
                try {
                    @SuppressWarnings("unchecked")
                    C res = (C) wrapper.cast(o);
                    return res;
                } catch (ClassCastException e) {
                    throw new ConvertException(Converter.incompatibleWith(targetClass), e);
                }
            });
        }
    }

    private static class EnumConverter<C extends Enum<C>> extends Converter<C> {
        private EnumConverter(Class<C> targetClass) {
            super(o -> {
                if (o == null) {
                    return null;
                }

                try {
                    return EnumUtils.enumOf(targetClass, o);
                } catch (UnknownEnumException e) {
                    throw new ConvertException(Converter.incompatibleWith(targetClass), e);
                }
            });
        }
    }

    public static <C> Converter<C> getInstance(Class<C> targetClass) {

        if (targetClass == null) {
            throw new IllegalArgumentException("TargetClass must not be null.");
        }

        if (targetClass == void.class) {
            throw new IllegalArgumentException("TargetClass must not be void.");
        }

        if (Enum.class.isAssignableFrom(targetClass)) {
            @SuppressWarnings({"unchecked", "rawtypes"})
            Class<? extends Enum> enumClass = (Class<? extends Enum>) targetClass;
            try {
                EnumUtils.checkEncodeSupport(enumClass);
            } catch (EnumEncoderNotSupportedException e) {
                throw new IllegalArgumentException("TargetClass is not support enum encoder.", e);
            }
        }

        Converter<?> converter = CONVERTERS.get(targetClass);
        if (converter == null) {
            if (Enum.class.isAssignableFrom(targetClass)) {
                @SuppressWarnings({"rawtypes", "unchecked"})
                EnumConverter<?> enumConverter =  new EnumConverter(targetClass);
                converter = enumConverter;
            } else {
                converter = new CastConverter<>(targetClass);
            }
        }
        @SuppressWarnings("unchecked")
        Converter<C> res = (Converter<C>) converter;
        return res;
    }

    private Function<Object, C> convertFunc;

    private Converter(Function<Object, C> convertFunc) {
        this.convertFunc = convertFunc;
    }

    public C convert(Object obj) {
        return this.convertFunc.apply(obj);
    }

    public C convertNullIfException(Object obj) {
        try {
            return this.convertFunc.apply(obj);
        } catch (ConvertException e) {
            //Ignore, not error
        }
        return null;
    }

}