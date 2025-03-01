package ppl.common.utils.argument.parser;

import ppl.common.utils.pair.Pair;

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

    public final K getKey() {
        return key;
    }

    public final V getValue() {
        return value;
    }

    public final String toString() {
        return join(key, value);
    }

    /**
     * Get Canonical string. Given a Parser A, if a Fragment M is come from A then
     * when the method parse of A is called with the canonical string of M,
     * the parse method will return a Fragment which is equals to M.
     *
     * @return Canonical string.
     */
    protected abstract String join(K key, V value);

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
