package ppl.common.utils;

import java.util.Objects;

public final class Substring {

    public static final Substring EMPTY_SUBSTRING = new Substring();

    private final char[] source;
    private final int start;
    private final int end;

    private String _substring;

    private Substring() {
        this("");
    }

    public Substring(String source) {
        this(source, 0, source.length());
    }

    public Substring(String source, int start) {
        this(source, start, source.length());
    }

    public Substring(String source, int start, int end) {
        this(source != null ? source.toCharArray() : null, start, end);
    }

    public Substring(char[] source, int start, int end) {
        Objects.requireNonNull(source, "Source is null");
        this.checkOutOfBounds(start, end, source.length);
        this.source = source;
        this.start = start;
        this.end = end;
    }

    private void checkOutOfBounds(int start, int end, int len) {
        if (start < 0 || end > len) {
            throw new StringIndexOutOfBoundsException(StringUtils.format("out of bound: ({}, {})", start, end));
        }
    }

    public char[] getSource() {
        return source;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int length() {
        return end - start;
    }

    public boolean isEmpty() {
        return end <= start;
    }

    @Override
    public String toString() {
        String res = this._substring;
        if (res == null) {
            res = String.valueOf(this.source, this.start, this.end - this.start);
            this._substring = res;
        }
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Substring substring = (Substring) o;
        return this.toString().equals(substring.toString());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

}
