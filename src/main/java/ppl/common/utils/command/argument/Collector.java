package ppl.common.utils.command.argument;

public interface Collector<V, R> {
    R collect(V v);
}
