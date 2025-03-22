package ppl.common.utils.cache;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * No hard reachable value will be evicted from cache unless manually invalidated.
 * For any given key, values returned by any call {@link #get(K, Callable)}, {@link
 * #putIfAbsent(K, V)} or {@link #getIfPresent(K)} between two adjacent evicting
 * are the same, judged by '=='.
 */
public class ConcurrentReferenceValueCache<K, V> implements Cache<K, V> {

    private final ReferenceType type;
    private final ReferenceQueue<Object> queue = new ReferenceQueue<>();
    private final Map<K, IdentityValue> cache = new ConcurrentHashMap<>();

    public ConcurrentReferenceValueCache() {
        this.type = ReferenceType.WEAK;
    }

    public ConcurrentReferenceValueCache(ReferenceType type) {
        this.type = type;
    }

    @Override
    public V get(K key, Callable<? extends V> loader) throws ExecutionException {
        Objects.requireNonNull(loader, "Loader is required.");
        try {
            return computeIfAbsent(key, k -> new IdentityValue(new SingletonSupplier(loader)));
        } catch (InternalLoaderException e) {
            throw new ExecutionException(e);
        }
    }

    @Override
    public V get(K key, Function<K, ? extends V> mapperFunction) throws ExecutionException {
        Objects.requireNonNull(mapperFunction, "MapperFunction is required.");
        return get(key, () -> mapperFunction.apply(key));
    }

    @Override
    public V getIfPresent(K key) {
        expungeStaleEntries();
        IdentityValue wv = cache.get(key);
        if (wv == null || wv.get() == null) {
            return null;
        }

        Object value = wv.get();
        if (value instanceof SingletonSupplier) {
            return singleton(key, wv, (SingletonSupplier) value);
        } else {
            @SuppressWarnings("unchecked")
            Reference<Object> ref = (Reference<Object>) value;
            Object referent = ref.get();
            if (referent != null && !(referent instanceof NullValue)) {
                @SuppressWarnings("unchecked")
                V v = (V) referent;
                return v;
            } else {
                return null;
            }
        }
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return computeIfAbsent(key, loader(value == null ? new NullValue() : value));
    }

    @Override
    public void invalid(K key) {
        expungeStaleEntries();
        cache.remove(key);
    }

    private Function<K, IdentityValue> loader(Object value) {
        Objects.requireNonNull(value);
        return k -> new IdentityValue(reference(k, value));
    }

    private V computeIfAbsent(K key, Function<K, IdentityValue> func) {
        while (true) {
            expungeStaleEntries();
            IdentityValue wv = cache.computeIfAbsent(key, func);
            Object value = wv.get();
            if (value instanceof SingletonSupplier) {
                return singleton(key, wv, (SingletonSupplier) value);
            } else {
                @SuppressWarnings("unchecked")
                Reference<Object> ref = (Reference<Object>) value;
                Object referent = ref.get();
                if (referent != null) {
                    if (referent instanceof NullValue) {
                        return null;
                    } else {
                        @SuppressWarnings("unchecked")
                        V v = (V) referent;
                        return v;
                    }
                }
            }
        }
    }

    private V singleton(K key, IdentityValue oldValue, SingletonSupplier supplier) {
        @SuppressWarnings("unchecked")
        V ret = (V) supplier.get();
        Object referent = (ret == null ? new NullValue() : ret);
        cache.replace(key, oldValue, new IdentityValue(reference(key, referent)));
        return ret;
    }

    private Reference<Object> reference(K key, Object referent) {
        if (type == ReferenceType.WEAK) {
            return new WeakValue<>(key, referent, queue);
        } else {
            return new SoftValue<>(key, referent, queue);
        }
    }

    private void expungeStaleEntries() {
        for (Reference<?> x; (x = queue.poll()) != null; ) {
            removeReference(x);
        }
    }

    private void removeReference(Reference<?> x) {
        @SuppressWarnings("unchecked")
        Key<K> wv = (Key<K>) x;
        cache.remove(wv.key(), new IdentityValue(wv));
    }

    private static class InternalLoaderException extends RuntimeException {
        public InternalLoaderException(Throwable cause) {
            super(cause);
        }
    }

    private static class IdentityValue {
        private final Object value;

        public IdentityValue(Object value) {
            Objects.requireNonNull(value);
            this.value = value;
        }

        public Object get() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IdentityValue that = (IdentityValue) o;
            return value == that.value;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(value);
        }
    }

    private static class WeakValue<K, V> extends WeakReference<Object> implements Key<K> {
        private final K k;

        public WeakValue(K k, V referent, ReferenceQueue<Object> q) {
            super(referent, q);
            this.k = k;
        }

        @Override
        public K key() {
            return k;
        }
    }

    private static class SoftValue<K, V> extends SoftReference<Object> implements Key<K> {
        private final K k;

        public SoftValue(K k, V referent, ReferenceQueue<Object> q) {
            super(referent, q);
            this.k = k;
        }

        @Override
        public K key() {
            return k;
        }
    }

    private interface Key<K> {
        K key();
    }

    /**
     * Solve a problem caused by nested call {@link ConcurrentHashMap#computeIfAbsent(Object, Function)}
     * if loaders use this cache.
     */
    private static final class SingletonSupplier {
        private static final Object NULL_OBJECT = new Object();
        private volatile Object t;
        @SuppressWarnings("rawtypes")
        private final Callable loader;

        public SingletonSupplier(Callable<?> loader) {
            this.loader = loader;
        }

        public synchronized Object get() {
            Object temp = t;
            if (temp == null) {
                try {
                    temp = loader.call();
                    if (temp == null) {
                        temp = NULL_OBJECT;
                    }
                } catch (Exception e) {
                    throw new InternalLoaderException(e);
                }
                this.t = temp;
            }
            return temp == NULL_OBJECT ? null : temp;
        }
    }

    private static final class NullValue {
        public Object get() {
            return null;
        }
    }

}
