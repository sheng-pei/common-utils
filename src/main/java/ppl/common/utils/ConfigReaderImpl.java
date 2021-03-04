package ppl.common.utils;

import ppl.common.utils.exception.ConfigReaderException;
import ppl.common.utils.exception.ConvertException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ConfigReaderImpl implements ConfigReader {

    private Object configObject;
    private Map<?, ?> configsMap;
    private List<?> configsList;

    private ConfigReaderImpl(Map<?, ?> configsMap) {
        this.configsMap = configsMap;
        this.configsList = null;
        this.configObject = configsMap;
    }

    private ConfigReaderImpl(Object configObject) {
        this.configsList = toList(configObject);
        this.configsMap = toMap(configObject);
        this.configObject = configObject;
    }

    private List<?> toList(Object configObject) {
        if (configObject == null) {
            return null;
        }

        @SuppressWarnings("rawtypes")
        Converter<Collection> collectionConverter = Converter.getInstance(Collection.class);
        Collection<?> tmp = collectionConverter.convertNullIfConvertExcept(configObject);
        return tmp == null ? null : new ArrayList<>(tmp);
    }

    private Map<?, ?> toMap(Object configObject) {
        if (configObject == null) {
            return null;
        }

        @SuppressWarnings("rawtypes")
        Converter<Map> mapConverter = Converter.getInstance(Map.class);
        return mapConverter.convertNullIfConvertExcept(configObject);
    }

    private ConfigReaderImpl(Collection<?> configsCollection) {
        this.configsList = configsCollection == null ? null : new ArrayList<>(configsCollection);
        this.configsMap = null;
        this.configObject = configsCollection;
    }

    public static ConfigReader createFromObject(Object configObject) {
        if (configObject == null) {
            return ConfigReader.NULL_CONFIG_READER;
        }

        return new ConfigReaderImpl(configObject);
    }

    public static ConfigReader createFromMap(Map<?, ?> configsMap) {
        if (configsMap == null) {
            return ConfigReader.NULL_CONFIG_READER;
        }

        return new ConfigReaderImpl(configsMap);
    }

    public static ConfigReader createFromCollection(Collection<?> configsCollection) {
        if (configsCollection == null) {
            return ConfigReader.NULL_CONFIG_READER;
        }

        return new ConfigReaderImpl(configsCollection);
    }

    @Override
    public Integer getIntegerOrDefault(Object key, Integer def) {
        return getOrDefault(key, def, Integer.class);
    }

    @Override
    public Integer getInteger(Object key) {
        return get(key, Integer.class);
    }

    @Override
    public Long getLongOrDefault(Object key, Long def) {
        return getOrDefault(key, def, Long.class);
    }

    @Override
    public Long getLong(Object key) {
        return get(key, Long.class);
    }

    @Override
    public String getStringOrDefault(Object key, String def) {
        return getOrDefault(key, def, String.class);
    }

    @Override
    public String getString(Object key) {
        return get(key, String.class);
    }

    @Override
    public Boolean getBooleanOrDefault(Object key, Boolean def) {
        return getOrDefault(key, def, Boolean.class);
    }

    @Override
    public Boolean getBoolean(Object key) {
        return get(key, Boolean.class);
    }

    @Override
    public <M> M getOrDefault(Object key, M def, Class<M> targetClass) {
        M v = get(key, targetClass);
        return v == null ? def : v;
    }

    @Override
    public <M> M get(Object key, Class<M> targetClass) {
        Object v = this.get(key);
        if (v == null) {
            return null;
        }

        try {
            return convert(v, targetClass);
        } catch (ConvertException e) {
            throw ConfigReaderException.newExceptionForMember(key.toString(), v, e);
        }
    }

    @Override
    public ConfigReader getReader(Object key) {
        return createFromObject(this.get(key));
    }

    @Override
    public String forStringOrDefault(String def) {
        return forCustomOrDefault(def, String.class);
    }

    @Override
    public String forString() {
        return forCustom(String.class);
    }

    @Override
    public Integer forIntegerOrDefault(Integer def) {
        return forCustomOrDefault(def, Integer.class);
    }

    @Override
    public Integer forInteger() {
        return forCustom(Integer.class);
    }

    @Override
    public Long forLongOrDefault(Long def) {
        return forCustomOrDefault(def, Long.class);
    }

    @Override
    public Long forLong() {
        return forCustom(Long.class);
    }

    @Override
    public Boolean forBooleanOrDefault(Boolean def) {
        return forCustomOrDefault(def, Boolean.class);
    }

    @Override
    public Boolean forBoolean() {
        return forCustom(Boolean.class);
    }

    @Override
    public <M> M forCustomOrDefault(M def, Class<M> targetClass) {
        M res = forCustom(targetClass);
        return res == null ? def : res;
    }

    @Override
    public <M> M forCustom(Class<M> targetClass) {
        if (this.configObject == null) {
            return null;
        }

        try {
            return convert(this.configObject, targetClass);
        } catch (ConvertException e) {
            throw ConfigReaderException.newException(this.configObject);
        }
    }

    /**
     * map preferred
     * @return size
     */
    @Override
    public int size() {
        if (configsMap != null) {
            return this.configsMap.size();
        } else if (configsList != null) {
            return this.configsList.size();
        }
        return ConfigReader.SCALAR_SIZE;
    }

    private <M> M convert(Object src, Class<M> targetClass) {
        Converter<M> converter = Converter.getInstance(targetClass);
        return converter.convert(src);
    }

    /**
     * map preferred
     * @param key
     * @return
     */
    private Object get(Object key) {
        if (this.configsMap == null && this.configsList == null) {
            return null;
        }

        if (this.configsMap != null) {
            return this.configsMap.get(key);
        }

        Integer intKey = toInt(key);
        return intKey == null ? null : this.configsList.get(intKey);
    }

    private Integer toInt(Object key) {
        Converter<Integer> intConverter = Converter.getInstance(Integer.class);
        Integer intKey = intConverter.convertNullIfConvertExcept(key);

        if (intKey == null) {
            Converter<String> stringConverter = Converter.getInstance(String.class);
            String tmp = stringConverter.convertNullIfConvertExcept(key);
            try {
                intKey = tmp == null ? null : Integer.parseInt(tmp);
            } catch (NumberFormatException e) {
                //Ignore, not error
            }
        }
        return intKey;
    }

}
