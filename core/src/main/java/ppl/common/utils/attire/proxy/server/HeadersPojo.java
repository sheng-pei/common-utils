package ppl.common.utils.attire.proxy.server;

import ppl.common.utils.Collections;
import ppl.common.utils.string.Strings;
import ppl.common.utils.string.variable.replacer.StringReplacer;
import ppl.common.utils.string.variable.replacer.VariableString;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HeadersPojo {

    public static final HeadersPojo EMPTY = new HeadersPojo();

    private final List<StringReplacer> headers;

    private HeadersPojo() {
        this.headers = Collections.emptyList();
    }

    public HeadersPojo(Headers headers) {
        this(headers.value());
    }

    public HeadersPojo(Header[] headers) {
        this.headers = Collections.unmodifiableList(Arrays.stream(headers)
                .map(Header::value)
                .peek(s -> {
                    if (Strings.isNotEmpty(s)) {
                        throw new IllegalArgumentException("Header must not be empty.");
                    }
                })
                .map(VariableString::new)
                .collect(Collectors.toList()));
    }

    public List<String> headers(Map<String, String> variables) {
        return headers.stream()
                .map(h -> h.replace(variables))
                .collect(Collectors.toList());
    }
}
