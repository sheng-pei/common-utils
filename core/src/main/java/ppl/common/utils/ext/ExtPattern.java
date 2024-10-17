package ppl.common.utils.ext;

import ppl.common.utils.string.Strings;

import java.util.Objects;

public class ExtPattern {
    private final String ext;
    private final ExtKind kind;
    private final ExtPredicate predicate;
    private final int order;

    private ExtPattern(Builder builder) {
        if (Strings.isEmpty(builder.ext)) {
            throw new IllegalArgumentException("Extension name must be empty.");
        }
        Objects.requireNonNull(builder.kind, "Extension name selector kind is required.");
        this.ext = builder.ext;
        this.kind = builder.kind;
        this.predicate = builder.predicate == null ? ExtPredicate.ALWAYS_TRUE : builder.predicate;
        this.order = builder.order;
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

    public int order() {
        return order;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String ext;
        private ExtKind kind;
        private ExtPredicate predicate;
        private int order;

        private Builder() {}

        public Builder ext(String ext) {
            this.ext = ext;
            return this;
        }

        public Builder kind(ExtKind kind) {
            this.kind = kind;
            return this;
        }

        public Builder predicate(ExtPredicate predicate) {
            this.predicate = predicate;
            return this;
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public ExtPattern build() {
            return new ExtPattern(this);
        }

    }
}
