package ppl.common.utils.argument;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

public interface Argument<K, V> {
    K getName();
    String toCanonicalString(V value);
    FeedingStream<V> stream();
    V merge(V v1, V v2);
    Builder<K, V> copy();
    interface Builder<K, V> {
        Builder<K, V> name(K name);
        Builder<K, V> split(Function<String, Stream<String>> splitter);
        <R> Builder<K, R> map(Function<V, R> mapper);
        Builder<K, V> collect();
        <A, R> Builder<K, R> collect(Collector<V, A, R> collector);
        Argument<K, V> build(BiFunction<K, V, String> toCanonicalString);
    }
}
