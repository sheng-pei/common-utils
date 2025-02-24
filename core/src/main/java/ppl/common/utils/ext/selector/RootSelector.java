package ppl.common.utils.ext.selector;

import ppl.common.utils.ext.parser.ExtParser;
import ppl.common.utils.string.trie.Trie;

import java.util.*;

class RootSelector implements Selector {
    @Override
    public String key(String item) {
        return "";
    }

    @Override
    public Set<ExtParser> select(Trie<List<ExtParser>> trie, String item) {
        Trie<List<ExtParser>>.Searcher searcher = trie.searcher(Collections.emptyList());
        Set<ExtParser> patterns = new HashSet<>();
        searcher.current().stream()
                .filter(p -> p.isAccept(item, SelectorKind.ROOT))
                .forEach(patterns::add);
        return patterns;
    }
}
