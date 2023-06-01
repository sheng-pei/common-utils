package ppl.common.utils.string.ascii;

import ppl.common.utils.character.ascii.AsciiGroup;

public final class CaseIgnoreString {
    private transient final int hash;
    private final String string;

    private CaseIgnoreString(String string) {
        this.string = string;
        this.hash = string.toUpperCase().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaseIgnoreString that = (CaseIgnoreString) o;
        return string.equalsIgnoreCase(that.string);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public String toString() {
        return string;
    }

    public static CaseIgnoreString create(String string) {
        if (string == null) {
            return null;
        }

        char[] chars = string.toCharArray();
        for (char c : chars) {
            if (AsciiGroup.ALL.negate().test(c)) {
                throw new IllegalArgumentException("Contains non-ascii character.");
            }
        }
        return new CaseIgnoreString(string);
    }

}
