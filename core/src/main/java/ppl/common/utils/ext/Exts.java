package ppl.common.utils.ext;

import ppl.common.utils.string.Strings;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Exts {
    public static final Exts DEFAULT_EXTS = Exts.builder()
            .add("xlt")
            .add("fif")
            .add("xlw")
            .add("ccb")
            .add("xlv")
            .add("gkh")
            .add("mime")
            .add("cch")
            .add("bak")
            .add("dem")
            .add("cco")
            .add("gks")
            .add("der")
            .add("scc")
            .add("bat")
            .add("bas")
            .add("cct")
            .add("xml")
            .add("scf")
            .add("mxf")
            .add("shtml")
            .add("vir")
            .add("ram")
            .add("cda")
            .add("scp")
            .add("rar")
            .add("viv")
            .add("ad")
            .add("scr")
            .add("cdf")
            .add("sct")
            .add("ai")
            .add("cdi")
            .add("hcom")
            .add("scx")
            .add("raw")
            .add("am")
            .add("cdm")
            .add("cdr")
            .add("cdt")
            .add("au")
            .add("xnk")
            .add("cdx")
            .add("wll")
            .add("aac")
            .add("sdt")
            .add("sdv")
            .add("jsp")
            .add("sdx")
            .add("bi")
            .add("wma")
            .add("lcfg")
            .add("aam")
            .add("tga")
            .add("iqy")
            .add("wmf")
            .add("aas")
            .add("bw")
            .add("jpeg")
            .add("fla")
            .add("gna")
            .add("3gp")
            .add("flc")
            .add("jtf")
            .add("cb")
            .add("sep")
            .add("cc")
            .add("a")
            .add("wmv")
            .add("xlsx")
            .add("ephtml")
            .add("c")
            .add("hpj")
            .add("abf")
            .add("bdf")
            .add("cfg")
            .add("hpp")
            .add("abk")
            .add("gnt")
            .add("m")
            .add("gnx")
            .add("sfd")
            .add("abs")
            .add("cv")
            .add("resmoncfg")
            .add("xpm")
            .add("rdf")
            .add("sfi")
            .add("x")
            .add("z")
            .add("thn")
            .add("dib")
            .add("sfr")
            .add("db")
            .add("dic")
            .add("dif")
            .add("ace")
            .add("dig")
            .add("cgi")
            .add("fml")
            .add("iso")
            .add("lyr")
            .add("sfx")
            .add("cgm")
            .add("isu")
            .add("ist")
            .add("dir")
            .add("acp")
            .add("pab")
            .add("tif")
            .add("dv")
            .add("act")
            .add("tig")
            .add("diz")
            .add("sgi")
            .add("reg")
            .add("lzh")
            .add("rep")
            .add("x16")
            .add("adb")
            .add("fng")
            .add("wow")
            .add("ada")
            .add("ilbm")
            .add("res")
            .add("tar.gz")
            .add("adf")
            .add("fnk")
            .add("pas")
            .add("lzs")
            .add("adi")
            .add("chk")
            .add("clss")
            .add("j62")
            .add("chm")
            .add("wpd")
            .add("adm")
            .add("adp")
            .add("af2")
            .add("chr")
            .add("wpf")
            .add("af3")
            .add("wpg")
            .add("pptx")
            .add("ads")
            .add("pbd")
            .add("shg")
            .add("wr1")
            .add("pbm")
            .add("wpt")
            .add("pbl")
            .add("wps")
            .add("ult")
            .add("emd")
            .add("emf")
            .add("cif")
            .add("pbr")
            .add("rft")
            .add("shw")
            .add("fon")
            .add("eml")
            .add("bgl")
            .add("cil")
            .add("fm")
            .add("hst")
            .add("fot")
            .add("rgb")
            .add("pcd")
            .add("sig")
            .add("hta")
            .add("x32")
            .add("gra")
            .add("pcl")
            .add("grf")
            .add("pcm")
            .add("class")
            .add("dlg")
            .add("htm")
            .add("grp")
            .add("dll")
            .add("gl")
            .add("go")
            .add("afm")
            .add("htt")
            .add("dls")
            .add("tlb")
            .add("htx")
            .add("wrl")
            .add("pdd")
            .add("wrk")
            .add("pdf")
            .add("iwc")
            .add("obj")
            .add("dmd")
            .add("dmf")
            .add("unv")
            .add("bif")
            .add("wrz")
            .add("hm")
            .add("bin")
            .add("ska")
            .add("vqf")
            .add("ht")
            .add("skl")
            .add("tmp")
            .add("nan")
            .add("nap")
            .add("frm")
            .add("cll")
            .add("frq")
            .add("ocx")
            .add("clp")
            .add("eps")
            .add("frt")
            .add("cls")
            .add("frx")
            .add("pfm")
            .add("doc")
            .add("cmd")
            .add("cmf")
            .add("mb1")
            .add("ods")
            .add("aim")
            .add("xwd")
            .add("vsd")
            .add("xwf")
            .add("dot")
            .add("js")
            .add("ais")
            .add("qic")
            .add("bks")
            .add("ncb")
            .add("cmv")
            .add("ncd")
            .add("vsl")
            .add("cmx")
            .add("qif")
            .add("ncf")
            .add("tol")
            .add("mad")
            .add("w3l")
            .add("maf")
            .add("pgl")
            .add("vst")
            .add("pgm")
            .add("vss")
            .add("ftg")
            .add("mam")
            .add("cnf")
            .add("vsw")
            .add("maq")
            .add("map")
            .add("mar")
            .add("mat")
            .add("cnm")
            .add("fts")
            .add("m2")
            .add("cnt")
            .add("draw")
            .add("settingcontent-ms")
            .add("dpx")
            .add("url")
            .add("nist")
            .add("tpl")
            .add("ofn")
            .add("cod")
            .add("php")
            .add("oft")
            .add("com")
            .add("bmk")
            .add("mbx")
            .add("bmp")
            .add("use")
            .add("rmd")
            .add("lu")
            .add("pic")
            .add("akw")
            .add("lab")
            .add("ogg")
            .add("php3")
            .add("alb")
            .add("md")
            .add("mcr")
            .add("aiaif")
            .add("cpl")
            .add("all")
            .add("cpp")
            .add("cpo")
            .add("cpr")
            .add("cpt")
            .add("qlb")
            .add("drw")
            .add("drv")
            .add("cpx")
            .add("mdb")
            .add("spl")
            .add("trm")
            .add("mde")
            .add("nff")
            .add("trn")
            .add("searchconnector-ms")
            .add("mdl")
            .add("dsd")
            .add("lbm")
            .add("mdn")
            .add("mpeg")
            .add("dsg")
            .add("pjt")
            .add("nft")
            .add("dsm")
            .add("lbt")
            .add("pjx")
            .add("dsp")
            .add("mdw")
            .add("mdz")
            .add("dsq")
            .add("lbx")
            .add("tiff")
            .add("sqc")
            .add("ams")
            .add("prproj")
            .add("dsw")
            .add("pkg")
            .add("crd")
            .add("dtd")
            .add("sqr")
            .add("anc")
            .add("ani")
            .add("kar")
            .add("crp")
            .add("vxd")
            .add("crt")
            .add("ant")
            .add("ans")
            .add("ttf")
            .add("ldb")
            .add("pli")
            .add("catpart")
            .add("csc")
            .add("ldl")
            .add("rpt")
            .add("ph")
            .add("dun")
            .add("csp")
            .add("cst")
            .add("uwf")
            .add("csv")
            .add("py")
            .add("leg")
            .add("nil")
            .add("exe")
            .add("java")
            .add("inrs")
            .add("search-ms")
            .add("ctl")
            .add("jar")
            .add("api")
            .add("apk")
            .add("olb")
            .add("aps")
            .add("qw")
            .add("png")
            .add("zap")
            .add("brx")
            .add("ole")
            .add("stl")
            .add("kdc")
            .add("kdh")
            .add("stp")
            .add("jbf")
            .add("ra")
            .add("str")
            .add("dwg")
            .add("lft")
            .add("rm")
            .add("bsp")
            .add("phtml")
            .add("cur")
            .add("mic")
            .add("mid")
            .add("mim")
            .add("dxf")
            .add("pot")
            .add("lgo")
            .add("arj")
            .add("ari")
            .add("sl")
            .add("btm")
            .add("so")
            .add("getright")
            .add("key")
            .add("dxr")
            .add("ppa")
            .add("art")
            .add("lha")
            .add("rtf")
            .add("yal")
            .add("lbst")
            .add("ppf")
            .add("ppm")
            .add("x_t")
            .add("asa")
            .add("docx")
            .add("asc")
            .add("bud")
            .add("d7d")
            .add("txt")
            .add("ase")
            .add("pps")
            .add("txw")
            .add("asd")
            .add("nls")
            .add("asf")
            .add("ppt")
            .add("nlu")
            .add("qry")
            .add("asm")
            .add("bun")
            .add("aso")
            .add("kfx")
            .add("swa")
            .add("asp")
            .add("swf")
            .add("lib")
            .add("asv")
            .add("asx")
            .add("icc")
            .add("rul")
            .add("icb")
            .add("lin")
            .add("icm")
            .add("icl")
            .add("ico")
            .add("lis")
            .add("atn")
            .add("cxx")
            .add("prg")
            .add("atw")
            .add("prf")
            .add("idd")
            .add("xar")
            .add("opj")
            .add("prj")
            .add("idf")
            .add("jff")
            .add("mli")
            .add("qtp")
            .add("prn")
            .add("rvp")
            .add("opo")
            .add("prt")
            .add("qtx")
            .add("idq")
            .add("a3m")
            .add("idx")
            .add("bwv")
            .add("psd")
            .add("xbm")
            .add("a3w")
            .add("mme")
            .add("aiff")
            .add("sgml")
            .add("book")
            .add("aifc")
            .add("avb")
            .add("psp")
            .add("sys")
            .add("pst")
            .add("syw")
            .add("gal")
            .add("avi")
            .add("wab")
            .add("wp")
            .add("a4m")
            .add("wad")
            .add("avr")
            .add("a4p")
            .add("kiz")
            .add("avs")
            .add("a4w")
            .add("mng")
            .add("iff")
            .add("wav")
            .add("mp2")
            .add("awd")
            .add("mp4")
            .add("mp3")
            .add("oogl")
            .add("xi")
            .add("mnu")
            .add("llx")
            .add("awr")
            .add("wbk")
            .add("a5w")
            .add("mod")
            .add("jif")
            .add("igf")
            .add("sprite3")
            .add("sprite2")
            .add("igs")
            .add("mov")
            .add("kkw")
            .add("fav")
            .add("fax")
            .add("mpa")
            .add("wcm")
            .add("zip")
            .add("7z")
            .add("mpe")
            .add("gdb")
            .add("mpg")
            .add("lnk")
            .add("mpp")
            .add("mpr")
            .add("p65")
            .add("gdm")
            .add("vba")
            .add("wdb")
            .add("dewf")
            .add("log")
            .add("vbp")
            .add("fcd")
            .add("vbs")
            .add("vbw")
            .add("hgl")
            .add("vbx")
            .add("gem")
            .add("kmp")
            .add("gen")
            .add("pwz")
            .add("theme")
            .add("vct")
            .add("jfif")
            .add("biff")
            .add("fdf")
            .add("vcx")
            .add("gfi")
            .add("vda")
            .add("ntx")
            .add("gfx")
            .add("pyc")
            .add("wfn")
            .add("wfm")
            .add("msg")
            .add("msi")
            .add("msn")
            .add("msp")
            .add("jmp")
            .add("mst")
            .add("pyw")
            .add("m1v")
            .add("dat")
            .add("accountpicture-ms")
            .add("html")
            .add("ffa")
            .add("eda")
            .add("dbc")
            .add("edd")
            .add("mtm")
            .add("dbf")
            .add("ffk")
            .add("taz")
            .add("ffl")
            .add("ffo")
            .add("gho")
            .add("udf")
            .add("dbx")
            .add("nwc")
            .add("ima")
            .add("sb2")
            .add("cab")
            .add("gif")
            .add("sb3")
            .add("cae")
            .add("cad")
            .add("part")
            .add("lsp")
            .add("gim")
            .add("kqp")
            .add("nws")
            .add("lst")
            .add("m3u")
            .add("cal")
            .add("dcm")
            .add("dcp")
            .add("cap")
            .add("cas")
            .add("dct")
            .add("dcs")
            .add("dcu")
            .add("dcx")
            .add("wil")
            .add("jpe")
            .add("inf")
            .add("jpg")
            .add("ini")
            .add("sav")
            .add("wiz")
            .add("inp")
            .add("xla")
            .add("hlp")
            .add("int")
            .add("m4v")
            .add("tar")
            .add("gz")
            .add("xlc")
            .add("ins")
            .add("k25")
            .add("xlb")
            .add("xld")
            .add("xlk")
            .add("xlm")
            .add("xll")
            .add("sbl")
            .add("xls")
            .add("0c/s")
            .add("0c/S")
            .add("rie/brt/brt(?:\\.[0-9]+)?")
            .add("rie/asm/asm(?:\\.[0-9]+)?")
            .add("riel/prepin/prepin")
            .add("rip/bsd/bsd[0-9]*")
            .add("rie/part.rar/part[0-9]*.rar")
            .build();

    public static final char EXT_DELIMITER = '.';

    private final EnumMap<ExtKind, ExtSelector> selectors = new EnumMap<>(ExtKind.class);

    private Exts(Builder builder) {
        Map<String, OrderedExtPattern> patterns = builder.patterns;
        for (OrderedExtPattern pattern : patterns.values()) {
            selectors.computeIfAbsent(pattern.getPattern().kind(), k -> pattern.getPattern().kind().create())
                    .addPattern(pattern);
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

    public Builder copy() {
        List<OrderedExtPattern> patterns = selectors.values().stream()
                .flatMap(s -> s.getPatterns().stream())
                .collect(Collectors.toList());
        return new Builder(patterns);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int max;
        private final Map<String, OrderedExtPattern> patterns = new HashMap<>();

        private Builder() {
            max = 0;
        }

        private Builder(List<OrderedExtPattern> patterns) {
            int max = 0;
            for (OrderedExtPattern p : patterns) {
                if (p.getOrder() > max) {
                    max = p.getOrder();
                }

                this.patterns.put(p.getPattern().ext(), p);
            }
            this.max = max + 1;
        }

        public Builder add(ExtPattern pattern) {
            this.patterns.put(pattern.ext(), new OrderedExtPattern(pattern, max ++));
            return this;
        }

        public Builder add(String pattern) {
            return add(ExtPatternParser.compile(pattern));
        }

        public Builder remove(String ext) {
            patterns.remove(ext);
            return this;
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
