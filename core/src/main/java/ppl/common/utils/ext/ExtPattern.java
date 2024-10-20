package ppl.common.utils.ext;

import ppl.common.utils.string.Strings;

import java.util.Objects;

public class ExtPattern {
    private final String ext;
    private final ExtKind kind;
    private final ExtPredicate predicate;

    private ExtPattern(Builder builder) {
        if (Strings.isEmpty(builder.ext)) {
            throw new IllegalArgumentException("Extension name must be empty.");
        }
        Objects.requireNonNull(builder.kind, "Extension name selector kind is required.");
        Objects.requireNonNull(builder.predicate, "Predicate is required.");
        this.ext = builder.ext;
        this.kind = builder.kind;
        this.predicate = builder.predicate;
    }

    public String ext() {
        return ext;
    }

    public ExtKind kind() {
        return kind;
    }

    public boolean matches(String name) {
        return predicate.test(name);
    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private String ext;
        private ExtKind kind;
        private ExtPredicate predicate;

        private Builder() {}

        Builder ext(String ext) {
            this.ext = ext;
            return this;
        }

        Builder kind(ExtKind kind) {
            this.kind = kind;
            return this;
        }

        Builder predicate(ExtPredicate predicate) {
            this.predicate = predicate;
            return this;
        }

        ExtPattern build() {
            return new ExtPattern(this);
        }

    }
}
