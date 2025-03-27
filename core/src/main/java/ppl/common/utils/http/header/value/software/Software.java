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
        int e = Strings.lastIndexOf(HttpCharGroup.WHITESPACE.negate(), value);
        int i = Strings.indexOf(HttpCharGroup.WHITESPACE.negate(), value);
        boolean start = true;
        while (i >= 0 && i < value.length()) {
            if (!start) {
                String whitespace = Lexer.extractRequiredWhitespace(value, i);
                if (null == whitespace) {
                    throw new IllegalArgumentException("Whitespace is required.");
                }
                i += whitespace.length();
            }

            String segment = Lexer.extractProduct(value, i);
            if (segment != null) {
                components.add(segment);
                i += segment.length();
                start = false;
                continue;
            }

            segment = Lexer.extractComment(value, i);
            if (segment != null) {
                components.add(segment);
                i += segment.length();
                start = false;
                continue;
            }

            throw new IllegalArgumentException("Invalid software component.");
        }
    }

    @Override
    public String toCanonicalString() {
        return String.join(" ", components);
    }
}
