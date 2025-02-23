package ppl.common.utils.ext.parser;

import ppl.common.utils.Collections;
import ppl.common.utils.ext.Ext;
import ppl.common.utils.ext.Exts;
import ppl.common.utils.ext.selector.SelectorKind;
import ppl.common.utils.string.Strings;
import ppl.common.utils.string.trie.Trie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    /**
     * Return the {@code ppl.common.utils.ext.Ext} which is the longest extension name extracted from
     * the name against some name item in it. Parsing the name is going on from right to left.
     * Return the {@code ppl.common.utils.ext.Ext} matched first.
     *
     * @param name file name to be parsed
     * @return the {@code ppl.common.utils.ext.Ext}
     */
    @Override
    public Ext parse(String name) {
        String[] items = Arrays.stream(name.split(Pattern.quote("" + Exts.EXT_DELIMITER)))
                .filter(Strings::isNotBlank)
                .toArray(String[]::new);
        for (int i = items.length - 1; i >= 0; i--) {
            String item = items[i];
            List<ExtParser> parsers = SelectorKind.selectAllOrdered(trie, item);
            Ext matcher = null;
            int maxLength = 0;
            for (ExtParser parser : parsers) {
                Ext m = parser.parse(name);
                if (m != null) {
                    int mLength = m.length();
                    if (mLength > maxLength) {
                        matcher = m;
                        maxLength = mLength;
                    }
                }
            }

            if (matcher != null) {
                return matcher;
            }
        }
        return null;
    }

    public List<ExtParser> getParsers() {
        return trie.getAll().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
