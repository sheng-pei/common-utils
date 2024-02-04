package ppl.common.utils.cache;

public interface Cache<K, V> {
    V get(K k);
    void put(K k, V v);
}
