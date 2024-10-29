package ppl.common.utils.ext;

import ppl.common.utils.string.Strings;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Exts {
    public static final Exts DEFAULT_EXTS = Exts.builder()
            .add("tar.gz")
            .add("ASC")
            .add("asv")
            .add("AVI")
            .add("bdf")
            .add("bmp")
            .add("cae")
            .add("CATPart")
            .add("csv")
            .add("d7d")
            .add("dat")
            .add("db")
            .add("doc")
            .add("docx")
            .add("dsd")
            .add("dwg")
            .add("dxf")
            .add("emf")
            .add("exe")
            .add("frq")
            .add("hm")
            .add("igs")
            .add("inp")
            .add("JPG")
            .add("kdh")
            .add("lbst")
            .add("lcfg")
            .add("log")
            .add("lsp")
            .add("m")
            .add("mat")
            .add("mp4")
            .add("md")
            .add("ogg")
            .add("opj")
            .add("pdf")
            .add("png")
            .add("ppt")
            .add("pptx")
            .add("rar")
            .add("raw")
            .add("rtf")
            .add("stl")
            .add("stp")
            .add("txt")
            .add("unv")
            .add("vsd")
            .add("wps")
            .add("x_t")
            .add("xls")
            .add("xlsx")
            .add("xml")
            .add("zip")
            .add("rie/brt/brt(?:\\.[0-9]+)?")
            .add("rie/asm/asm(?:\\.[0-9]+)?")
            .add("riel/prepin/prepin")
            .add("rip/bsd/bsd[0-9]*")
            .build();

    public static final char EXT_DELIMITER = '.';

    private final EnumMap<ExtKind, ExtSelector> selectors = new EnumMap<>(ExtKind.class);

    private Exts(Builder builder) {
        List<ExtPattern> patterns = builder.patterns;
        int order = 0;
        for (ExtPattern pattern : patterns) {
            OrderedExtPattern orderedExtPattern = new OrderedExtPattern(pattern, order++);
            selectors.computeIfAbsent(pattern.kind(), k -> pattern.kind().create())
                    .addPattern(orderedExtPattern);
        }
    }

    @Deprecated
    public ParsedName parse(String name) {
        List<ExtPattern> patterns = getPatterns(name);
        ParsedName parsedName = parse(patterns, name);
        if (parsedName != null) {
            return parsedName;
        }

        int periodIdx = name.lastIndexOf(EXT_DELIMITER);
        if (periodIdx == -1) {
            return null;
        }
        String unknownExt = name.substring(periodIdx + 1);
        return new ParsedName(name.substring(0, periodIdx),
                name.substring(periodIdx + 1),
                new Ext(false, ExtPosition.RIGHT, unknownExt));
    }

    public ParsedName parseKnownExt(String name) {
        List<ExtPattern> patterns = getPatterns(name);
        return parse(patterns, name);
    }

    private ParsedName parse(List<ExtPattern> patterns, String name) {
        if (!patterns.isEmpty()) {
            Optional<ParsedName> optionalExt = patterns.stream()
                    .map(p -> p.parse(name))
                    .filter(Objects::nonNull)
                    .findFirst();
            if (optionalExt.isPresent()) {
                return optionalExt.get();
            }
        }
        return null;
    }

    private List<ExtPattern> getPatterns(String name) {
        List<OrderedExtPattern> patterns = new ArrayList<>();
        if (!selectors.isEmpty()) {
            String[] items = Strings.split(name, Pattern.quote("" + EXT_DELIMITER));
            for (String item : items) {
                if (!item.isEmpty()) {
                    for (ExtSelector selector : selectors.values()) {
                        if (selector != null) {
                            patterns.addAll(selector.select(item));
                        }
                    }
                }
            }
        }
        return patterns.stream()
                .sorted(Comparator.comparingInt(OrderedExtPattern::getOrder))
                .map(OrderedExtPattern::getPattern)
                .collect(Collectors.toList());
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

        public Builder add(String pattern) {
            return add(ExtPatternParser.compile(pattern));
        }

        public Exts build() {
            return new Exts(this);
        }
    }

    public static class Ext {
        private final boolean known;
        private final ExtPosition position;
        private final String ext;

        public Ext(boolean known, ExtPosition position, String ext) {
            this.known = known;
            this.position = position;
            this.ext = ext;
        }

        public boolean isKnown() {
            return known;
        }

        public ExtPosition getPosition() {
            return position;
        }

        public String getExt() {
            return ext;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Ext ext1 = (Ext) o;
            return known == ext1.known &&
                    position == ext1.position &&
                    Objects.equals(ext, ext1.ext);
        }

        @Override
        public int hashCode() {
            return Objects.hash(known, position, ext);
        }

        @Override
        public String toString() {
            return (isKnown() ? "known" : "unknown") +
                    " extension name: '" + getExt() + "' " +
                    "and on the " + position.name().toLowerCase() + ".";
        }
    }

    public static class ParsedName {
        private final String base;
        private final String ext;
        private final Ext parsedExt;

        public ParsedName(String base, String ext, Ext parsedExt) {
            this.base = base;
            this.ext = ext;
            this.parsedExt = parsedExt;
        }

        public Ext getParsedExt() {
            return parsedExt;
        }

        public String getBase() {
            return base;
        }

        public String getExt() {
            return ext;
        }
    }
}
