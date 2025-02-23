package ppl.common.utils.ext;

import ppl.common.utils.string.Strings;

public class Ext {
    private final String ext;
    private final Name name;

    public Ext(String ext, Name name) {
        this.ext = ext;
        this.name = name;
    }

    public String getExt() {
        return ext;
    }

    public Name getName() {
        return name;
    }

    public int length() {
        return countOf(ext, Exts.EXT_DELIMITER) + 1;
    }

    private static int countOf(String string, char c) {
        char[] chars = string.toCharArray();
        int i = 0;
        for (char c1 : chars) {
            if (c1 == c) {
                i ++;
            }
        }
        return i;
    }

    @Override
    public String toString() {
        return Strings.format("The extension name of '{}' is '{}'.", name, ext);
    }
}
