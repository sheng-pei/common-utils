package ppl.common.utils.http.header.known;

import ppl.common.utils.http.Name;
import ppl.common.utils.http.header.Context;
import ppl.common.utils.http.header.Header;
import ppl.common.utils.http.header.HeaderValue;
import ppl.common.utils.http.header.value.ListValue;
import ppl.common.utils.http.header.value.transfercoding.Coding;
import ppl.common.utils.http.symbol.HttpCharGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Name("Transfer-Encoding")
public class TransferEncoding implements Header<ListValue<Coding>> {
    @SuppressWarnings("rawtypes")
    private final ListValue values;

    private TransferEncoding(String value, Context context) {
        this.values = ListValue.create(value, context, TransferEncoding::split, Coding::create);
    }

    public TransferEncoding(List<Coding> codings) {
        this.values = ListValue.create(codings);
    }

    private static List<String> split(String value, char delimiter) {
        List<String> res = new ArrayList<>();
        char[] chars = value.toCharArray();
        int start = 0;
        boolean quoted = false;
        boolean doubleQuoted = false;
        int i = 0;
        while (i < chars.length) {
            char c = chars[i++];
            if (!doubleQuoted) {
                if (delimiter == c) {
                    res.add(new String(chars, start, i - 1));
                    start = i;
                } else if (HttpCharGroup.DQUOTE.test(c)) {
                    doubleQuoted = true;
                }
            } else {
                if (quoted) {
                    quoted = false;
                } else if (HttpCharGroup.DQUOTE.test(c)) {
                    doubleQuoted = false;
                } else if (HttpCharGroup.BS.test(c)) {
                    quoted = true;
                }
            }
        }
        res.add(new String(chars, start, chars.length - start));
        return res;
    }

    @Override
    public ListValue<HeaderValue> value() {
        @SuppressWarnings("unchecked")
        ListValue<HeaderValue> res = values;
        return res;
    }

    @Override
    public ListValue<Coding> knownValue() {
        @SuppressWarnings("unchecked")
        Stream<HeaderValue> stream = values.getValues().stream();
        return ListValue.create(stream
                .filter(v -> v instanceof Coding)
                .map(v -> (Coding) v)
                .collect(Collectors.toList()));
    }
}
