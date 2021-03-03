package ppl.common.utils;

public interface ConfigReader {

    String getStringOrDefault(String key, String def);

    String getString(String key);

    Integer getIntegerOrDefault(String key, Integer def);

    Integer getInteger(String key);

    Long getLongOrDefault(String key, Long def);

    Long getLong(String key);

    Boolean getBooleanOrDefault(String key, Boolean def);

    Boolean getBoolean(String key);

    <M> M getOrDefault(String key, M def, Class<M> targetClass);

    <M> M get(String key, Class<M> targetClass);

}
