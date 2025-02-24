package ppl.common.utils.ext.selector;

import ppl.common.utils.ext.parser.ExtParser;
import ppl.common.utils.string.trie.Trie;

import java.util.List;
import java.util.Set;

public interface Selector {
    String key(String item);
    Set<ExtParser> select(Trie<List<ExtParser>> trie, String item);
}
