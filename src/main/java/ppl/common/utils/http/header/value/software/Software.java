package ppl.common.utils.http.header.value.software;

import ppl.common.utils.http.header.SingleLineHeaderValue;
import ppl.common.utils.http.symbol.HttpCharGroup;
import ppl.common.utils.http.symbol.Lexer;
import ppl.common.utils.string.Strings;

import java.util.ArrayList;
import java.util.List;

public class Software extends SingleLineHeaderValue {

    private final List<String> components = new ArrayList<>();

    public static Software create(String value) {
        return new Software(value);
    }

    private Software(String value) {
        super(value);
        int i = 0;
        while (i >= 0 && i < value.length()) {
            String segment = Lexer.extractProduct(value, i);
            if (!segment.isEmpty()) {
                components.add(segment);
            }
            i += segment.length();
            segment = Lexer.extractComment(value, i);
            if (!segment.isEmpty()) {
                components.add(segment);
            }
            i += segment.length();
            i = Strings.indexOf(HttpCharGroup.WS.negate(), value, i, value.length());
        }
    }

    @Override
    public String toCanonicalString() {
        return String.join(" ", components);
    }
}
