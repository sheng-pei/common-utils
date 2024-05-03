package ppl.common.utils.cache;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * A semi-persistent mapping from keys to values. Cache entries are manually added using
 * {@link #get(K, Callable)} or {@link #putIfAbsent(K, V)}, and are stored in the cache
 * until evicted or manually invalidated.
 *
 * <p><b>Suggest:</b> For any given key, every value computed by loader used for the call
 * {@link #get(K, Callable loader)} or used for the call {@link #putIfAbsent(K, V)} should
 * the same value -- judged by {@link Object#equals(Object)}. For example, a call by an
 * unprivileged user may return a resource accessible only to a privileged user making a
 * similar call.
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
    V getIfPresent(K key);
    V putIfAbsent(K key, V value);
    void invalid(K key);
}
