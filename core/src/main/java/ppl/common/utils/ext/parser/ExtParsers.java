package ppl.common.utils.ext.parser;

import ppl.common.utils.Collections;
import ppl.common.utils.ext.ExtMatcher;
import ppl.common.utils.ext.selector.SelectorKind;
import ppl.common.utils.string.Strings;
import ppl.common.utils.string.trie.Trie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExtParsers implements ExtParser {

    private final Trie<List<ExtParser>> trie = new Trie<>();

    public void addParser(ExtParser parser) {
        String name = parser.name();
        List<ExtParser> list = trie.get(name, Collections.emptyList());
        if (list.isEmpty()) {
            list = new ArrayList<>();
            trie.put(name, list);
        }
        list.add(parser);
    }

    @Override
    public String name() {
        return "";
    }

    @Override
    public boolean isAccept(String item, SelectorKind selector) {
        return true;
    }

    @Override
    public ExtMatcher parse(String name) {
        String[] items = Arrays.stream(name.split("\\."))
                .filter(Strings::isNotBlank)
                .toArray(String[]::new);
        for (int i = items.length; i >= 0; i--) {
            String item = items[i];
            List<ExtParser> parsers = SelectorKind.selectAllOrdered(trie, item);

        }
        return null;
    }
}
