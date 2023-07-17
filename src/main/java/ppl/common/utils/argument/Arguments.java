package ppl.common.utils.argument;

public interface Arguments<K, S> {
    AbstractArgument<K, Object> get(S s);
    AbstractArgument<K, Object> getByName(K name);
}
