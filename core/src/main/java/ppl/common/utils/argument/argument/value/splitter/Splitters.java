package ppl.common.utils.argument.argument.value.splitter;

import java.util.function.Function;
import java.util.stream.Stream;

public class Splitters {
    private static final Function<String, Stream<String>> DEFAULT_DELIMITER_SPLITTER = new DelimiterSplitter();

    public static Function<String, Stream<String>> delimiter() {
        return DEFAULT_DELIMITER_SPLITTER;
    }

    public static Function<String, Stream<String>> delimiter(String delimiter) {
        delimiter = delimiter == null ? "" : delimiter;
        if (delimiter.isEmpty()) {
            return DEFAULT_DELIMITER_SPLITTER;
        }
        return new DelimiterSplitter(delimiter);
    }

}
