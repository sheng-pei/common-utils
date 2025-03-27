package ppl.common.utils.http.property.elements;

import ppl.common.utils.http.Client;
import ppl.common.utils.http.Name;
import ppl.common.utils.http.header.known.UserAgent;
import ppl.common.utils.http.property.Element;
import ppl.common.utils.http.request.Request;
import ppl.common.utils.http.request.RequestInitializer;

@Name(Client.USER_AGENT)
public class EUserAgent implements Element<String>, RequestInitializer {
    private final String userAgent;

    public EUserAgent(Object userAgent) {
        this.userAgent = toString(userAgent);
    }

    private String toString(Object value) {
        String userAgent;
        if (value instanceof String) {
            userAgent = (String) value;
        } else {
            throw new IllegalArgumentException(String.format(
                    "Unsupported value type: '%s' for '%s'.",
                    value.getClass(), EUserAgent.class.getCanonicalName()));
        }
        return userAgent.trim();
    }

    @Override
    public String get() {
        return userAgent;
    }

    @Override
    public void init(Request.Builder request) {
        UserAgent header = request.getHeader(UserAgent.class);
        if (header == null) {
            request.setHeader(new UserAgent(userAgent));
        }
    }

}
