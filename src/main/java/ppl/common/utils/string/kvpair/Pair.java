package ppl.common.utils.string.kvpair;

import java.util.Objects;
import java.util.function.BiPredicate;

public class Pair<F, S> {
    @SuppressWarnings("rawtypes")
    private static final Pair INVALID = new Pair() {
        @Override
        public Object getFirst() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getSecond() {
            throw new UnsupportedOperationException();
        }
    };

    private final F first;
    private final S second;

    private Pair() {
        this.first = null;
        this.second = null;
    }

    private Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public boolean isValid() {
        return this != INVALID;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        @SuppressWarnings("rawtypes")
        Pair pair = (Pair) o;
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    public static <F, S> Pair<F, S> create(F first, S second) {
        return create(first, second, (f, s) -> true);
    }

    public static <F, S> Pair<F, S> create(F first, S second, BiPredicate<F, S> checker) {
        if (!checker.test(first, second)) {
            @SuppressWarnings("unchecked")
            Pair<F, S> res = (Pair<F, S>) INVALID;
            return res;
        }
        return new Pair<>(first, second);
    }

}
