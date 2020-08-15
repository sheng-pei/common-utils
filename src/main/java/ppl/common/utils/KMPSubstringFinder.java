package ppl.common.utils;

public class KMPSubstringFinder implements SubstringFinder {

    private final char[] pattern;
    private final int[] next;

    public KMPSubstringFinder(String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            throw new IllegalArgumentException(StringUtils.format("Pattern string is empty or null"));
        }

        this.pattern = pattern.toCharArray();
        this.next = new int[this.pattern.length];
        fillNext();
        this.next[0] = -1;
    }

    private void fillNext() {
        for (int i = 1; i < pattern.length - 1; i++) {
            next[i + 1] = calcNext(i, 1);
        }
    }

    private int calcNext(int idx, int level) {
        int tmp = fn(idx, level);
        if (pattern[tmp] == pattern[idx]) {
            return tmp + 1;
        } else if (tmp == 0) {
            return 0;
        } else {
            return calcNext(idx, level + 1);
        }
    }

    private int fn(int len, int level) {
        int nxt = len;
        for (int i = 0; i < level; i ++) {
            if (nxt == 0) {
                return 0;
            }
            nxt = next[nxt];
        }
        return nxt;
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
