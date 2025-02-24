package ppl.common.utils.ext.selector;

import ppl.common.utils.enumerate.EnumEncoder;
import ppl.common.utils.ext.parser.ExtParser;
import ppl.common.utils.ext.parser.Ordered;
import ppl.common.utils.string.trie.Trie;

import java.util.*;
import java.util.stream.Collectors;

public enum SelectorKind implements Selector {
    ROOT('r', new RootSelector()),
    PREFIX('p', new PrefixSelector()),
    SUFFIX('s', new SuffixSelector());

    private final char flag;
    private final Selector selector;

    SelectorKind(char flag, Selector selector) {
        this.flag = flag;
        this.selector = selector;
    }

    @EnumEncoder
    public char getFlag() {
        return flag;
    }

    @Override
    public String key(String item) {
        return selector.key(item);
    }

    @Override
    public Set<ExtParser> select(Trie<List<ExtParser>> trie, String item) {
        return selector.select(trie, item);
    }

    public static List<ExtParser> selectAllOrdered(Trie<List<ExtParser>> trie, String item) {
        Set<ExtParser> ret = new HashSet<>();
        ret.addAll(ROOT.select(trie, item));
        ret.addAll(PREFIX.select(trie, item));
        ret.addAll(SUFFIX.select(trie, item));
        return ret.stream()
                .sorted((p1, p2) -> {
                    if (!(p1 instanceof Ordered) && !(p2 instanceof Ordered)) {
                        return 0;
                    }
                    if (p1 instanceof Ordered && !(p2 instanceof Ordered)) {
                        return -1;
                    }
                    if (!(p1 instanceof Ordered)) {
                        return 1;
                    }
                    return Comparator.comparingInt(Ordered::getOrder)
                            .compare((Ordered) p1, (Ordered) p2);
                })
                .collect(Collectors.toList());
    }
}
