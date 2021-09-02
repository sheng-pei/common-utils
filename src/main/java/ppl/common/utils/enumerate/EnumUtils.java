package ppl.common.utils.enumerate;

import ppl.common.utils.StringUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("rawtypes")
public class EnumUtils {

    private static final ConcurrentHashMap<Class<? extends Enum>, Object> encoderCache = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<Class<? extends Enum>, Map<EnumKey, Enum<?>>> keyToEnumCache = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Enum<?>, EnumKey> enumToKeyCache = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>, K> E enumOf(Class<E> enumClass, K key) {

        Objects.requireNonNull(enumClass, "Enum class is null");
        Objects.requireNonNull(key, "Key is null");
        checkEncodeSupport(enumClass);

        E e = (E) keyToEnumCache.get(enumClass).get(EnumKey.wrap(key));
        if (e == null) {
            throw new UnknownEnumException(enumClass, key);
        }
        return e;

    }

    public static <K> K encode(Enum e, Class<K> keyClazz) {
        Objects.requireNonNull(keyClazz, "keyClazz is null");
        if (e == null) {
            return null;
        }

        checkEncodeSupport(e.getClass());

        try {
            return EnumKey.unwrap(enumToKeyCache.get(e), keyClazz);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(StringUtils.format(
                    "Could not encode enum({}) to {}",
                    e.getClass().getCanonicalName(),
                    keyClazz.getCanonicalName()), ex);
        }
    }

    public static Object encode(Enum e) {
        if (e == null) {
            return null;
        }

        checkEncodeSupport(e.getClass());
        return EnumKey.unwrap(enumToKeyCache.get(e));
    }

    public static Class<?> getKeyType(Class<? extends Enum> enumClass) {
        Objects.requireNonNull(enumClass, "Enum class is null");
        checkEncodeSupport(enumClass);

        Method encoder = (Method) encoderCache.get(enumClass);
        return encoder.getReturnType();
    }

    public static boolean isEncodeSupport(Class<? extends Enum> enumClass) {
        loadEnums(enumClass);
        return !EnumSupport.isError(encoderCache.get(enumClass));
    }

    public static void checkEncodeSupport(Class<? extends Enum> enumClass) {
        loadEnums(enumClass);

        EnumSupport.check(encoderCache.get(enumClass), enumClass);
    }

    private static void loadEnums(Class<? extends Enum> enumClass) {

        Object encoder = encoderCache.get(enumClass);
        if (encoder != null) {
            return;
        }

        encoder = EnumSupport.loadEncodeMethod(enumClass);
        if (encoder instanceof Method) {
            Map<EnumKey, Enum<?>> keyToEnum = new HashMap<>();
            Object error = EnumSupport.applyEncoder(enumClass.getEnumConstants(), (Method) encoder, keyToEnum);
            if (error == null) {
                keyToEnumCache.put(enumClass, keyToEnum);
                enumToKeyCache.putAll(invertMap(keyToEnum));
            } else {
                encoder = error;
            }
        }
        encoderCache.put(enumClass, encoder);
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
