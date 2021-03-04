package ppl.common.utils;

public interface ConfigReader {

    int SCALAR_SIZE = -1;

    ConfigReader NULL_CONFIG_READER = new ConfigReader() {

        @Override
        public String getStringOrDefault(Object key, String def) {
            return def;
        }

        @Override
        public String getString(Object key) {
            return null;
        }

        @Override
        public Integer getIntegerOrDefault(Object key, Integer def) {
            return def;
        }

        @Override
        public Integer getInteger(Object key) {
            return null;
        }

        @Override
        public Long getLongOrDefault(Object key, Long def) {
            return def;
        }

        @Override
        public Long getLong(Object key) {
            return null;
        }

        @Override
        public Boolean getBooleanOrDefault(Object key, Boolean def) {
            return def;
        }

        @Override
        public Boolean getBoolean(Object key) {
            return null;
        }

        @Override
        public <M> M getOrDefault(Object key, M def, Class<M> targetClass) {
            return def;
        }

        @Override
        public <M> M get(Object key, Class<M> targetClass) {
            return null;
        }

        @Override
        public ConfigReader getReader(Object key) {
            return NULL_CONFIG_READER;
        }

        @Override
        public String forStringOrDefault(String def) {
            return def;
        }

        @Override
        public String forString() {
            return null;
        }

        @Override
        public Integer forIntegerOrDefault(Integer def) {
            return def;
        }

        @Override
        public Integer forInteger() {
            return null;
        }

        @Override
        public Long forLongOrDefault(Long def) {
            return def;
        }

        @Override
        public Long forLong() {
            return null;
        }

        @Override
        public Boolean forBooleanOrDefault(Boolean def) {
            return def;
        }

        @Override
        public Boolean forBoolean() {
            return null;
        }

        @Override
        public <M> M forCustomOrDefault(M def, Class<M> targetClass) {
            return def;
        }

        @Override
        public <M> M forCustom(Class<M> targetClass) {
            return null;
        }

        @Override
        public int size() {
            return SCALAR_SIZE;
        }

    };

    String getStringOrDefault(Object key, String def);

    String getString(Object key);

    Integer getIntegerOrDefault(Object key, Integer def);

    Integer getInteger(Object key);

    Long getLongOrDefault(Object key, Long def);

    Long getLong(Object key);

    Boolean getBooleanOrDefault(Object key, Boolean def);

    Boolean getBoolean(Object key);

    <M> M getOrDefault(Object key, M def, Class<M> targetClass);

    <M> M get(Object key, Class<M> targetClass);

    ConfigReader getReader(Object key);

    String forStringOrDefault(String def);

    String forString();

    Integer forIntegerOrDefault(Integer def);

    Integer forInteger();

    Long forLongOrDefault(Long def);

    Long forLong();

    Boolean forBooleanOrDefault(Boolean def);

    Boolean forBoolean();

    <M> M forCustomOrDefault(M def, Class<M> targetClass);

    <M> M forCustom(Class<M> targetClass);

    int size();

}
