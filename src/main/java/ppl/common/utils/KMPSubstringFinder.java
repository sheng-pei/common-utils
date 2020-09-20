package ppl.common.utils;

import java.util.Arrays;
import java.util.Objects;

public class KMPSubstringFinder implements SubstringFinder {

    private final char[] pattern;
    private final int[] next;

    public KMPSubstringFinder(String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            throw new IllegalArgumentException(StringUtils.format("Pattern string is empty or null"));
        }

        this.pattern = pattern.toCharArray();
        this.next = new int[this.pattern.length + 1];
        this.next[0] = -1;
        fillNext();
    }

    private void fillNext() {
        int pos = 1;
        int cnd = 0;
        while (pos < this.pattern.length) {
            if (this.pattern[pos] == this.pattern[cnd]) {
                this.next[pos] = this.next[cnd];
            } else {
                this.next[pos] = cnd;
                cnd = this.next[cnd];
                while (cnd >= 0 && this.pattern[pos] != this.pattern[cnd]) {
                    cnd = this.next[cnd];
                }
            }
            pos = pos + 1;
            cnd = cnd + 1;
        }
        this.next[pos] = cnd;
    }

    @Override
    public String getPattern() {
        return String.valueOf(pattern);
    }

    @Override
    public Substring find(String input) {
        return this.find(input, 0, input.length());
    }

    @Override
    public Substring find(String input, int start) {
        return this.find(input, start, input.length());
    }

    @Override
    public Substring find(String input, int start, int end) {
        return this.find(input != null ? input.toCharArray() : null, start, end);
    }

    @Override
    public Substring find(char[] input, int start, int end) {
        Objects.requireNonNull(input, "The string to be checked could not be null");
        if (start < 0 || start > end || end > input.length) {
            throw new IndexOutOfBoundsException("Start: " + start + " and end: " + end + " must be in [0, " + input.length + ")");
        }

        int matchedIdx = this.match(input, start, end);
        if (matchedIdx != -1) {
            return new Substring(input, matchedIdx, matchedIdx + this.pattern.length);
        }
        return Substring.EMPTY_SUBSTRING;
    }

    private int match(char[] input, int start, int end) {

        if (end - start < pattern.length) {
            return -1;
        }

        int next = 0;
        int consumed = start;
        while (consumed < end) {

            if (next == pattern.length) {
                return consumed - pattern.length;
            }

            if (pattern[next] != input[consumed]) {
                next = this.next[next];
                if (next == -1) {
                    consumed++;
                    next++;
                }
            } else {
                consumed++;
                next++;
            }
        }

        return next == pattern.length ? consumed - pattern.length : -1;
    }

}
