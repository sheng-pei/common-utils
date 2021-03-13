package ppl.common.utils.config;

public interface Getter {

    String getStringOrDefault(Object key, String def);

    String getString(Object key);

    Integer getIntegerOrDefault(Object key, Integer def);

    Integer getInteger(Object key);

    Long getLongOrDefault(Object key, Long def);

    Long getLong(Object key);

    Boolean getBooleanOrDefault(Object key, Boolean def);

    Boolean getBoolean(Object key);

    <E extends Enum<E>> E getEnum(Object key, Class<E> enumClass);

    <M> M getOrDefault(Object key, Class<M> targetClass, M def);

    <M> M get(Object key, Class<M> targetClass);

}
