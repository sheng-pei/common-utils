package ppl.common.utils.command.argument;

public interface Mapper<V, R> {
    R map(V v);
}
