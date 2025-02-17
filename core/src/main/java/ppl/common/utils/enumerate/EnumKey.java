package ppl.common.utils.enumerate;

import ppl.common.utils.Numbers;
import ppl.common.utils.reflect.Types;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

final class EnumKey {

    private final static Set<Class<?>> VALID_ENUM_KEY_TYPE = Collections.unmodifiableSet(new LinkedHashSet<Class<?>>() {
        {
            this.add(byte.class);
            this.add(Byte.class);
            this.add(short.class);
            this.add(Short.class);
            this.add(int.class);
            this.add(Integer.class);
            this.add(long.class);
            this.add(Long.class);
            this.add(char.class);
            this.add(Character.class);
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
        Objects.requireNonNull(key);
        if (!isSupported(key.getClass())) {
            throw new IllegalArgumentException("'" + key.getClass() + "' is not supported by enum encoder.");
        }
        return new EnumKey(key);
    }

    static EnumKey wrap(Object key, boolean caseInsensitive) {
        Objects.requireNonNull(key);
        if (!isSupported(key.getClass())) {
            throw new IllegalArgumentException("'" + key.getClass() + "' is not supported by enum encoder.");
        }
        return new EnumKey(key, caseInsensitive);
    }

    // return original key if case-sensitive or non-string key, otherwise lower case key.
    static Object unwrap(EnumKey key) {
        Object k = key.key;
        if (k instanceof String) {
            String s = (String) k;
            return key.caseInsensitive ? s.toLowerCase() : s;
        }
        return k;
    }

    private final Object key;
    private final boolean caseInsensitive;
    private volatile transient Integer hashCode;
    private volatile transient Object canonicalKey;

    private EnumKey(Object key) {
        this.key = key;
        this.caseInsensitive = false;
    }

    private EnumKey(Object key, boolean caseInsensitive) {
        this.key = key;
        this.caseInsensitive = caseInsensitive;
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
        this.canonicalKey = canonicalKey();
        this.hashCode = Objects.hashCode(canonicalKey);
    }

    private Object canonicalKey() {
        Object key = this.key;
        if (Types.isBaseInteger(key)) {
            if (key instanceof Integer) {
                return key;
            }

            if (!(key instanceof Long)) {
                return ((Number) key).intValue();
            }

            Long l =  (Long) key;
            if (Numbers.inInt(l)) {
                return l.intValue();
            }
            return l;
        } else if (key instanceof String) {
            String s = (String) key;
            return caseInsensitive ? s.toLowerCase() : s;
        }
        return key;
    }
}
