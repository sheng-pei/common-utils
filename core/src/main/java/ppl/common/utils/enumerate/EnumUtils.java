package ppl.common.utils.enumerate;

import ppl.common.utils.string.Strings;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@SuppressWarnings("rawtypes")
public class EnumUtils {

    private static final ConcurrentHashMap<Class<? extends Enum>, Object> encoderCache = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<Class<? extends Enum>, Map<EnumKey, Enum<?>>> keyToEnumCache = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Enum<?>, EnumKey> enumToKeyCache = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <E extends Enum, K> E enumOf(Class<E> enumClass, K key) {

        Objects.requireNonNull(enumClass, "Enum class is null");
        Objects.requireNonNull(key, "Key is null");
        checkEncodeSupport(enumClass);

        E e = (E) keyToEnumCache.get(enumClass).get(EnumKey.wrap(key));
        if (e == null) {
            throw new UnknownEnumException(enumClass, key);
        }
        return e;

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
        getEnums(enumClass);
        return !EnumSupport.isError(encoderCache.get(enumClass));
    }

    public static void checkEncodeSupport(Class<? extends Enum> enumClass) {
        getEnums(enumClass);

        EnumSupport.check(encoderCache.get(enumClass), enumClass);
    }

    private static void getEnums(Class<? extends Enum> enumClass) {
        //fix bug: ConcurrentHashMap.computeIfAbsent(k,f) locks bin when k present
        //https://bugs.openjdk.org/browse/JDK-8161372
        if (null == encoderCache.get(enumClass)) {
            encoderCache.computeIfAbsent(enumClass, ec -> loadEnums(ec, es -> {
                keyToEnumCache.put(enumClass, es);
                enumToKeyCache.putAll(invertMap(es));
            }));
        }
    }

    private static Object loadEnums(Class<? extends Enum> enumClass, Consumer<Map<EnumKey, Enum<?>>> enumsConsumer) {
        Object encoder = EnumSupport.loadEncodeMethod(enumClass);
        if (encoder instanceof Method) {
            Map<EnumKey, Enum<?>> enums = new HashMap<>();
            Object error = EnumSupport.applyEncoder(enumClass.getEnumConstants(), (Method) encoder, enums);
            if (error == null) {
                enumsConsumer.accept(enums);
            } else {
                encoder = error;
            }
        }
        return encoder;
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
