package ppl.common.utils.http.request;

import ppl.common.utils.http.Headers;
import ppl.common.utils.http.header.Header;
import ppl.common.utils.http.header.HeaderName;
import ppl.common.utils.http.header.HeaderValue;
import ppl.common.utils.http.header.known.ContentLength;
import ppl.common.utils.http.header.known.TransferEncoding;
import ppl.common.utils.http.header.value.transfercoding.Coding;
import ppl.common.utils.http.header.value.transfercoding.CodingKind;
import ppl.common.utils.http.url.URL;
import ppl.common.utils.string.Strings;

import javax.net.ssl.SSLContext;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Request implements Headers {

    private final Proxy proxy;
    private final SSLContext sslContext;
    private final Method method;
    private final URL url;
    private final int chunkedLength;
    @SuppressWarnings("rawtypes")
    private final List headers;

    private Request(Proxy proxy, SSLContext sslContext, Method method, URL url, int chunkedLength, List<? extends Header<? extends HeaderValue>> headers) {
        this.proxy = proxy;
        this.sslContext = sslContext;
        this.method = method;
        this.url = url;
        this.chunkedLength = chunkedLength;
        this.headers = headers == null ? Collections.emptyList() : headers;
    }

    public Method getMethod() {
        return method;
    }

    public URL getUrl() {
        return this.url;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public SSLContext getSslContext() {
        return sslContext;
    }

    public int getChunkedLength() {
        return chunkedLength;
    }

    @Override
    public List<Header<HeaderValue>> getHeaders() {
        @SuppressWarnings("unchecked")
        List<Header<HeaderValue>> headers = Collections.unmodifiableList(this.headers);
        return headers;
    }

    public Builder copy() {
        return new Builder(this);
    }

    public static Builder get(URL url) {
        return new Builder(Method.GET, url);
    }

    public static Builder post(URL url) {
        return new Builder(Method.POST, url);
    }

    public static Builder put(URL url) {
        return new Builder(Method.PUT, url);
    }

    public static Builder delete(URL url) {
        return new Builder(Method.DELETE, url);
    }

    public static class Builder {
        private Proxy proxy;
        private SSLContext sslContext;
        private final Method method;
        private URL url;
        private int chunkedLength;
        @SuppressWarnings("rawtypes")
        private List headers = Collections.emptyList();

        private Builder(Method method, URL url) {
            this.method = method;
            this.url = url;
        }

        private Builder(Request request) {
            method = request.method;
            url = request.url;
            headers = request.headers;
        }

        public Builder use(Proxy proxy) {
            if (this.proxy == null) {
                this.proxy = proxy;
            }
            return this;
        }

        public Builder use(SSLContext sslContext) {
            if (this.sslContext == null) {
                this.sslContext = sslContext;
            }
            return this;
        }

        public Builder chunkedLength(int chunkedLength) {
            clearHeader(TransferEncoding.class);
            List<Header<? extends HeaderValue>> headers = ensure(this.headers);
            headers.add(new TransferEncoding(
                    Collections.singletonList(
                            Coding.create(CodingKind.CHUNKED))));
            this.headers = headers;
            this.chunkedLength = chunkedLength;
            return this;
        }

        public Builder fixedLength(long fixedLength) {
            clearHeader(ContentLength.class);
            List<Header<? extends HeaderValue>> headers = ensure(this.headers);
            headers.add(new ContentLength(fixedLength));
            this.headers = headers;
            return this;
        }

        public Builder replaceQuery(String name, String value) {
            URL url = this.url;
            this.url = url.dynamic()
                    .removeDynamicQuery(name)
                    .appendDynamicQuery(name, value);
            return this;
        }

        public Builder appendQuery(String name, String value) {
            URL url = this.url;
            this.url = url.appendDynamicQuery(name, value);
            return this;
        }

        public Builder setHeader(Header<? extends HeaderValue> header) {
            List<Header<? extends HeaderValue>> headers = ensure(this.headers);
            int idx = -1;
            for (int i = 0; i < headers.size(); i++) {
                if (headers.get(i).name().equals(header.name())) {
                    if (idx == -1) {
                        idx = i;
                    } else {
                        throw new IllegalArgumentException(Strings.format(
                                "There are more than one header: '{}'.", header.name()));
                    }
                }
            }
            if (idx == -1) {
                throw new IllegalArgumentException(Strings.format(
                        "Header not found: '{}'", header.name()));
            }
            headers.set(idx, header);
            this.headers = headers;
            return this;
        }

        public Builder appendHeader(Header<? extends HeaderValue> header) {
            List<Header<? extends HeaderValue>> headers = ensure(this.headers);
            headers.add(header);
            this.headers = headers;
            return this;
        }

        public Builder clearHeader(Class<? extends Header<? extends HeaderValue>> clazz) {
            HeaderName name = Header.extractName(clazz);
            return clearHeader(name);
        }

        public Builder clearHeader(HeaderName name) {
            @SuppressWarnings("unchecked")
            List<Header<HeaderValue>> headers = this.headers;
            if (!headers.isEmpty()) {
                headers = headers.stream()
                        .filter(h -> !h.name().equals(name))
                        .collect(Collectors.toList());
            }
            this.headers = headers;
            return this;
        }

        @SuppressWarnings("rawtypes")
        private <T> List<T> ensure(List headers) {
            @SuppressWarnings("unchecked")
            List<T> ret = headers;
            if (ret == null || ret.isEmpty()) {
                ret = new ArrayList<>();
            }
            return ret;
        }

        public Header<HeaderValue> getHeader(HeaderName name) {
            return findHeader(name);
        }

        public Header<HeaderValue> getHeader(String name) {
            return findHeader(HeaderName.create(name));
        }

        public <T extends Header<? extends HeaderValue>> T getHeader(Class<T> clazz) {
            HeaderName targetName = Header.extractName(clazz);
            if (targetName == null) {
                throw new IllegalArgumentException("Unknown header: " + clazz.getCanonicalName());
            }

            @SuppressWarnings("unchecked")
            T res = (T) findHeader(targetName);
            return res;
        }

        private Header<HeaderValue> findHeader(HeaderName targetName) {
            @SuppressWarnings("unchecked")
            List<Header<HeaderValue>> headers = this.headers;
            return headers.stream()
                    .filter(h -> h.name().equals(targetName))
                    .findFirst()
                    .orElse(null);
        }

        public Request build() {
            @SuppressWarnings("unchecked")
            List<? extends Header<? extends HeaderValue>> headers = this.headers;
            return new Request(proxy, sslContext, method, url, chunkedLength, headers);
        }

    }

}
