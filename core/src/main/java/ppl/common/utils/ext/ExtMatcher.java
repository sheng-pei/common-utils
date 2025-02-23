package ppl.common.utils.ext;

import ppl.common.utils.string.Strings;

public class ExtMatcher {
    private final String ext;
    private final Name name;

    public ExtMatcher(String ext, Name name) {
        this.ext = ext;
        this.name = name;
    }

    public String getExt() {
        return ext;
    }

    public Name getName() {
        return name;
    }

    @Override
    public String toString() {
        return Strings.format("The extension name of '{}' is '{}'.", name, ext);
    }
}
