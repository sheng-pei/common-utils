package ppl.common.utils.cache;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * A semi-persistent mapping from keys to values. Cache entries are manually added using
 * {@link #get(K, Callable)} or {@link #putIfAbsent(K, V)}, and are stored in the cache
 * until evicted or manually invalidated.
 *
 * <p><b>Suggest:</b> For any given key, the loader used for the call {@link #get(K,
 * Callable loader)} return the same value -- judged by {@link Object#equals(Object)}
 * every time it is executed. The execute result of the mapper function used for the call
 * {@link #get(K, Function mapperFunction)} must be the same when it is passed in the same
 * key.
 *
 * <p>Implementations of this interface are expected to be thread-safe, and can be safely accessed
 * by multiple concurrent threads.
 *
 * <p>No observable state associated with this cache is modified until loading or putting complete.
 * @param <K>
 * @param <V>
 */
public interface Cache<K, V> {
    V get(K key, Callable<? extends V> loader) throws ExecutionException;
    V get(K key, Function<K, ? extends V> mapperFunction) throws ExecutionException;
    V getIfPresent(K key);
    V putIfAbsent(K key, V value);
    void invalid(K key);
}
