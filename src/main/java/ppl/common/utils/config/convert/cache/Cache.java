package ppl.common.utils.config.convert.cache;

import ppl.common.utils.Private;

@Private("ppl.common.utils.config.convert")
public interface Cache<K, V> {
    V get(K k);
    void put(K k, V v);
}
