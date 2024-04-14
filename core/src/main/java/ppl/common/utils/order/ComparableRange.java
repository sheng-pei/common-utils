package ppl.common.utils.order;

public class ComparableRange<T extends Comparable<T>> implements Range<T> {

    private final T start;
    private final T end;

    public ComparableRange(T start, T end) {
        this.start = start;
        this.end = end;
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
        if (start == null || end == null) {
            return false;
        }
        return end.compareTo(start) <= 0;
    }

    @Override
    public boolean contains(T value) {
        if (start == null && end == null) {
            return true;
        }
        if (start == null) {
            return value.compareTo(end) <= 0;
        }
        if (end == null) {
            return value.compareTo(start) >= 0;
        }
        return Condition.in(value, start, end);
    }

    @Override
    public Range<T> shrinkEnd(T end) {
        if (end == null) {
            return this;
        }
        T min = end;
        if (this.end != null && min.compareTo(this.end) > 0) {
            min = this.end;
        }
        return new ComparableRange<>(start, min);
    }

    @Override
    public String toString() {
        return String.format("range [%s, %s]",
                start == null ? "" : start,
                end == null ? "" : end);
    }
}
