package ppl.common.utils.http.header;

import ppl.common.utils.http.Name;

import java.util.function.BiFunction;

public interface Header<V extends HeaderValue> {
    char SEPARATOR = ':';

    default boolean isNet() {
        return !name().isInternal();
    }

    default HeaderName name() {
        HeaderName name = extractName(this.getClass());
        if (name == null) {
            throw new IllegalStateException(String.format(
                    "Please add name annotation for class '%s'.",
                    this.getClass().getCanonicalName()));
        }
        return name;
    }

    default boolean isHeader(Class<? extends Header<? extends HeaderValue>> clazz) {
        if (clazz == null) {
            return false;
        }
        HeaderName name = extractName(clazz);
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

    static HeaderName extractName(Class<?> clazz) {
        Name name = clazz.getAnnotation(Name.class);
        if (name == null) {
            return null;
        }
        return HeaderName.create(name.value());
    }

    BiFunction<HeaderName, HeaderValue, String> TO_CANONICAL_STRING = (n, v) ->
            n.toString() + SEPARATOR + " " + (v == null ? "" : v.toCanonicalString());
}
