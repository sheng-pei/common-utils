package ppl.common.utils.string.substring.impl;

import ppl.common.utils.string.substring.SubstringIndexOutOfBoundsException;

import java.util.Arrays;
import java.util.Objects;

public class Substring implements ppl.common.utils.string.substring.Substring {

    final char[] source;
    final int start;
    final int end;

    Substring(char[] source, int start, int end) {
        Objects.requireNonNull(source, "Source is null");
        if (start < 0) {
            throw new ArrayIndexOutOfBoundsException(start);
        }
        if (end > source.length) {
            throw new ArrayIndexOutOfBoundsException(end);
        }
        if (start > end) {
            throw new ArrayIndexOutOfBoundsException(
                    "It is not allowed to give a start that is greater than an end.");
        }

        this.source = source;
        this.start = start;
        this.end = end;
    }

    @Override
    public final String string() {
        return unsafeString(0, length());
    }

    @Override
    public final String string(int offset) {
        if (offset < 0 || offset > length()) {
            throw new SubstringIndexOutOfBoundsException(offset);
        }

        return unsafeString(offset, length() - offset);
    }

    @Override
    public final String string(int offset, int length) {
        if (offset < 0 || offset > length()) {
            throw new SubstringIndexOutOfBoundsException(offset);
        }

        if (length < 0 || offset + length > length()) {
            throw new SubstringIndexOutOfBoundsException(
                    "It is not allowed to give a length that is greater than " +
                            "the number of characters beginning at the given offset " +
                            "and extending to the end.");
        }

        return unsafeString(offset, length);
    }

    private String unsafeString(int offset, int length) {
        return new String(this.source, this.start + offset, length);
    }

    @Override
    public final int start() {
        return start;
    }

    @Override
    public final int end() {
        return end;
    }

    @Override
    public final boolean isEmpty() {
        return this.start() == this.end();
    }

    @Override
    public final int length() {
        return this.end() - this.start();
    }

    @Override
    public final String toString() {
        return string();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Substring substring = (Substring) o;
        return start == substring.start && end == substring.end && Arrays.equals(source, substring.source);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(start, end);
        result = 31 * result + Arrays.hashCode(source);
        return result;
    }
}
