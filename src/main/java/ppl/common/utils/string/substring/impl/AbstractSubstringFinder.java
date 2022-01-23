package ppl.common.utils.string.substring.impl;

import ppl.common.utils.string.substring.Substring;
import ppl.common.utils.string.substring.SubstringFinder;

import java.util.Objects;

abstract class AbstractSubstringFinder implements SubstringFinder {

    public Substring find(String input) {
        return this.find(input, 0, input.length());
    }

    public Substring find(String input, int start) {
        return this.find(input, start, input.length());
    }

    public Substring find(String input, int start, int end) {
        return this.find(input != null ? input.toCharArray() : null, start, end);
    }

    public Substring find(char[] input, int start, int end) {
        Objects.requireNonNull(input, "The input is null.");
        if (start < 0) {
            throw new StringIndexOutOfBoundsException(start);
        }
        if (end > input.length) {
            throw new StringIndexOutOfBoundsException(end);
        }
        if (start > end) {
            throw new StringIndexOutOfBoundsException(end - start);
        }
        return findInternal(input, start, end);
    }

    protected abstract Substring findInternal(char[] input, int start, int end);

}
