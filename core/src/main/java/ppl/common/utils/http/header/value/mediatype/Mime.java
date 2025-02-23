package ppl.common.utils.http.header.value.mediatype;

import ppl.common.utils.argument.argument.Arguments;
import ppl.common.utils.argument.argument.value.BaseValueArgument;
import ppl.common.utils.argument.argument.value.ValueArgument;
import ppl.common.utils.http.header.BaseArguments;
import ppl.common.utils.http.header.value.UnknownParameterTargetException;
import ppl.common.utils.http.symbol.HttpCharGroup;
import ppl.common.utils.http.symbol.Lexer;
import ppl.common.utils.string.Strings;
import ppl.common.utils.character.ascii.CaseIgnoreString;

import java.nio.charset.Charset;
import java.util.*;

public class Mime implements Arguments<String, ValueArgument<Object>> {

    private static final ValueArgument<Charset> CHARSET_ARGUMENT =
            BaseValueArgument.newBuilder("charset")
                    .map(Mime::eraseQuotedString)
                    .map(Charset::forName)
                    .collect()
                    .build(v -> Lexer.quoteString(v.name().toLowerCase()));

    private static final Map<CaseIgnoreString, Mime> MIMES;
    public static final Mime JSON;
    public static final Mime PLAIN;
    public static final Mime HTML;
    public static final Mime OCTET;

    static {
        Mime json = new Mime("application/json", Collections.singletonList(CHARSET_ARGUMENT));
        Mime plain = new Mime("text/plain", Collections.singletonList(CHARSET_ARGUMENT));
        Mime html = new Mime("text/html", Collections.singletonList(CHARSET_ARGUMENT));
        Mime octet = new Mime("application/octet-stream");
        Map<CaseIgnoreString, Mime> mimes = new HashMap<>();
        mimes.put(json.mime, json);
        mimes.put(plain.mime, plain);
        mimes.put(html.mime, html);
        mimes.put(octet.mime, octet);
        MIMES = Collections.unmodifiableMap(mimes);
        JSON = json;
        PLAIN = plain;
        HTML = html;
        OCTET = octet;
    }

    private static String eraseQuotedString(String string) {
        if (string.length() >= 2 &&
                HttpCharGroup.QM.test(string.charAt(0)) &&
                HttpCharGroup.QM.test(string.charAt(string.length() - 1))) {
            string = Lexer.eraseQuotedPair(string.substring(1, string.length() - 1));
        }
        return string;
    }

    private final CaseIgnoreString mime;
    private final BaseArguments arguments;

    Mime(String string) {
        this(string, Collections.emptyList());
    }

    Mime(String string, List<? extends ValueArgument<?>> arguments) {
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

    @Override
    public ValueArgument<Object> getByKey(String s) {
        return arguments.getByKey(s);
    }

    @Override
    public ValueArgument<Object> getByName(String name) {
        return arguments.getByName(name);
    }
}
