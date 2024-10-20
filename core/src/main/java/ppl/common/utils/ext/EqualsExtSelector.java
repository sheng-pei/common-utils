package ppl.common.utils.ext;

import java.util.*;

class EqualsExtSelector implements ExtSelector {
    private final Map<String, List<OrderedExtPattern>> patterns = new HashMap<>();

    @Override
    public List<OrderedExtPattern> select(String item) {
        return patterns.getOrDefault(item.toLowerCase(), Collections.emptyList());
    }

    @Override
    public void addPattern(OrderedExtPattern pattern) {
        String key = pattern.getPattern().ext().toLowerCase();
        int idx = key.lastIndexOf('.');
        if (idx >= 0) {
            key = key.substring(idx + 1);
        }
        List<OrderedExtPattern> patterns = this.patterns.computeIfAbsent(key, k -> new ArrayList<>());
        patterns.add(pattern);
    }
}
