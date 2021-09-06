package ppl.common.utils.enumerate;

import ppl.common.utils.Condition;
import ppl.common.utils.StringUtils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

final class EnumKey {

    private final static Set<Class<?>> INTEGER_KEY_TYPE = Collections.unmodifiableSet(new LinkedHashSet<Class<?>>() {
        {
            this.add(byte.class);
            this.add(short.class);
            this.add(int.class);
            this.add(long.class);

            this.add(Byte.class);
            this.add(Short.class);
            this.add(Integer.class);
            this.add(Long.class);
        }
    });

    private final static Set<Class<?>> CHARACTER_KEY_TYPE = Collections.unmodifiableSet(new LinkedHashSet<Class<?>>() {
        {
            this.add(char.class);
            this.add(Character.class);
        }
    });

    private final static Set<Class<?>> VALID_ENUM_KEY_TYPE = Collections.unmodifiableSet(new LinkedHashSet<Class<?>>() {
        {
            this.addAll(INTEGER_KEY_TYPE);
            this.addAll(CHARACTER_KEY_TYPE);
            this.add(String.class);
        }
    });

    static boolean isSupported(Class<?> keyType) {
        return VALID_ENUM_KEY_TYPE.contains(keyType);
    }

    static Set<Class<?>> getSupported() {
        return VALID_ENUM_KEY_TYPE;
    }

    static EnumKey wrap(Object key) {
        return new EnumKey(key);
    }

    static Object unwrap(EnumKey key) {
        return key.key;
    }

    @SuppressWarnings("unchecked")
    static <T> T unwrap(EnumKey key, Class<T> clazz) {
        if (key.canonicalKey == null) {
            key.init();
        }

        Object ck = key.canonicalKey;
        if (ck instanceof Integer) {
            Integer iKey = (Integer) ck;
            if (isInt(clazz)) {
                return (T) iKey;
            } else if (isLong(clazz)) {
                return (T) (Long) iKey.longValue();
            } else if (isShort(clazz)) {
                if (inShort(iKey)) {
                    return (T) (Short) iKey.shortValue();
                }
            } else if (isByte(clazz)) {
                if (inByte(iKey)) {
                    return (T) (Byte) iKey.byteValue();
                }
            }
        } else if (ck instanceof String) {
            if (clazz.equals(String.class)) {
                return (T) ck;
            }
        } else if (ck instanceof Long) {
            if (isLong(clazz)) {
                return (T) ck;
            }
        } else if (ck instanceof Character) {
            if (isChar(clazz)) {
                return (T) ck;
            }
        } else {
            if (ck.getClass().equals(clazz)) {
                return (T) clazz.cast(ck);
            }
        }

        throw new IllegalArgumentException(StringUtils.format(
                "Could not convert to {}", clazz.getCanonicalName()));
    }

    private static boolean isInt(Class<?> clazz) {
        return clazz.equals(Integer.class) || clazz.equals(int.class);
    }

    private static boolean isLong(Class<?> clazz) {
        return clazz.equals(Long.class) || clazz.equals(long.class);
    }

    private static boolean isShort(Class<?> clazz) {
        return clazz.equals(Short.class) || clazz.equals(short.class);
    }

    private static boolean isByte(Class<?> clazz) {
        return clazz.equals(Byte.class) || clazz.equals(byte.class);
    }

    private static boolean isChar(Class<?> clazz) {
        return clazz.equals(Character.class) || clazz.equals(char.class);
    }

    private final Object key;
    private volatile transient Integer hashCode;
    private volatile transient Object canonicalKey;

    private EnumKey(Object key) {
        Objects.requireNonNull(key, "Key is null");
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnumKey enumKey = (EnumKey) o;
        if (this.canonicalKey == null) {
            init();
        }
        if (enumKey.canonicalKey == null) {
            enumKey.init();
        }
        return this.canonicalKey.equals(enumKey.canonicalKey);
    }

    @Override
    public int hashCode() {
        if (this.hashCode == null) {
            init();
        }
        return this.hashCode;
    }

    private void init() {
        this.canonicalKey = canonicalKey(this.key);
        this.hashCode = Objects.hashCode(canonicalKey);
    }

    private Object canonicalKey(Object key) {
        if (INTEGER_KEY_TYPE.contains(key.getClass())) {
            //Integer optimise
            if (key instanceof Integer) {
                return key;
            }
            Long l =  ((Number) key).longValue();
            if (outOfInt(l)) {
                return l;
            }
            return l.intValue();
        }
        return key;
    }

    private static boolean outOfInt(Long l) {
        return !Condition.in(l, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
    private static boolean inShort(Integer i) {
        return Condition.in(i, Short.MIN_VALUE, Short.MAX_VALUE);
    }
    private static boolean inByte(Integer i) {
        return Condition.in(i, Byte.MIN_VALUE, Byte.MAX_VALUE);
    }
}
