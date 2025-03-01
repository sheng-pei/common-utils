package ppl.common.utils.http.property;

import ppl.common.utils.http.request.RequestInitializer;

import java.util.*;
import java.util.stream.Collectors;

//TODO, Use this to save cookie.
public class Properties {
    @SuppressWarnings("rawtypes")
    private final Map properties;

    private Properties(Map<?, ?> properties) {
        this.properties = Collections.unmodifiableMap(properties);
    }

    public final boolean hasProperty(String name) {
        return properties.containsKey(name);
    }

    public final Object getProperty(String name) {
        if (hasProperty(name)) {
            Object obj = properties.get(name);
            if (obj instanceof Element) {
                Element<?> e = (Element<?>) properties.get(name);
                return e.get();
            }
            return obj;
        }
        return null;
    }

    public final Collection<RequestInitializer> getRequestInitializer() {
        @SuppressWarnings("unchecked")
        Collection<Object> res = properties.values();
        return res.stream()
                .filter(o -> o instanceof RequestInitializer)
                .map(o -> (RequestInitializer) o)
                .collect(Collectors.toList());
    }

    public Builder copy() {
        return new Builder(this);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        @SuppressWarnings("rawtypes")
        private final Map properties;

        private Builder(Properties properties) {
            @SuppressWarnings({"rawtypes", "unchecked"})
            Map p = new HashMap(properties.properties);
            this.properties = p;
        }

        private Builder() {
            this.properties = new HashMap<>();
        }

        public Builder putProperty(String name, Object value) {
            Objects.requireNonNull(name);
            if (value != null) {
                @SuppressWarnings("unchecked")
                Map<Object, Object> p = (Map<Object, Object>) this.properties;
                p.put(name.trim(), ElementFactory.def().create(name, value));
            }
            return this;
        }

        public Properties build() {
            return new Properties(properties);
        }

    }

}
