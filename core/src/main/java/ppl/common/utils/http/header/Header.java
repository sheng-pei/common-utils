package ppl.common.utils.http.header;

import ppl.common.utils.http.Name;

import java.util.function.BiFunction;

public interface Header<V extends HeaderValue> {
    char SEPARATOR = ':';

    default boolean isNet() {
        return !name().isInternal();
    }

    default HeaderName name() {
        return nameOf(this.getClass());
    }

    default boolean isHeader(Class<? extends Header<? extends HeaderValue>> clazz) {
        if (clazz == null) {
            return false;
        }
        HeaderName name = nameOf(clazz);
        return name().equals(name);
    }

    default boolean isHeader(HeaderName name) {
        return name().equals(name);
    }

    HeaderValue value();

    V knownValue();

    default String toCanonicalString() {
        return TO_CANONICAL_STRING.apply(name(), value());
    }

    static HeaderName nameOf(Class<?> clazz) {
        Name name = clazz.getAnnotation(Name.class);
        if (name == null) {
            throw new IllegalArgumentException("No name annotation is found in class: " +
                    clazz.getCanonicalName());
        }
        return HeaderName.create(name.value());
    }

    BiFunction<HeaderName, HeaderValue, String> TO_CANONICAL_STRING = (n, v) ->
            n.toString() + SEPARATOR + " " + (v == null ? "" : v.toCanonicalString());
}
