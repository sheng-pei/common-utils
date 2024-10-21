package ppl.common.utils.ext;

import ppl.common.utils.exception.UnreachableCodeException;

import java.util.regex.Matcher;

public class ExtMatcher {
    private final String name;
    private final Matcher matcher;
    private final ExtPosition position;
    private final boolean matches;

    public ExtMatcher(String name, Matcher matcher, ExtPosition position) {
        this.matches = matcher.find();
        this.name = name;
        this.matcher = matcher;
        this.position = position;
    }

    public boolean matches() {
        return matches;
    }

    public String base() {
        if (!matches) {
            throw new IllegalStateException("Extension pattern not matches.");
        }

        if (position == ExtPosition.LEFT) {
            return name.substring(matcher.start());
        } else if (position == ExtPosition.RIGHT) {
            return name.substring(0, matcher.end());
        } else {
            throw new UnreachableCodeException("Unknown position flags.");
        }
    }
}
