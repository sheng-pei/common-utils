package ppl.common.utils;

import java.util.Objects;

public class SundaySubstringFinder implements SubstringFinder {

    private static class Pattern {

        private final char[] pattern;

        public Pattern(String pattern) {
            if (StringUtils.isEmpty(pattern)) {
                throw new IllegalArgumentException(StringUtils.format("Pattern string is empty or null"));
            }

            this.pattern = pattern.toCharArray();
        }

        public boolean isAtTheStartOf(char[] input, int start) {
            for (int j = 0; j < this.length(); j++) {
                if (this.pattern[j] != input[start + j]) {
                    return false;
                }
            }
            return true;
        }

        public int lastIndexOf(char c) {
            int lastIdx = this.length() - 1;
            for (int i = lastIdx; i >= 0; i--) {
                if (this.pattern[i] == c) {
                    return this.length() - i;
                }
            }
            return this.length();
        }

        public int length() {
            return this.pattern.length;
        }

        @Override
        public String toString() {
            return String.valueOf(this.pattern);
        }

    }

    private final Pattern pattern;

    public SundaySubstringFinder(String pattern) {
        this.pattern = new Pattern(pattern);
    }

    @Override
    public String getPattern() {
        return this.pattern.toString();
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
            return new Substring(input, matchedIdx, matchedIdx + this.pattern.length());
        }
        return Substring.EMPTY_SUBSTRING;
    }

    private int match(char[] input, int start, int end) {
        int prospectiveMatchStart = start;
        int prospectiveMatchEnd = prospectiveMatchStart + this.pattern.length();

        while (prospectiveMatchEnd <= end) {

            if (this.pattern.isAtTheStartOf(input, prospectiveMatchStart)) {
                return prospectiveMatchStart;
            }

            if (prospectiveMatchEnd == end) {
                return -1;
            }

            int distanceToBeMove = this.pattern.lastIndexOf(input[prospectiveMatchEnd]);

            prospectiveMatchStart += distanceToBeMove;
            prospectiveMatchEnd += distanceToBeMove;
        }

        return -1;
    }



}
