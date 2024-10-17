package ppl.common.utils.ext;

import ppl.common.utils.string.Strings;

import java.util.*;
import java.util.regex.Pattern;

public class Exts {
    public static final char EXT_DELIMITER = '.';

    private final EnumMap<ExtKind, ExtSelector> selectors = new EnumMap<>(ExtKind.class);

    private Exts(Builder builder) {
        List<ExtPattern> patterns = builder.patterns;
        for (ExtPattern pattern : patterns) {
            selectors.computeIfAbsent(pattern.kind(), k -> pattern.kind().create())
                    .addPattern(pattern);
        }
    }

    public Ext getExt(String name) {
        String[] items = Strings.split(name, Pattern.quote("" + EXT_DELIMITER));
        List<ExtPattern> patterns = new ArrayList<>();
        for (String item : items) {
            if (!item.isEmpty()) {
                for (ExtSelector selector : selectors.values()) {
                    if (selector != null) {
                        patterns.addAll(selector.select(item));
                    }
                }
            }
        }

        if (patterns.size() == 1 && patterns.get(0).matches(name)) {
            return new Ext(true, patterns.get(0).ext());
        }
        if (!patterns.isEmpty()) {
            patterns.sort(Comparator.comparingInt(ExtPattern::order));
            Optional<Ext> optionalExt = patterns.stream()
                    .filter(p -> p.matches(name))
                    .findFirst()
                    .map(ExtPattern::ext)
                    .map(e -> new Ext(true, e));
            if (optionalExt.isPresent()) {
                return optionalExt.get();
            }
        }

        int periodIdx = name.lastIndexOf('.');
        if (periodIdx == -1) {
            return null;
        }
        String unknownExt = name.substring(periodIdx + 1);
        return new Ext(false, unknownExt);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<ExtPattern> patterns = new ArrayList<>();

        public Builder add(ExtPattern pattern) {
            this.patterns.add(pattern);
            return this;
        }

        public Exts build() {
            return new Exts(this);
        }
    }

    public static class Ext {
        private final boolean known;
        private final String ext;

        public Ext(boolean known, String ext) {
            this.known = known;
            this.ext = ext;
        }

        public boolean isKnown() {
            return known;
        }

        public String getExt() {
            return ext;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Ext ext1 = (Ext) o;
            return known == ext1.known && Objects.equals(ext, ext1.ext);
        }

        @Override
        public int hashCode() {
            return Objects.hash(known, ext);
        }

        @Override
        public String toString() {
            return (isKnown() ? "known" : "unknown") +
                    " extension name: '" + getExt() + "'.";
        }
    }
}
