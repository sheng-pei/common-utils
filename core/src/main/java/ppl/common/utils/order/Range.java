package ppl.common.utils.order;

import java.util.Comparator;

public interface Range<T> {
    T getStart();

    T getEnd();

    boolean isEmpty();

    boolean contains(T value);

    Range<T> shrinkEnd(T end);

    static <T> Range<T> create(T begin, T end, Comparator<T> comparator) {
        return new ComparatorRange<>(begin, end, comparator);
    }

    static <T extends Comparable<T>> Range<T> create(T begin, T end) {
        return new ComparableRange<>(begin, end);
    }
}
