package ppl.common.utils.http;

import ppl.common.utils.argument.argument.value.collector.ExCollectors;
import ppl.common.utils.http.header.Header;
import ppl.common.utils.http.header.HeaderName;
import ppl.common.utils.http.header.HeaderValue;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface Headers {
    List<Header<HeaderValue>> getHeaders();

    default List<Header<HeaderValue>> getHeaders(String name) {
        if (name == null || name.isEmpty()) {
            return Collections.emptyList();
        }

        return getHeaders(HeaderName.create(name));
    }

    default  <V extends HeaderValue, T extends Header<V>> List<T> getHeaders(Class<T> clazz) {
        if (clazz == null) {
            return Collections.emptyList();
        }

        @SuppressWarnings("unchecked")
        List<T> res = (List<T>) getHeaders(Header.nameOf(clazz));
        return res;
    }

    default List<Header<HeaderValue>> getHeaders(HeaderName name) {
        return getHeaders().stream()
                .filter(h -> h.isHeader(name))
                .collect(Collectors.toList());
    }

    default Header<HeaderValue> getHeader(String name) {
        if (name == null) {
            return null;
        }

        return getHeader(HeaderName.create(name));
    }

    default  <V extends HeaderValue, T extends Header<V>> T getHeader(Class<T> clazz) {
        if (clazz == null) {
            return null;
        }

        @SuppressWarnings("unchecked")
        T res = (T) getHeader(Header.nameOf(clazz));
        return res;
    }

    default Header<HeaderValue> getHeader(HeaderName name) {
        return getHeaders().stream()
                .filter(h -> h.isHeader(name))
                .collect(ExCollectors.one());
    }

    default boolean containsHeader(String name) {
        if (name == null) {
            return false;
        }

        return containsHeader(HeaderName.create(name));
    }

    default boolean containsHeader(HeaderName name) {
        return getHeaders().stream()
                .anyMatch(h -> h.isHeader(name));
    }

    default <V extends HeaderValue, T extends Header<V>> boolean containsHeader(Class<T> clazz) {
        if (clazz == null) {
            return false;
        }

        return containsHeader(Header.nameOf(clazz));
    }
}
