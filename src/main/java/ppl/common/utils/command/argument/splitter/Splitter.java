package ppl.common.utils.command.argument.splitter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Splitter implements ppl.common.utils.command.argument.Splitter {
    private final String delim;

    Splitter() {
        this(",");
    }

    Splitter(String delim) {
        this.delim = delim;
    }

    @Override
    public List<String> split(String value) {
        if (value.isEmpty()) {
            return Collections.singletonList(value);
        }
        List<String> res = new ArrayList<>();
        int start = 0;
        while (start < value.length()) {
            int idx = value.indexOf(delim, start);
            if (idx < 0) {
                res.add(value.substring(start));
                return res;
            }
            res.add(value.substring(start, idx));
            start = idx + delim.length();
        }
        return res;
    }
}
