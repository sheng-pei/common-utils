package ppl.common.utils.order;

import java.util.Comparator;
import java.util.Objects;

public class ComparatorRange<T> implements Range<T> {
    private final T start;
    private final T end;
    private final Comparator<T> comparator;

    public ComparatorRange(T start, T end, Comparator<T> comparator) {
        Objects.requireNonNull(comparator);
        this.start = start;
        this.end = end;
        this.comparator = comparator;
    }

    @Override
    public T getStart() {
        return start;
    }

    @Override
    public T getEnd() {
        return end;
    }

    @Override
    public boolean isEmpty() {
        return comparator.compare(end, start) <= 0;
    }

    @Override
    public boolean contains(T value) {
        return Condition.in(value, start, end, comparator);
    }

    @Override
    public Range<T> shrinkEnd(T end) {
        if (end == null) {
            return this;
        }

        T min = end;
        if (comparator.compare(min, this.end) > 0) {
            min = this.end;
        }
        return new ComparatorRange<>(start, min, comparator);
    }

    @Override
    public String toString() {
        return String.format("range [%s, %s]", start, end);
    }
}
