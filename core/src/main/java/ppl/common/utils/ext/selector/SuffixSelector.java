package ppl.common.utils.ext.selector;

import ppl.common.utils.ext.parser.ExtParser;
import ppl.common.utils.string.Strings;
import ppl.common.utils.string.trie.Trie;

import java.util.*;

class SuffixSelector implements Selector {
    @Override
    public String key(String item) {
        return Strings.reverse(item);
    }

    @Override
    public Set<ExtParser> select(Trie<List<ExtParser>> trie, String item) {
        char[] chars = key(item).toCharArray();
        Trie<List<ExtParser>>.Searcher searcher = trie.searcher(Collections.emptyList());
        Set<ExtParser> patterns = new HashSet<>();
        for (char c : chars) {
            if (!searcher.hasNext()) {
                break;
            }

            searcher.next(c);
            searcher.current().stream()
                    .filter(p -> p.isAccept(item, SelectorKind.SUFFIX))
                    .forEach(patterns::add);
        }
        return patterns;
    }
}
