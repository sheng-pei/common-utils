package ppl.common.utils;

public class EscapableSundaySubstringFinder {

    private final char[] pattern;
    private final char escape;

    EscapableSundaySubstringFinder(String pattern, char escape) {
        if (StringUtils.isEmpty(pattern)) {
            throw new IllegalArgumentException(StringUtils.format("Pattern string is empty or null"));
        }

        if (pattern.indexOf(escape) > 0) {
            throw new IllegalArgumentException(StringUtils.format("Pattern: {} contains escape character: {}", pattern, escape));
        }

        this.pattern = pattern.toCharArray();
        this.escape = escape;
    }

    public boolean find(int pos, char[] reference) {

//        int firstUnescape = this.splitter.firstUnescape(this.input, pos, this.input.length - this.splitter.length() + 1);
//        if (firstUnescape < 0) {
//            return false;
//        }
//
//
//
//        int start = pos;
//        int firstUnescape = pos;
//        while (firstUnescape <= this.chars.length - reference.length) {
//
//            if (this.chars[firstUnescape] == ESCAPE_CHARACTER) {
//                firstUnescape++;
//                continue;
//            }
//
//            if (isMatch(firstUnescape, reference)) {
//                this.start = start;
//                this.end = firstUnescape + reference.length;
//                return true;
//            } else {
//                if (firstUnescape + reference.length >= this.chars.length) {
//                    break;
//                }
//                firstUnescape = start = moveTo(firstUnescape, reference);
//            }
//
//        }
        return false;
    }

    protected boolean matchSubstring(int pos, char[] reference) {
//        for (int j = 0; j < reference.length; j++) {
//            if (reference[j] != chars[pos + j]) {
//                return false;
//            }
//        }
        return true;
    }

    private int moveTo(int pos, char[] reference) {
//        int lastIndex = lastIndexOf(reference, chars[pos + reference.length]);
//        return pos + reference.length + (lastIndex == -1 ? 1 : -lastIndex);
        return 0;
    }

    private int lastIndexOf(char[] reference, char c) {
//        for (int i = reference.length - 1; i >= 0; i--) {
//            if (reference[i] == c) {
//                return i;
//            }
//        }
        return -1;
    }
}
