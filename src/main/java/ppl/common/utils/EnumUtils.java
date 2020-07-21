package ppl.common.utils;

import ppl.common.utils.exception.InvalidEncoderImplementException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.*;

@SuppressWarnings("rawtypes")
public class EnumUtils {

    private static final Set<Class<?>> VALID_ENUM_KEY_TYPE;
    private static final String VALID_ENUM_KEY_TYPE_DESC;
    static {
        Set<Class<?>> tmpValidType = new LinkedHashSet<>();
        tmpValidType.add(byte.class);
        tmpValidType.add(short.class);
        tmpValidType.add(int.class);
        tmpValidType.add(long.class);

        tmpValidType.add(char.class);

        tmpValidType.add(Byte.class);
        tmpValidType.add(Short.class);
        tmpValidType.add(Integer.class);
        tmpValidType.add(Long.class);
        tmpValidType.add(BigInteger.class);

        tmpValidType.add(Character.class);
        tmpValidType.add(String.class);

        StringBuilder descBuilder = new StringBuilder("[");
        for (Class<?> type : tmpValidType) {
            descBuilder.append(type.getSimpleName()).append(',');
        }
        descBuilder.setLength(descBuilder.length() - 1);
        descBuilder.append(']');

        VALID_ENUM_KEY_TYPE = Collections.unmodifiableSet(tmpValidType);
        VALID_ENUM_KEY_TYPE_DESC = descBuilder.toString();
    }

    public enum ERROR {

        PARAMETER_UNMATCHED("The encoder required no parameters if the enum({}) support encoder protocol"),
        INVALID_RETURN_TYPE("The return type of the encoder must be in " + VALID_ENUM_KEY_TYPE_DESC + " if the enum({}) support encoder protocol"),
        MULTIPLE_ENCODER("Only one encoder is admit if the enum({}) support encoder protocol"),
        ILLEGAL_ACCESS("The encoder of the enum({}) is not accessible"),
        INVOCATION_ERROR("Failed to apply the encoder to some element of enum({})"),
        MULTIPLE_ELEMENT("There are same keys in the enum({})");

        ERROR(String cause) {
            this.cause = cause;
        }

        private final String cause;

        public String causeOf(Class<? extends Enum> enumClass) {
            return StringUtils.format(this.cause, enumClass.getCanonicalName());
        }

    }

    private static final WeakHashMap<Class<? extends Enum>, Map<Object, Enum<?>>> keyToEnumCache = new WeakHashMap<>();
    private static final WeakHashMap<Class<? extends Enum>, Object> encoderCache = new WeakHashMap<>();
    private static final WeakHashMap<Enum<?>, Object> enumToKeyCache = new WeakHashMap<>();

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>, K> E enumOf(Class<E> enumClass, K key) {

        Objects.requireNonNull(enumClass, "Enum class is null");
        Objects.requireNonNull(key, "Key is null");

        if (!supportEncoderProtocol(enumClass)) {
            throw new IllegalArgumentException(StringUtils.format("Encoder protocol is not supported by enum({})", enumClass.getCanonicalName()));
        }

        return (E) keyToEnumCache.get(enumClass).get(key);

    }

    @SuppressWarnings("unchecked")
    public static <K> K encodeTo(Enum e, Class<K> keyClazz) {
        Objects.requireNonNull(e, "Enum is null");
        Objects.requireNonNull(e, "keyClazz is null");

        if (!supportEncoderProtocol(e.getClass())) {
            throw new IllegalArgumentException(StringUtils.format("Encoder protocol is not supported by enum({})", e.getClass().getCanonicalName()));
        }

        Object key = enumToKeyCache.get(e);
        if (!keyClazz.isInstance(key)) {
            throw new IllegalArgumentException(StringUtils.format("Could not encode enum({}) to {}", e.getClass().getCanonicalName(), keyClazz.getCanonicalName()));
        }

        return (K) key;
    }

    public static Object encode(Enum e) {
        Objects.requireNonNull(e, "Enum is null");

        if (!supportEncoderProtocol(e.getClass())) {
            throw new IllegalArgumentException(StringUtils.format("Encoder protocol is not supported by enum({})", e.getClass().getCanonicalName()));
        }
        return enumToKeyCache.get(e);
    }

    public static Class<?> getKeyType(Class<? extends Enum> enumClass) {
        Objects.requireNonNull(enumClass, "Enum class is null");

        if (!supportEncoderProtocol(enumClass)) {
            throw new IllegalArgumentException(StringUtils.format("Encoder protocol is not supported by enum({})", enumClass.getCanonicalName()));
        }

        List encoders = (List) encoderCache.get(enumClass);
        Method encoder = (Method) encoders.get(0);
        return encoder.getReturnType();
    }

    public static boolean supportEncoderProtocol(Class<? extends Enum> enumClass) {
        return getEncodeMethod(enumClass) != null;
    }

    @SuppressWarnings("unchecked")
    private static Method getEncodeMethod(Class<? extends Enum> enumClass) {
        Objects.requireNonNull(enumClass, "Enum class is null");

        if (!encoderCache.containsKey(enumClass)) {
            loadEncodeMethod(enumClass);
            loadEnums(enumClass);
        }

        Object encoder = encoderCache.get(enumClass);
        if (encoder instanceof ERROR) {
            throw new InvalidEncoderImplementException((ERROR) encoder, enumClass);
        }
        return ((List<Method>) encoder).get(0);
    }

    private static void loadEncodeMethod(Class<? extends Enum> enumClass) {
        List<Method> encoders = new ArrayList<>();
        Method[] methods = enumClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Encoder.class)) {
                if (method.getParameterCount() != 0) {
                    encoderCache.put(enumClass, ERROR.PARAMETER_UNMATCHED);
                    return;
                }
                Class<?> returnType = method.getReturnType();
                if (!VALID_ENUM_KEY_TYPE.contains(returnType)) {
                    encoderCache.put(enumClass, ERROR.INVALID_RETURN_TYPE);
                    return;
                }
                if (!encoders.isEmpty()) {
                    encoderCache.put(enumClass, ERROR.MULTIPLE_ENCODER);
                    return;
                }
                encoders.add(method);
            }
        }

        if (encoders.isEmpty()) {
            encoders.add(null);
        }
        encoderCache.put(enumClass, encoders);
    }

    @SuppressWarnings("unchecked")
    private static void loadEnums(Class<? extends Enum> enumClass) {
        Method encoderMethod = null;
        Object encoder = encoderCache.get(enumClass);
        if (encoder instanceof List) {
            encoderMethod = ((List<Method>) encoder).get(0);
        }

        if (encoderMethod != null) {
            Map<Object, Enum<?>> keyToEnum = new HashMap<>();
            Map<Enum<?>, Object> enumToKey = new HashMap<>();

            for (Enum<?> e : enumClass.getEnumConstants()) {
                try {
                    Object key = encoderMethod.invoke(e);

                    keyToEnum.put(key, e);

                    if (enumToKey.containsKey(e)) {
                        encoderCache.put(enumClass, ERROR.MULTIPLE_ELEMENT);
                        return;
                    }
                    enumToKey.put(e, key);
                } catch (IllegalAccessException exception) {
                    encoderCache.put(enumClass, ERROR.ILLEGAL_ACCESS);
                    return;
                } catch (InvocationTargetException exception) {
                    encoderCache.put(enumClass, ERROR.INVOCATION_ERROR);
                    return;
                }
            }

            keyToEnumCache.put(enumClass, keyToEnum);
            enumToKeyCache.putAll(enumToKey);
        }
    }

}
