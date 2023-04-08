package ppl.common.utils.command.argument.splitter;

import ppl.common.utils.command.argument.Splitter;

import java.util.Collections;

public class Splitters {
    private static final Splitter DEFAULT_SPLITTER = new ppl.common.utils.command.argument.splitter.Splitter();
    public static final Splitter NOT_SPLIT = Collections::singletonList;

    public static Splitter split() {
        return DEFAULT_SPLITTER;
    }

    public static Splitter split(String delim) {
        delim = delim == null ? "" : delim;
        if (delim.isEmpty()) {
            return DEFAULT_SPLITTER;
        }
        return new ppl.common.utils.command.argument.splitter.Splitter(delim);
    }
}
