//package ppl.common.utils.ext.pattern.selector;
//
//import ppl.common.utils.Collections;
//import ppl.common.utils.ext.ExtMatcher;
//import ppl.common.utils.ext.pattern.ExtPattern;
//import ppl.common.utils.string.trie.Trie;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class BaseSelector implements Selector {
//
//    private final Trie<List<ExtPattern>> trie = new Trie<>();
//
//    @Override
//    public void addPattern(ExtPattern pattern) {
//
//    }
//
//    @Override
//    public List<ExtPattern> getPatterns() {
//        return Collections.emptyList();
//    }
//
//    @Override
//    public ExtMatcher parse(String name) {
//        Trie<List<ExtPattern>> trie;
//        String item;
//
//        char[] chars = item.chars();
//        Trie<List<ExtPattern>>.Searcher searcher = trie.searcher(Collections.emptyList());
//        List<ExtPattern> patterns = new ArrayList<>();
//        searcher.current().stream()
//                .filter(p -> p.isAccept(item))
//                .forEach(patterns::add);
//        for (char c : chars) {
//            searcher.next(c);
//            searcher.current().stream()
//                    .filter(p -> p.isAccept(item))
//                    .forEach(patterns::add);
//        }
//
//        return null;
//    }
//}
