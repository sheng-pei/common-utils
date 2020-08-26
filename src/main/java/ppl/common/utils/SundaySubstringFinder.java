package ppl.common.utils;

public class SundaySubstringFinder implements SubstringFinder {

    private final char[] pattern;

    public SundaySubstringFinder(String pattern) {

        if (StringUtils.isEmpty(pattern)) {
            throw new IllegalArgumentException(StringUtils.format("Pattern string is empty or null"));
        }

        this.pattern = pattern.toCharArray();
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
        int patternStart = start;
        int patternEnd = patternStart + this.pattern.length;

        while (patternEnd < end) {

            if (this.isPrefixOf(input, patternStart)) {
                return patternStart;
            }

            int distance = distance(input[patternEnd]);

            patternStart += distance;
            patternEnd += distance;
        }

        return patternEnd == end && this.isPrefixOf(input, patternStart) ? patternStart : -1;
    }

    private boolean isPrefixOf(char[] input, int start) {
        for (int j = 0; j < pattern.length; j++) {
            if (this.pattern[j] != input[start + j]) {
                return false;
            }
        }
        return true;
    }

    private int distance(char c) {
        int lastIdx = this.pattern.length - 1;
        for (int i = lastIdx; i >= 0; i--) {
            if (this.pattern[i] == c) {
                return this.pattern.length - i;
            }
        }
        return this.pattern.length;
    }

}
