package ppl.common.utils.config;

import ppl.common.utils.config.convert.ConvertException;
import ppl.common.utils.config.convert.Converter;
import ppl.common.utils.StringUtils;
import ppl.common.utils.exception.ReaderException;

import java.util.LinkedHashMap;
import java.util.Objects;

abstract class AbstractReader implements Reader {

    private final Reader parent;
    protected final Object key;

    protected AbstractReader() {
        this.parent = null;
        this.key = null;
    }

    protected AbstractReader(Reader parent, Object key) {
        this.parent = parent;
        this.key = key;
    }

    ///////////////////////////////////////////////Reader///////////////////////////////////////////

    protected abstract String path(Object key);

    @Override
    public String absolutePath() {
        return this.absolutePath(this.parent == null);
    }

    private String absolutePath(boolean isRoot) {
        if (this.parent == null) {
            return isRoot ? Reader.ROOT_PATH : "";
        }

        return StringUtils.format(
                "{}.{}",
                ((AbstractReader) this.parent).absolutePath(isRoot),
                ((AbstractReader) this.parent).path(key)
        );
    }

    @Override
    public Reader getParent() {
        return this.parent;
    }

    @Override
    public Reader getChild(Object key) {
        Objects.requireNonNull(key, "The key is required for getting child");
        Object value = this.get(key);
        LinkedHashMap<Class<?>, Object> params = new LinkedHashMap<>();
        params.put(Reader.class, this);
        params.put(Object.class, key);
        return Readers.create(value, c -> Readers.newer(c, params));
    }

    //////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////Getter/////////////////////////////////////////////
    @Override
    public Integer getIntegerOrDefault(Object key, Integer def) {
        return getOrDefault(key, Integer.class, def);
    }

    @Override
    public Integer getInteger(Object key) {
        return get(key, Integer.class);
    }

    @Override
    public Long getLongOrDefault(Object key, Long def) {
        return getOrDefault(key, Long.class, def);
    }

    @Override
    public Long getLong(Object key) {
        return get(key, Long.class);
    }

    @Override
    public String getStringOrDefault(Object key, String def) {
        return getOrDefault(key, String.class, def);
    }

    @Override
    public String getString(Object key) {
        return get(key, String.class);
    }

    @Override
    public Boolean getBooleanOrDefault(Object key, Boolean def) {
        return getOrDefault(key, Boolean.class, def);
    }

    @Override
    public Boolean getBoolean(Object key) {
        return get(key, Boolean.class);
    }

    @Override
    public <E extends Enum<E>> E getEnum(Object key, Class<E> enumClass) {
        return get(key, enumClass);
    }

    @Override
    public <E extends Enum<E>> E getEnum(Object key, Class<E> enumClass, E def) {
        return getOrDefault(key, enumClass, def);
    }

    @Override
    public <M> M getOrDefault(Object key, Class<M> targetClass, M def) {
        M v = get(key, targetClass);
        return v == null ? def : v;
    }

    @Override
    public <M> M get(Object key, Class<M> targetClass) {
        Objects.requireNonNull(key, "Key must not be null");
        Objects.requireNonNull(targetClass, "TargetClass must not be null");

        Object v = this.get(key);
        if (v == null) {
            return null;
        }

        try {
            return convert(v, targetClass);
        } catch (ConvertException e) {
            throw new ReaderException(
                    StringUtils.format("Couldn't parse field {} in {}",
                            this.path(key),
                            this.absolutePath()), e);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * no exception
     * @param key key of child
     * @return value of child corresponding key
     */
    protected abstract Object get(Object key);

    //////////////////////////////////////////////////Key///////////////////////////////////////////
    @Override
    public String keyString() {
        return key(String.class);
    }

    @Override
    public Integer keyInteger() {
        return key(Integer.class);
    }

    @Override
    public Long keyLong() {
        return key(Long.class);
    }

    @Override
    public Boolean keyBoolean() {
        return key(Boolean.class);
    }

    @Override
    public <M> M key(Class<M> targetClass) {
        Objects.requireNonNull(targetClass, "TargetClass must not be null");
        try {
            return convert(this.key, targetClass);
        } catch (ConvertException e) {
            throw new ReaderException(
                    StringUtils.format(
                            "Couldn't parse key of {}",
                            this.absolutePath()), e);
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////Value/////////////////////////////////////////////////
    @Override
    public String valueStringOrDefault(String def) {
        return valueOrDefault(String.class, def);
    }

    @Override
    public String valueString() {
        return value(String.class);
    }

    @Override
    public Integer valueIntegerOrDefault(Integer def) {
        return valueOrDefault(Integer.class, def);
    }

    @Override
    public Integer valueInteger() {
        return value(Integer.class);
    }

    @Override
    public Long valueLongOrDefault(Long def) {
        return valueOrDefault(Long.class, def);
    }

    @Override
    public Long valueLong() {
        return value(Long.class);
    }

    @Override
    public Boolean valueBooleanOrDefault(Boolean def) {
        return valueOrDefault(Boolean.class, def);
    }

    @Override
    public Boolean valueBoolean() {
        return value(Boolean.class);
    }

    @Override
    public <E extends Enum<E>> E valueEnum(Class<E> enumClass) {
        return value(enumClass);
    }

    @Override
    public <M> M valueOrDefault(Class<M> targetClass, M def) {
        M res = value(targetClass);
        return res == null ? def : res;
    }

    @Override
    public <M> M value(Class<M> targetClass) {
        Objects.requireNonNull(targetClass, "TargetClass must not be null");

        Object self = this.value();
        if (self == null) {
            return null;
        }

        try {
            return convert(self, targetClass);
        } catch (ConvertException e) {
            throw new ReaderException(
                    StringUtils.format(
                            "Couldn't parse value of {}",
                            this.absolutePath()), e);
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * no exception
     * @return value of reader
     */
    protected abstract Object value();

    static <M> M convert(Object src, Class<M> targetClass) {
        Converter<M> converter = Converter.getInstance(targetClass);
        return converter.convert(src);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractReader)) return false;
        AbstractReader that = (AbstractReader) o;
        return Objects.equals(parent, that.parent) && Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, key);
    }
}
