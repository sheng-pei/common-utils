package ppl.common.utils.http.header.value.mediatype;

import ppl.common.utils.argument.argument.Arguments;
import ppl.common.utils.argument.argument.value.ValuedArgument;
import ppl.common.utils.argument.argument.value.map.Mappers;
import ppl.common.utils.http.header.BaseArguments;
import ppl.common.utils.http.header.value.UnknownParameterTargetException;
import ppl.common.utils.http.header.value.parameter.ParameterValuedArgument;
import ppl.common.utils.http.symbol.Lexer;
import ppl.common.utils.string.Strings;
import ppl.common.utils.character.ascii.CaseIgnoreString;

import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

public class Mime implements Arguments<String, ValuedArgument<Object>> {

    private static final ValuedArgument<Charset> CHARSET_ARGUMENT =
            ParameterValuedArgument.newBuilder("charset")
                    .map(Mappers.required())
                    .map(Charset::forName)
                    .collect()
                    .build(v -> v.name().toLowerCase());

    private static final ValuedArgument<String> BOUNDARY_ARGUMENT =
            ParameterValuedArgument.newBuilder("boundary")
                    .map(Mappers.required())
                    .map(Mappers.predicate(Lexer::isBoundary, "Invalid boundary."))
                    .collect()
                    .build();


    private static final Map<String, Mime> PRIMARY_MAPPER = new HashMap<>();
    private static final Map<CaseIgnoreString, Mime> MIMES;
    public static final Mime JSON;
    public static final Mime PLAIN;
    public static final Mime HTML;
    public static final Mime XHTML;
    public static final Mime OCTET;
    public static final Mime XLS;
    public static final Mime XLSX;
    public static final Mime XML;
    public static final Mime ZIP;
    public static final Mime TAR;
    public static final Mime RAR;
    public static final Mime X_WWW_FORM_URLENCODED;
    public static final Mime MULTIPART_FORM_DATA;
    public static final Mime MULTIPART_MIXED;

    static {
        Map<CaseIgnoreString, Mime> mimes = new HashMap<>();
        JSON = register(mimes, "application/json", Collections.singletonList(CHARSET_ARGUMENT), "json");
        PLAIN = register(mimes, "text/plain", Collections.singletonList(CHARSET_ARGUMENT), "txt");
        HTML = register(mimes, "text/html", Collections.singletonList(CHARSET_ARGUMENT), "htm", "html");
        XHTML = register(mimes, "application/xhtml+xml", Collections.singletonList(CHARSET_ARGUMENT), "xhtml");
        OCTET = register(mimes, "application/octet-stream", "bin");
        XLS = register(mimes, "application/vnd.ms-excel", "xls");
        XLSX = register(mimes, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx");
        XML = register(mimes, "application/xml", "xml");
        ZIP = register(mimes, "application/x-zip-compressed", "zip");
        TAR = register(mimes, "application/x-tar", "tar");
        RAR = register(mimes, "application/vnd.rar", "rar");
        X_WWW_FORM_URLENCODED = register(mimes, "application/x-www-form-urlencoded");
        MULTIPART_FORM_DATA = register(mimes, "multipart/form-data", Collections.singletonList(BOUNDARY_ARGUMENT));
        MULTIPART_MIXED = register(mimes, "multipart/mixed", Collections.singletonList(BOUNDARY_ARGUMENT));
        MIMES = Collections.unmodifiableMap(mimes);
    }

    private static Mime register(
            Map<CaseIgnoreString, Mime> mimes,
            String mime, String... extensions) {
        return register(mimes, mime, Collections.emptyList(), extensions);
    }

    private static Mime register(
            Map<CaseIgnoreString, Mime> mimes,
            String mime,
            List<? extends ValuedArgument<?>> arguments,
            String... extensions) {
        return register(mimes, mime, arguments, Arrays.asList(extensions), extensions);
    }

    private static Mime register(
            Map<CaseIgnoreString, Mime> mimes,
            String mime,
            List<? extends ValuedArgument<?>> arguments,
            List<String> primaryExtensions,
            String... extensions) {
        Set<String> p = new HashSet<>(primaryExtensions);
        p.forEach(s -> {
            if (PRIMARY_MAPPER.containsKey(s)) {
                throw new IllegalArgumentException("More than one mime map to single primary extension.");
            }
        });

        Set<String> g = Arrays.stream(extensions)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        p.removeAll(g);
        if (!p.isEmpty()) {
            throw new IllegalArgumentException(Strings.format("Unknown primary extensions: '{}'", p));
        }

        Mime ret = new Mime(mime, arguments, g);
        mimes.put(ret.mime, ret);
        primaryExtensions.forEach(s -> PRIMARY_MAPPER.put(s, ret));
        return ret;
    }

    private final CaseIgnoreString mime;
    private final BaseArguments arguments;
    private final Set<String> extensions;

    private Mime(String string) {
        this(string, Collections.emptyList(), Collections.emptySet());
    }

    private Mime(String string, Set<String> extensions) {
        this(string, Collections.emptyList(), extensions);
    }

    private Mime(String string, List<? extends ValuedArgument<?>> arguments, Set<String> extensions) {
        string = Strings.emptyIfNull(string).trim();
        int slashIdx = string.indexOf('/');
        if (slashIdx < 0) {
            throw new IllegalArgumentException("Subtype is required.");
        }
        if (!Lexer.isToken(string.substring(0, slashIdx)) ||
                !Lexer.isToken(string.substring(slashIdx + 1))) {
            throw new IllegalArgumentException("Type or subtype is not token.");
        }
        this.mime = CaseIgnoreString.create(string);
        this.arguments = arguments.isEmpty() ? BaseArguments.EMPTY : new BaseArguments(arguments);
        this.extensions = extensions;
    }

    public String getType() {
        String string = mime.toString();
        int slashIdx = string.indexOf('/');
        return string.substring(0, slashIdx);
    }

    public String getSubType() {
        String string = mime.toString();
        int slashIdx = string.indexOf('/');
        return string.substring(slashIdx + 1);
    }

    public Set<String> getExtensions() {
        return new HashSet<>(extensions);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mime m = (Mime) o;
        return Objects.equals(mime, m.mime);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(mime);
    }

    @Override
    public String toString() {
        return mime.toString();
    }

    public static Mime create(String mime) {
        if (mime == null) {
            return null;
        }

        Mime m = MIMES.get(CaseIgnoreString.create(mime));
        if (m == null) {
            throw new UnknownParameterTargetException("Unknown mime: " + mime);
        }
        return m;
    }

    public static Mime primary(String extension) {
        Objects.requireNonNull(extension);
        return PRIMARY_MAPPER.get(extension);
    }

    @Override
    public ValuedArgument<Object> getByKey(String s) {
        return arguments.getByKey(s);
    }

    @Override
    public ValuedArgument<Object> getByName(String name) {
        return arguments.getByName(name);
    }
}
