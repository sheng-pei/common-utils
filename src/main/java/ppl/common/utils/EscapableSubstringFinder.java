package ppl.common.utils;

import java.util.Objects;

public class EscapableSubstringFinder implements SubstringFinder {

    private static final char DEFAULT_ESCAPE = '\\';
    private final char escape;
    private final SubstringFinder finder;

    public EscapableSubstringFinder(char escape, SubstringFinder finder) {
        if (finder.getPattern().indexOf(escape) != -1) {
            throw new IllegalArgumentException("Pattern: " + finder.getPattern() + " mustn't contain escape: " + escape);
        }
        if (maybeOverlap(finder.getPattern().toCharArray()))
            throw new IllegalArgumentException("This pattern: " + finder.getPattern() + " may be ambiguity.");
        this.escape = escape;
        this.finder = finder;
    }

    public EscapableSubstringFinder(SubstringFinder finder) {
        this(DEFAULT_ESCAPE, finder);
    }

    public EscapableSubstringFinder(char escape, String pattern) {
        this(escape, new SundaySubstringFinder(pattern));
    }

    public EscapableSubstringFinder(String pattern) {
        this(DEFAULT_ESCAPE, new SundaySubstringFinder(pattern));
    }

    private boolean maybeOverlap(char[] pattern) {
        int[] lens = new int[pattern.length + 1];
        for (int i = 2; i < pattern.length + 1; i++) {
            int len = lens[i - 1];
            while (len != 0) {
                if (pattern[i - 1] == pattern[len]) {
                    lens[i] = len + 1;
                    break;
                }
                len = lens[len];
            }
            if (len == 0) {
                lens[i] = pattern[i - 1] == pattern[len] ? 1 : 0;
            }
        }
        return lens[pattern.length] != 0;
    }

    public char getEscape() {
        return this.escape;
    }

    @Override
    public String getPattern() {
        return this.finder.getPattern();
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

        int firstEscape = start;
        while (firstEscape != -1) {
            int firstUnescape = StringUtils.indexOfNot(this.escape, input, firstEscape, end);
            if (firstUnescape == -1) {
                return Substring.EMPTY_SUBSTRING;
            } else {
                int nextFirstEscape = StringUtils.indexOf(this.escape, input, firstUnescape, end);
                Substring substring = this.finder.find(input, firstUnescape, nextFirstEscape == -1 ? end : nextFirstEscape);
                if (substring.isEmpty()) {
                    firstEscape = nextFirstEscape;
                } else if (substring.getStart() == firstUnescape) {
                    return new Substring(input, firstEscape, substring.getEnd());
                } else {
                    return substring;
                }
            }
        }
        return Substring.EMPTY_SUBSTRING;
    }

}
