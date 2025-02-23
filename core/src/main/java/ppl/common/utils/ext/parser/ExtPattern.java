package ppl.common.utils.ext.parser;

import ppl.common.utils.ext.ExtMatcher;
import ppl.common.utils.ext.Name;
import ppl.common.utils.ext.selector.SelectorKind;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtPattern implements ExtParser {
    private final String name;
    private final SelectorKind supportedSelector;
    private final boolean exact;
    private final Pattern pattern;
    private final ExtPatternPosition position;

    private ExtPattern(Builder builder) {
        Objects.requireNonNull(builder.name, "Pattern name is required.");
        Objects.requireNonNull(builder.pattern, "Pattern is required.");
        Objects.requireNonNull(builder.position, "Position is required.");

        SelectorKind supportedSelector = builder.supportedSelector;
        this.name = builder.name;
        this.supportedSelector = supportedSelector == null ? SelectorKind.PREFIX : supportedSelector;
        this.exact = builder.exact;
        this.pattern = builder.pattern;
        this.position = builder.position;
    }

    protected ExtPattern(ExtPattern pattern) {
        this.name = pattern.name;
        this.supportedSelector = pattern.supportedSelector;
        this.exact = pattern.exact;
        this.pattern = pattern.pattern;
        this.position = pattern.position;
    }

    @Override
    public String name() {
        return supportedSelector.key(name);
    }

    @Override
    public boolean isAccept(String item, SelectorKind selector) {
        return this.supportedSelector == selector && (!exact || name.length() == item.length());
    }

    @Override
    public ExtMatcher parse(String name) {
        Matcher matcher = pattern.matcher(name);
        if (!matcher.find()) {
            return null;
        }

        String matched = matcher.group();
        String base = matcher.replaceFirst("");
        int cnt = matcher.groupCount();
        String ext = this.name();
        if (cnt > 0) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < cnt; i++) {
                builder.append(matcher.group(i));
            }
            ext = builder.toString();
        }

        if (ext.isEmpty()) {
            throw new IllegalStateException("Error pattern. No extension found.");
        }

        if (matcher.find()) {
            throw new IllegalStateException("Error pattern. Too many subsequence matched.");
        }

        String[] parts = position == ExtPatternPosition.LEFT ? new String[] {matched, base} : new String[] {base, matched};
        int baseIndex = position == ExtPatternPosition.LEFT ? 1 : 0;
        Name n = new Name(parts, baseIndex);
        return new ExtMatcher(ext, n);
    }

    public OrderedExtPattern orderBy(int order) {
        return new OrderedExtPattern(this, order);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private boolean exact;
        private SelectorKind supportedSelector;
        private Pattern pattern;
        private ExtPatternPosition position;

        private Builder() {}

        public Builder name(String name) {
            if (name.contains(".")) {
                throw new IllegalArgumentException("Point is not allowed.");
            }

            this.name = name;
            return this;
        }

        public Builder supportedSelector(SelectorKind supportedSelector) {
            this.supportedSelector = supportedSelector;
            return this;
        }

        public Builder exact(boolean exact) {
            this.exact = exact;
            return this;
        }

        public Builder pattern(Pattern pattern) {
            this.pattern = pattern;
            return this;
        }

        public Builder position(ExtPatternPosition position) {
            this.position = position;
            return this;
        }

        public ExtPattern build() {
            return new ExtPattern(this);
        }

    }
}
