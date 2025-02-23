package ppl.common.utils.ext.selector;

import ppl.common.utils.ext.parser.ExtParser;
import ppl.common.utils.string.trie.Trie;

import java.util.List;

public interface Selector {
    String key(String item);
    List<ExtParser> select(Trie<List<ExtParser>> trie, String item);
}
