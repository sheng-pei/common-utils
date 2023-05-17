package ppl.common.utils.string.substring;

import java.util.Arrays;
import java.util.Objects;

/**
 * An object that is a subsequence of a string.
 *
 * <p>This substring begins at {@link #start() startIndex}, inclusive, and extends to {@link #end() endIndex},
 * exclusive, of the source string. The {@link #start() startIndex} must not be greater than the {@link #end()
 * endIndex}.</p>
 *
 * <p>The {@link #length() length} of this substring is the number of the characters of the subsequence. If
 * there is no character in this substring then this is empty.</p>
 *
 */
public class Substring {

    final char[] source;
    final int start;
    final int end;

    public Substring(char[] source, int start, int end) {
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

    public final char charAt(int idx) {
        if (idx < 0 || idx >= length()) {
            throw new SubstringIndexOutOfBoundsException(idx);
        }

        return source[start + idx];
    }

    public final String string() {
        return unsafeString(0, length());
    }

    public final String string(int offset) {
        if (offset < 0 || offset > length()) {
            throw new SubstringIndexOutOfBoundsException(offset);
        }

        return unsafeString(offset, length() - offset);
    }

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

    public final int start() {
        return start;
    }

    public final int end() {
        return end;
    }

    public final boolean isEmpty() {
        return this.start() == this.end();
    }

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
