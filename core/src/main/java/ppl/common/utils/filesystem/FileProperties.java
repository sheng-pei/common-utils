package ppl.common.utils.filesystem;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FileProperties {

    private static final String TYPE = "type";
    private static final String MODIFIED = "modified";
    private static final String CREATED = "created";
    private final Map<String, Object> properties;

    private FileProperties(Map<String, Object> properties) {
        this.properties = Collections.unmodifiableMap(properties);
    }

    public FileType getType() {
        return (FileType) this.properties.getOrDefault(TYPE, FileType.FILE);
    }

    public LocalDateTime getModified() {
        return (LocalDateTime) this.properties.get(MODIFIED);
    }

    public LocalDateTime getCreated() {
        return (LocalDateTime) this.properties.get(CREATED);
    }

    public Builder copy() {
        return new Builder(properties);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private Map<String, Object> properties = Collections.emptyMap();

        private Builder() {
        }

        private Builder(Map<String, Object> properties) {
            if (!properties.isEmpty()) {
                this.properties = new HashMap<>(properties);
            }
        }

        private void ensureProperties() {
            if (properties.isEmpty()) {
                properties = new HashMap<>();
            }
        }

        public void directory() {
            type(FileType.DIRECTORY);
        }

        public void file() {
            type(FileType.FILE);
        }

        public void type(FileType type) {
            ensureProperties();
            this.properties.put(TYPE, type);
        }

        public void modified(LocalDateTime modified) {
            ensureProperties();
            this.properties.put(MODIFIED, modified);
        }

        public void created(LocalDateTime created) {
            ensureProperties();
            this.properties.put(CREATED, created);
        }

        public FileProperties build() {
            return new FileProperties(properties);
        }

    }

}
