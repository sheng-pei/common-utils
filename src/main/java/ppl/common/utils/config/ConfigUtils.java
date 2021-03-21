package ppl.common.utils.config;

import java.util.*;
import java.util.function.Function;

public class ConfigUtils {

    public static <E> Set<E> toSet(Reader reader, Function<Reader, E> elementConverter) {
        Objects.requireNonNull(reader, "Reader must not be null.");
        Objects.requireNonNull(elementConverter, "Element converter must not be null.");
        Set<E> res = new HashSet<>();
        reader.iterator().forEachRemaining(r -> res.add(elementConverter.apply(r)));
        return res;
    }

    public static <E> List<E> toList(Reader reader, Function<Reader, E> elementConverter) {
        Objects.requireNonNull(reader, "Reader must not be null.");
        Objects.requireNonNull(elementConverter, "Element converter must not be null.");
        List<E> res = new ArrayList<>();
        reader.iterator().forEachRemaining(r -> res.add(elementConverter.apply(r)));
        return res;
    }

    public static <K, V> Map<K, V> toMap(Reader reader, Function<Reader, K> kConverter, Function<Reader, V> vConverter) {
        Objects.requireNonNull(reader, "Reader must not be null.");
        Objects.requireNonNull(kConverter, "Key converter must not be null.");
        Objects.requireNonNull(vConverter, "Value converter must not be null.");
        Map<K, V> res = new HashMap<>();
        reader.iterator().forEachRemaining(r -> {
            res.put(kConverter.apply(r), vConverter.apply(r));
        });
        return res;
    }

}