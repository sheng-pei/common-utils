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
        this.next = new int[this.pattern.length];
        this.next[0] = -1;
        fillNext();
    }

    public static void main(String[] args) {
        String pattern = "ppppapppp";
        KMPSubstringFinder finder = new KMPSubstringFinder(pattern);
    }

    private void fillNext() {
        int[] len = new int[this.pattern.length];

        for (int pos = 1; pos < this.pattern.length; pos++) {
            calc(len, pos);
            if (this.pattern[pos] == this.pattern[len[pos]]) {
                this.next[pos] = this.next[len[pos]];
            } else {
                this.next[pos] = len[pos];
            }
        }

        System.out.println(Arrays.toString(this.next));

    }

    private void calc(int[] l, int len) {
        int p = len - 1;
        while (p != 0) {
            p = l[p];
            if (this.pattern[len - 1] == this.pattern[p]) {
                l[len] = p + 1;
                return;
            }
        }
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

        int patternPos = start;
        int consumed = start;
        while (consumed < end) {
            int prefixLen = consumed - patternPos;

            if (prefixLen == pattern.length) {
                return patternPos;
            } else if (pattern[prefixLen] != input[consumed]) {
                patternPos = consumed - next[prefixLen];
            }

            if (pattern[prefixLen] == input[consumed] || prefixLen == 0) {
                consumed++;
            }
        }

        if (patternPos + pattern.length == end) {
            return patternPos;
        }
        return -1;
    }

}
