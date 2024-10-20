package ppl.common.utils.ext;

import java.util.*;

class PrefixExtSelector implements ExtSelector {

    private final Map<Integer, List<OrderedExtPattern>> patterns = new HashMap<>();

    @Override
    public List<OrderedExtPattern> select(String item) {
        if (patterns.isEmpty()) {
            return Collections.emptyList();
        }

        List<OrderedExtPattern> ret = new ArrayList<>();
        char[] chars = item.toLowerCase().toCharArray();
        int hash = 0;
        for (char c : chars) {
            hash = hashCode(hash, c);
            ret.addAll(patterns.getOrDefault(hash, Collections.emptyList()));
        }
        return ret;
    }

    private int hashCode(String s) {
        int hash = 0;
        char[] chars = s.toCharArray();
        for (char c : chars) {
            hash = hashCode(hash, c);
        }
        return hash;
    }

    private int hashCode(int result, char c) {
        return 31 * result + Character.hashCode(c);
    }

    @Override
    public void addPattern(OrderedExtPattern pattern) {
        String key = pattern.getPattern().ext().toLowerCase();
        int idx = key.lastIndexOf('.');
        if (idx >= 0) {
            key = key.substring(idx + 1);
        }

        List<OrderedExtPattern> patterns = this.patterns.computeIfAbsent(hashCode(key), k -> new ArrayList<>());
        patterns.add(pattern);
    }
}
