package ppl.common.utils.string.substring.impl;

import ppl.common.utils.string.substring.PositionalArguments;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

final class EscapableSubstring implements ppl.common.utils.string.substring.Substring {

    private final Substring substring;
    private final int firstUnescape;

    EscapableSubstring(char[] source, int start, int firstUnescape, int end) {
        this.substring = new Substring(source, start, end);
        if (firstUnescape < start || firstUnescape >= end) {
            throw new StringIndexOutOfBoundsException(firstUnescape);
        }
        this.firstUnescape = firstUnescape;
    }

    EscapableSubstring(Substring substring) {
        this.substring = substring;
        this.firstUnescape = substring.start();
    }

    @Override
    public void append(StringBuilder builder, PositionalArguments arguments) {
        Objects.requireNonNull(builder, "Builder is null.");
        Objects.requireNonNull(arguments, "Arguments is null.");
        if (!arguments.available()) {
            throw new IllegalArgumentException("Arguments is unavailable.");
        }

        append(builder, arguments::consume);
    }

    @Override
    public void append(StringBuilder builder, String argument) {
        Objects.requireNonNull(builder, "Builder is null.");

        append(builder, () -> argument);
    }

    private void append(StringBuilder builder, Supplier<String> supplier) {
        Objects.requireNonNull(builder, "Builder is null.");

        int lenEscape = lenEscape();
        int maintainEscape = lenEscape >> 1;
        if ((lenEscape & 1) == 1) {
            builder.append(this.substring.source, this.firstUnescape - maintainEscape, this.length() - 1 - maintainEscape);
        } else {
            builder.append(this.substring.source, this.substring.start(), maintainEscape);
            builder.append(supplier.get());
        }
    }

    @Override
    public boolean isEmpty() {
        return substring.isEmpty();
    }

    @Override
    public int start() {
        return substring.start();
    }

    @Override
    public int end() {
        return substring.end();
    }

    @Override
    public int length() {
        return substring.length();
    }

    @Override
    public String string() {
        return substring.string();
    }

    @Override
    public String string(int offset) {
        return substring.string();
    }

    @Override
    public String string(int offset, int length) {
        return substring.string();
    }

    private int lenEscape() {
        return this.firstUnescape - this.start();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EscapableSubstring that = (EscapableSubstring) o;
        return firstUnescape == that.firstUnescape && Objects.equals(substring, that.substring);
    }

    @Override
    public int hashCode() {
        return Objects.hash(substring, firstUnescape);
    }
}
