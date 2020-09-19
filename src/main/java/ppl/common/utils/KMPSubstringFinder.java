package ppl.common.utils;

import java.util.Arrays;

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
        System.out.println(Arrays.toString(this.next));
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
        int matchedIdx = this.match(input.toCharArray(), start, end);
        if (matchedIdx != -1) {
            return new Substring(input, matchedIdx, matchedIdx + this.pattern.length);
        }
        return null;
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
