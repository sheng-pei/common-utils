package ppl.common.utils.argument.splitter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class DelimiterSplitter implements Function<String, Stream<String>> {
    private final String delim;

    DelimiterSplitter() {
        this(",");
    }

    DelimiterSplitter(String delim) {
        this.delim = delim;
    }

    @Override
    public Stream<String> apply(String value) {
        if (value.isEmpty()) {
            return Stream.empty();
        }
        List<String> res = new ArrayList<>();
        int start = 0;
        while (start < value.length()) {
            int idx = value.indexOf(delim, start);
            if (idx < 0) {
                res.add(value.substring(start));
                break;
            }
            res.add(value.substring(start, idx));
            start = idx + delim.length();
        }
        return res.stream();
    }
}
