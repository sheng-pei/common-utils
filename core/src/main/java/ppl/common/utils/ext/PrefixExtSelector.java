package ppl.common.utils.ext;

import java.util.*;
import java.util.regex.Pattern;

public class PrefixExtSelector implements ExtSelector {

    private final Map<String, List<ExtPattern>> patterns = new HashMap<>();
    private Pattern pattern = null;

    @Override
    public List<ExtPattern> select(String item) {
        if (patterns.isEmpty()) {
            return Collections.emptyList();
        }

        if (pattern == null) {
            Set<String> prefixes = patterns.keySet();
            StringBuilder builder = new StringBuilder();
            for (String p : prefixes) {
                builder.append("^(").append(Pattern.quote(p)).append(")|");
            }
            if (builder.length() > 0) {
                builder.setLength(builder.length() - 1);
            }
            pattern = Pattern.compile(builder.toString());
        }
        pattern.matcher(item);
        return null;
    }

    @Override
    public void addPattern(ExtPattern pattern) {

    }
}
