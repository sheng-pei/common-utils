package ppl.common.utils.string.substring.impl;

import ppl.common.utils.string.substring.PositionalArguments;
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
            throw new StringIndexOutOfBoundsException(start);
        }
        if (end > source.length) {
            throw new StringIndexOutOfBoundsException(end);
        }
        if (start > end) {
            throw new StringIndexOutOfBoundsException(end - start);
        }

        this.source = source;
        this.start = start;
        this.end = end;
    }

    @Override
    public void append(StringBuilder builder, PositionalArguments arguments) {
        Objects.requireNonNull(builder, "Builder is null.");
        Objects.requireNonNull(arguments, "Arguments is null.");
        if (!arguments.available()) {
            throw new IllegalArgumentException("Arguments is unavailable.");
        }

        append(builder, arguments.consume());
    }

    @Override
    public void append(StringBuilder builder, String argument) {
        Objects.requireNonNull(builder, "Builder is null.");
        Objects.requireNonNull(argument, "Argument is null.");
        builder.append(argument);
    }

    @Override
    public final String string() {
        return new String(this.source, start(), length());
    }

    @Override
    public final String string(int offset) {
        if (offset < 0 || offset > length()) {
            throw new SubstringIndexOutOfBoundsException(offset);
        }

        int start = this.start() + offset;
        return new String(this.source, start, this.end() - start);
    }

    @Override
    public final String string(int offset, int length) {
        if (offset < 0 || offset > length()) {
            throw new SubstringIndexOutOfBoundsException(offset);
        }

        if (length < 0 || offset + length > length()) {
            throw new SubstringIndexOutOfBoundsException(length);
        }

        int start = this.start() + offset;
        return new String(this.source, start, length);
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
