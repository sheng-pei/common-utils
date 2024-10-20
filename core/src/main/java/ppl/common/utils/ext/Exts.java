package ppl.common.utils.ext;

import ppl.common.utils.string.Strings;

import java.util.*;
import java.util.regex.Pattern;

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
            .add("tmp")
            .add("txt")
            .add("unv")
            .add("vsd")
            .add("wps")
            .add("x_t")
            .add("xls")
            .add("xlsx")
            .add("xml")
            .add("zip")
            .add("rie/brt/\\.brt(?:\\.[0-9]+)?$")
            .add("rie/asm/\\.asm(?:\\.[0-9]+)?$")
            .add("rie/prepin/^prepin\\.")
            .build();

    private static final char EXT_DELIMITER = '.';

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

    public Ext getExt(String name) {
        List<OrderedExtPattern> patterns = new ArrayList<>();
        if (!selectors.isEmpty()) {
            String[] items = Strings.split(name, Pattern.quote("" + EXT_DELIMITER));
            for (int i = 1; i < items.length; i++) {
                if (!items[i].isEmpty()) {
                    for (ExtSelector selector : selectors.values()) {
                        if (selector != null) {
                            patterns.addAll(selector.select(items[i]));
                        }
                    }
                }
            }
        }

        if (patterns.size() == 1 && patterns.get(0).getPattern().matches(name)) {
            return new Ext(true, patterns.get(0).getPattern().ext());
        }
        if (!patterns.isEmpty()) {
            patterns.sort(Comparator.comparingInt(OrderedExtPattern::getOrder));
            Optional<Ext> optionalExt = patterns.stream()
                    .map(OrderedExtPattern::getPattern)
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

        public Builder add(String pattern) {
            return add(ExtPatternParser.compile(pattern));
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
