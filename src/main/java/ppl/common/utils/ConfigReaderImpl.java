package ppl.common.utils;

import ppl.common.utils.exception.ConfigException;
import ppl.common.utils.exception.ConvertException;

import java.util.Collections;
import java.util.Map;

public class ConfigReaderImpl implements ConfigReader {

    private Map<?, ?> configs;

    public ConfigReaderImpl(Map<?, ?> configs) {
        this.configs = Collections.unmodifiableMap(configs);
    }

    @Override
    public Integer getIntegerOrDefault(String key, Integer def) {
        return getOrDefault(key, def, Integer.class);
    }

    @Override
    public Integer getInteger(String key) {
        return get(key, Integer.class);
    }

    @Override
    public Long getLongOrDefault(String key, Long def) {
        return getOrDefault(key, def, Long.class);
    }

    @Override
    public Long getLong(String key) {
        return get(key, Long.class);
    }

    @Override
    public String getStringOrDefault(String key, String def) {
        return getOrDefault(key, def, String.class);
    }

    @Override
    public String getString(String key) {
        return get(key, String.class);
    }

    @Override
    public Boolean getBooleanOrDefault(String key, Boolean def) {
        return getOrDefault(key, def, Boolean.class);
    }

    @Override
    public Boolean getBoolean(String key) {
        return get(key, Boolean.class);
    }

    @Override
    public <M> M getOrDefault(String key, M def, Class<M> targetClass) {
        M v = get(key, targetClass);
        return v == null ? def : v;
    }

    @Override
    public <M> M get(String key, Class<M> targetClass) {
        Object v = this.configs.get(key);
        if (v == null) {
            return null;
        }

        Converter<M> converter = Converter.getInstance(targetClass);
        try {
            return converter.convert(v);
        } catch (ConvertException e) {
            throw ConfigException.newExceptionForConversion(key, v, e);
        }
    }
}
