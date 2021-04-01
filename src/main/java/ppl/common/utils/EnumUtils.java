package ppl.common.utils;

import ppl.common.utils.exception.EnumEncoderNotSupportedException;
import ppl.common.utils.exception.UnknownEnumException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static ppl.common.utils.EnumEncoder.ERROR;
import static ppl.common.utils.EnumEncoder.VALID_ENUM_KEY_TYPE;

@SuppressWarnings("rawtypes")
public class EnumUtils {

    private static final WeakHashMap<Class<? extends Enum>, Object> encoderCache = new WeakHashMap<>();

    private static final WeakHashMap<Class<? extends Enum>, Map<Object, Enum<?>>> keyToEnumCache = new WeakHashMap<>();
    private static final WeakHashMap<Enum<?>, Object> enumToKeyCache = new WeakHashMap<>();

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>, K> E enumOf(Class<E> enumClass, K key) {

        Objects.requireNonNull(enumClass, "Enum class is null");
        Objects.requireNonNull(key, "Key is null");
        checkEncodeSupport(enumClass);

        E e = (E) keyToEnumCache.get(enumClass).get(key);
        if (e == null) {
            throw new UnknownEnumException(enumClass, key);
        }
        return e;

    }

    @SuppressWarnings("unchecked")
    public static <K> K encode(Enum e, Class<K> keyClazz) {
        Objects.requireNonNull(keyClazz, "keyClazz is null");
        if (e == null) {
            return null;
        }

        checkEncodeSupport(e.getClass());

        Object key = enumToKeyCache.get(e);
        if (!keyClazz.isInstance(key)) {
            throw new IllegalArgumentException(StringUtils.format("Could not encode enum({}) to {}", e.getClass().getCanonicalName(), keyClazz.getCanonicalName()));
        }

        return (K) key;
    }

    public static Object encode(Enum e) {
        if (e == null) {
            return null;
        }
        checkEncodeSupport(e.getClass());

        return enumToKeyCache.get(e);
    }

    public static Class<?> getKeyType(Class<? extends Enum> enumClass) {
        Objects.requireNonNull(enumClass, "Enum class is null");
        checkEncodeSupport(enumClass);

        Method encoder = (Method) encoderCache.get(enumClass);
        return encoder.getReturnType();
    }

    public static boolean isEncodeSupport(Class<? extends Enum> enumClass) {
        loadEnums(enumClass);

        Object encoder = encoderCache.get(enumClass);
        if (encoder instanceof ERROR) {
            return false;
        }
        return true;
    }

    public static void checkEncodeSupport(Class<? extends Enum> enumClass) {
        loadEnums(enumClass);

        Object encoder = encoderCache.get(enumClass);
        if (encoder instanceof ERROR) {
            throw new EnumEncoderNotSupportedException((ERROR) encoder, enumClass);
        }
    }

    private static void loadEnums(Class<? extends Enum> enumClass) {

        Object encoder = encoderCache.get(enumClass);
        if (encoder != null) {
            return;
        }

        encoder = loadEncodeMethod(enumClass);
        if (encoder instanceof Method) {
            Map<Object, Enum<?>> keyToEnum = new HashMap<>();
            ERROR error = applyEncoder(enumClass.getEnumConstants(), (Method) encoder, keyToEnum);
            if (error == null) {
                keyToEnumCache.put(enumClass, keyToEnum);
                enumToKeyCache.putAll(invertMap(keyToEnum));
            } else {
                encoder = error;
            }
        }
        encoderCache.put(enumClass, encoder);
    }

    private static Object loadEncodeMethod(Class<? extends Enum> enumClass) {
        List<Method> encoders = new ArrayList<>();
        Method[] methods = enumClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(EnumEncoder.class)) {
                if (method.getParameterCount() != 0) {
                    return ERROR.PARAMETER_UNMATCHED;
                }
                Class<?> returnType = method.getReturnType();
                if (!VALID_ENUM_KEY_TYPE.contains(returnType)) {
                    return ERROR.INVALID_RETURN_TYPE;
                }
                if (!encoders.isEmpty()) {
                    return ERROR.MULTIPLE_ENCODER;
                }
                encoders.add(method);
            }
        }

        if (encoders.isEmpty()) {
            return ERROR.NO_ENUM_ENCODER;
        }
        return encoders.get(0);
    }

    private static ERROR applyEncoder(Enum<?>[] enums, Method encoder, Map<Object, Enum<?>> keyToEnum) {
        for (Enum<?> e : enums) {
            try {
                Object key = encoder.invoke(e);
                if (key == null) {
                    return ERROR.NULL_KEY;
                }
                if (keyToEnum.containsKey(key)) {
                    return ERROR.DUPLICATE_ENUM_KEYS;
                }
                keyToEnum.put(key, e);
            } catch (IllegalAccessException exception) {
                return ERROR.ILLEGAL_ACCESS;
            } catch (InvocationTargetException exception) {
                return ERROR.INVOCATION_ERROR;
            }
        }
        return null;
    }

    private static <K, V> Map<V, K> invertMap(Map<K, V> input) {
        Map<V, K> res = new HashMap<>();
        for (Map.Entry<K, V> entry : input.entrySet()) {
            if (!res.containsKey(entry.getValue())) {
                res.put(entry.getValue(), entry.getKey());
            } else {
                throw new IllegalArgumentException("The input must be one-to-one map.");
            }
        }
        return res;
    }

}
