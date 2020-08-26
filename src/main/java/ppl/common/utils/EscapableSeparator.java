package ppl.common.utils;

import java.util.Objects;

public class EscapableSeparator {

    private final char escape;
    private SubstringFinder finder;

    public EscapableSeparator(char escape, String separator) {
        if (separator.indexOf(escape) >= 0) {
            throw new IllegalArgumentException(StringUtils.format("Separator: {} could not contain escape: {}", separator, escape));
        }

        this.escape = escape;
        this.finder = new SundaySubstringFinder(separator);
    }

    public SubstringFinder getFinder() {
        return finder;
    }

    public void setFinder(SubstringFinder finder) {
        Objects.requireNonNull(finder, "Finder is null");

        if (finder.getPattern().indexOf(escape) >= 0) {
            throw new IllegalArgumentException(StringUtils.format("The pattern of finder: {} could not contain escape: {}", finder.getPattern(), this.escape));
        }

        this.finder = finder;
    }

    private void checkSeparator(String separator) {

    }

    public String[] split() {
        return new String[0];
    }

    public String replaceAll(String input, Object... parameters) {
        return null;
    }

    private int escapableFind(char[] input) {
//        char escape = '\\';
//        int firstEscape = 0;
//        while (firstEscape != -1) {
//            int firstUnescape = StringUtils.indexOfNot(escape, input, firstEscape, input.length);
//            if (firstUnescape == -1) {
//                return -1;
//            } else {
//                int nextFirstEscape = StringUtils.indexOf(escape, input, firstUnescape, input.length);
//                int matched = this.finder.match(input, firstUnescape, nextFirstEscape == -1 ? input.length : nextFirstEscape);
//                if (matched == -1) {
//                    firstEscape = nextFirstEscape;
//                } else if (matched == firstUnescape) {
//                    return firstEscape;
//                } else {
//                    return matched;
//                }
//            }
//        }
        return -1;
    }

}
