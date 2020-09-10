package ppl.common.utils;

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

    private Pattern pattern;

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
        int matchedIdx = this.match(input.toCharArray(), start, end);
        if (matchedIdx != -1) {
            return new Substring(input, matchedIdx, matchedIdx + this.pattern.length());
        }
        return null;
    }

//    private int escapableFind(char[] input) {
//        char escape = '\\';
//        int firstEscape = 0;
//        while (firstEscape != -1) {
//            int firstUnescape = StringUtils.indexOfNot(escape, input, firstEscape, input.length);
//            if (firstUnescape == -1) {
//                return -1;
//            } else {
//                int nextFirstEscape = StringUtils.indexOf(escape, input, firstUnescape, input.length);
//                int matched = match(input, firstUnescape, nextFirstEscape == -1 ? input.length : nextFirstEscape);
//                if (matched == -1) {
//                    firstEscape = nextFirstEscape;
//                } else if (matched == firstUnescape) {
//                    return firstEscape;
//                } else {
//                    return matched;
//                }
//            }
//        }
//        return -1;
//    }

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
