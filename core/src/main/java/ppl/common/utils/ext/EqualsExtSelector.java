package ppl.common.utils.ext;

import java.util.*;

public class EqualsExtSelector implements ExtSelector {
    private final Map<String, List<ExtPattern>> exts = new HashMap<>();

    @Override
    public List<ExtPattern> select(String item) {
        return exts.getOrDefault(item.toLowerCase(), Collections.emptyList());
    }

    @Override
    public void addPattern(ExtPattern pattern) {
        String key = pattern.ext().toLowerCase();
        int idx = key.lastIndexOf('.');
        if (idx >= 0) {
            key = key.substring(idx + 1);
        }
        List<ExtPattern> patterns = exts.computeIfAbsent(key, k -> new ArrayList<>());
        patterns.add(pattern);
    }
}
