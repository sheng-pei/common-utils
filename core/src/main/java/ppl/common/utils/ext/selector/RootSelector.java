package ppl.common.utils.ext.selector;

import ppl.common.utils.ext.parser.ExtParser;
import ppl.common.utils.ext.parser.ExtPattern;
import ppl.common.utils.string.trie.Trie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class RootSelector implements Selector {
    @Override
    public String key(String item) {
        return "";
    }

    @Override
    public List<ExtParser> select(Trie<List<ExtParser>> trie, String item) {
        Trie<List<ExtParser>>.Searcher searcher = trie.searcher(Collections.emptyList());
        List<ExtParser> patterns = new ArrayList<>();
        searcher.current().stream()
                .filter(p -> p.isAccept(item, SelectorKind.ROOT))
                .forEach(patterns::add);
        return patterns;
    }
}
