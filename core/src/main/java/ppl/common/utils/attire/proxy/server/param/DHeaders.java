package ppl.common.utils.attire.proxy.server.param;

import ppl.common.utils.http.header.Header;
import ppl.common.utils.http.header.HeaderFactory;
import ppl.common.utils.http.header.HeaderValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DHeaders {
    private final List<Header<HeaderValue>> headers;

    public DHeaders(Builder builder) {
        this.headers = Collections.unmodifiableList(new ArrayList<>(builder.headers));
    }

    public List<Header<HeaderValue>> getHeaders() {
        return this.headers;
    }

    public static final class Builder {
        private final List<Header<HeaderValue>> headers = new ArrayList<>();

        public Builder appendHeader(Header<? extends HeaderValue> header) {
            List<Header<HeaderValue>> headers = this.headers;
            @SuppressWarnings("unchecked")
            Header<HeaderValue> h = (Header<HeaderValue>) header;
            headers.add(h);
            return this;
        }

        public Builder appendHeader(String header) {
            this.headers.add(HeaderFactory.def().create(header));
            return this;
        }

        public DHeaders build() {
            return new DHeaders(this);
        }
    }
}
