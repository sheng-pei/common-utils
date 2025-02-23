//package ppl.common.utils.ext.pattern.selector;
//
//import ppl.common.utils.ext.Exts;
//import ppl.common.utils.ext.pattern.ExtPattern;
//import ppl.common.utils.string.Strings;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//public abstract class AbstractSelector implements Selector {
//
//    private final Map<Object, List<ExtPattern>> patterns = new HashMap<>();
//
//    @Override
//    public void addPattern(ExtPattern pattern) {
//        String lowerExt = pattern.name().toLowerCase();
//        String[] fields = Strings.split(lowerExt, String.valueOf(Exts.EXT_DELIMITER));
//        for (String field : fields) {
//            this.patterns.computeIfAbsent(key(field), k -> new ArrayList<>())
//                    .add(pattern);
//        }
//    }
//
//    protected Object key(String field) {
//        return field;
//    }
//
//    @Override
//    public List<ExtPattern> getPatterns() {
//        return patterns.values().stream()
//                .flatMap(Collection::stream)
//                .collect(Collectors.toList());
//    }
//}
