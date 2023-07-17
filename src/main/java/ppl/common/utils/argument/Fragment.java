package ppl.common.utils.argument;

import ppl.common.utils.string.kvpair.Pair;

import java.util.Objects;

public abstract class Fragment<K, V> {
    private final K key;
    private final V value;

    public Fragment(Pair<K, V> pair) {
        this.key = pair.getFirst();
        this.value = pair.getSecond();
    }

    public Fragment(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public String toString() {
        return merge(key, value);
    }

    /**
     * Get Canonical string. Given a Parser A, if a Fragment M is come from A then
     * when the method parse of A is called with the canonical string of M,
     * the parse method will return a Fragment which is equals to M.
     *
     * @return Canonical string.
     */
    protected abstract String merge(K key, V value);

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fragment<?, ?> fragment = (Fragment<?, ?>) o;
        return Objects.equals(key, fragment.key) && Objects.equals(value, fragment.value);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(key, value);
    }
}
