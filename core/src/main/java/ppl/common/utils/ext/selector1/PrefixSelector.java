//package ppl.common.utils.ext.pattern.selector;
//
//import ppl.common.utils.ext.ExtMatcher;
//
//public class PrefixSelector extends AbstractSelector {
//
////    @Override
////    public List<ExtPattern> select(String item) {
////        if (patterns.isEmpty()) {
////            return Collections.emptyList();
////        }
////
////        List<ExtPattern> ret = new ArrayList<>();
////        char[] chars = item.toLowerCase().toCharArray();
////        int hash = 0;
////        for (char c : chars) {
////            hash = hashCode(hash, c);
////            ret.addAll(patterns.getOrDefault(hash, Collections.emptyList()));
////        }
////        return ret;
////    }
//
//    @Override
//    public ExtMatcher parse(String name) {
//        return null;
//    }
//
//    @Override
//    protected Object key(String field) {
//        return hashCode(field);
//    }
//
//    private int hashCode(String s) {
//        int hash = 0;
//        char[] chars = s.toCharArray();
//        for (char c : chars) {
//            hash = hashCode(hash, c);
//        }
//        return hash;
//    }
//
//    private int hashCode(int result, char c) {
//        return 31 * result + Character.hashCode(c);
//    }
//}
