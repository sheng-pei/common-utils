package ppl.common.utils.attire.proxy.server;

import ppl.common.utils.http.request.Method;
import ppl.common.utils.net.HierURI;
import ppl.common.utils.string.Strings;
import ppl.common.utils.string.variable.VariableParser;
import ppl.common.utils.string.variable.replacer.StringReplacer;

import java.util.Map;

public class RequestLinePojo {

    private final Method method;
    private final StringReplacer requestLine;

    public RequestLinePojo(RequestLine requestLine) {
        String uri = requestLine.uri();
        if (uri.isEmpty()) {
            uri = requestLine.value();
        } else if (!requestLine.value().isEmpty()) {
            throw new IllegalArgumentException("Both uri and value is specified. This is not allowed.");
        }

        if (Strings.isNotEmpty(uri)) {
            HierURI hUri = HierURI.create(uri);
            if (hUri.isAbsolute()) {
                throw new IllegalArgumentException("Absolute uri is not allowed.");
            }

            if (hUri.getAuthority() != null) {
                throw new IllegalArgumentException("Invalid uri, authority is not allowed.");
            }
        }
        this.method = requestLine.method();
        this.requestLine = VariableParser.parse(uri);
    }

    public Method method() {
        return method;
    }

    public String requestLine(Map<String, String> variables) {
        return requestLine.replace(variables, true);
    }

    @Override
    public String toString() {
        return method + " " + requestLine;
    }
}
