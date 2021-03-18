package ppl.common.utils.config;

import java.util.*;
import java.util.function.Function;

public class ConfigUtils {

    public static <E> Set<E> toSet(Reader reader, Function<Reader, E> convert) {
        Set<E> res = new HashSet<>();
        reader.iterator().forEachRemaining(r -> res.add(convert.apply(r)));
        return res;
    }

    public static <E> List<E> toList(Reader reader, Function<Reader, E> convert) {
        List<E> res = new ArrayList<>();
        reader.iterator().forEachRemaining(r -> res.add(convert.apply(r)));
        return res;
    }

    public static <K, V> Map<K, V> toMap(Reader reader, Function<Reader, K> kConvert, Function<Reader, V> vConvert) {
        Map<K, V> res = new HashMap<>();
        reader.iterator().forEachRemaining(r -> {
            res.put(kConvert.apply(r), vConvert.apply(r));
        });
        return res;
    }

}
