package ppl.common.utils.string.substring.impl;

import ppl.common.utils.string.substring.Substring;
import ppl.common.utils.string.substring.SubstringFinder;

import java.util.Objects;

abstract class AbstractSubstringFinder implements SubstringFinder {

    public Substring find(String input) {
        Objects.requireNonNull(input, "The input is null.");
        return findInternal(input.toCharArray(), 0, input.length());
    }

    public Substring find(String input, int start) {
        Objects.requireNonNull(input, "The input is null.");
        if (start < 0 || start > input.length()) {
            throw new StringIndexOutOfBoundsException(start);
        }
        return findInternal(input.toCharArray(), start, input.length());
    }

    public Substring find(String input, int start, int end) {
        Objects.requireNonNull(input, "The input is null.");
        if (start < 0 || start > input.length()) {
            throw new StringIndexOutOfBoundsException(start);
        }
        if (end < 0 || end > input.length()) {
            throw new StringIndexOutOfBoundsException(end);
        }
        return findInternal(input.toCharArray(), start, end);
    }

    @Override
    public Substring find(char[] input) {
        Objects.requireNonNull(input, "The input is null.");
        return findInternal(input, 0, input.length);
    }

    @Override
    public Substring find(char[] input, int start) {
        Objects.requireNonNull(input, "The input is null.");
        if (start < 0 || start > input.length) {
            throw new ArrayIndexOutOfBoundsException(start);
        }
        return findInternal(input, start, input.length);
    }

    public Substring find(char[] input, int start, int end) {
        Objects.requireNonNull(input, "The input is null.");
        if (start < 0 || start > input.length) {
            throw new ArrayIndexOutOfBoundsException(start);
        }
        if (end < 0 || end > input.length) {
            throw new ArrayIndexOutOfBoundsException(end);
        }
        return findInternal(input, start, end);
    }

    protected abstract Substring findInternal(char[] input, int start, int end);

}
