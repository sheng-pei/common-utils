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
        int patternStart = start;
        int patternEnd = patternStart + this.pattern.length;

        while (patternEnd <= end) {

            if (this.isPatternAtTheStartOf(input, patternStart)) {
                return patternStart;
            }

            if (patternEnd == end) {
                return -1;
            }

            int distance = distance(input[patternEnd]);

            patternStart += distance;
            patternEnd += distance;
        }

        return -1;
    }

    private boolean isPatternAtTheStartOf(char[] input, int start) {
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
