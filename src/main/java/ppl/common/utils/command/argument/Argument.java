package ppl.common.utils.command.argument;

import java.util.Optional;

public interface Argument<V> {
    String getName();

    void receive();

    void receive(String value);

    boolean received();

    Optional<V> resolve();

    interface Builder<V> {
        Builder<V> split(Splitter splitter);
        <R> Builder<R> map(Mapper<V, R> mapper);
        Argument<V> collect();
        <R> Argument<R> collect(Collector<V, R> collector);
    }

}
