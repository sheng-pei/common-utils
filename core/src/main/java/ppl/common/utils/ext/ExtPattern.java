package ppl.common.utils.ext;

import ppl.common.utils.exception.UnreachableCodeException;
import ppl.common.utils.string.Strings;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtPattern {
    private final String ext;
    private final ExtKind kind;
    private final Pattern pattern;
    private final ExtPosition position;

    private ExtPattern(Builder builder) {
        if (Strings.isEmpty(builder.ext)) {
            throw new IllegalArgumentException("Extension name must be empty.");
        }
        Objects.requireNonNull(builder.kind, "Extension name selector kind is required.");
        Objects.requireNonNull(builder.pattern, "Pattern is required.");
        this.ext = builder.ext;
        this.kind = builder.kind;
        this.pattern = builder.pattern;
        this.position = builder.position == null ? ExtPosition.RIGHT : builder.position;
    }

    public String ext() {
        return ext;
    }

    public ExtKind kind() {
        return kind;
    }

    public Exts.ParsedName parse(String name) {
        Matcher matcher = pattern.matcher(name);
        if (matcher.find()) {
            Exts.Ext ext = new Exts.Ext(ext(name, matcher), ext());
            return new Exts.ParsedName(base(name, matcher), position, ext);
        }
        return null;
    }

    private String base(String name, Matcher matcher) {
        if (position == ExtPosition.LEFT) {
            if (matcher.end() == name.length()) {
                return "";
            } else {
                return name.substring(matcher.end());
            }
        } else if (position == ExtPosition.RIGHT) {
            return name.substring(0, matcher.start());
        } else {
            throw new UnreachableCodeException("Unknown position flags.");
        }
    }

    private String ext(String name, Matcher matcher) {
        if (position == ExtPosition.LEFT) {
            if (matcher.end() == name.length() && name.charAt(name.length() - 1) != '.') {
                return name;
            } else {
                return name.substring(0, matcher.end());
            }
        } else if (position == ExtPosition.RIGHT) {
            if (matcher.start() == 0 && name.charAt(0) != '.') {
                return name;
            } else {
                return name.substring(matcher.start());
            }
        } else {
            throw new UnreachableCodeException("Unknown position flags.");
        }
    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private String ext;
        private ExtKind kind;
        private Pattern pattern;
        private ExtPosition position;

        private Builder() {}

        Builder ext(String ext) {
            this.ext = ext;
            return this;
        }

        Builder kind(ExtKind kind) {
            this.kind = kind;
            return this;
        }

        Builder pattern(Pattern pattern) {
            this.pattern = pattern;
            return this;
        }

        Builder position(ExtPosition position) {
            this.position = position;
            return this;
        }

        ExtPattern build() {
            return new ExtPattern(this);
        }

    }
}
