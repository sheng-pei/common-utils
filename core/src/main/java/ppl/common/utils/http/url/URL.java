package ppl.common.utils.http.url;

import ppl.common.utils.Bytes;
import ppl.common.utils.asserts.IPs;
import ppl.common.utils.character.ascii.AsciiGroup;
import ppl.common.utils.character.ascii.Mask;
import ppl.common.utils.net.URICharGroup;
import ppl.common.utils.net.URLDecoder;
import ppl.common.utils.net.URLEncoder;
import ppl.common.utils.pair.Pair;
import ppl.common.utils.string.Strings;
import ppl.common.utils.string.substring.Substring;
import ppl.common.utils.string.substring.SubstringFinder;
import ppl.common.utils.string.substring.impl.SundaySubstringFinder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Standard or compatible form of http(s) url like 'host[:8888][/path][?query][#fragment]'.
 */
public class URL {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    public static final String DEFAULT_SCHEME = HTTP;

    private static final Pattern COMPATIBLE_HOST_PATTERN = Pattern.compile("(?:[a-zA-Z0-9-]+\\.)*[a-zA-Z0-9-]+");
    private static final Pattern PATH_PATTERN = Pattern.compile("(?:/(?:[a-zA-Z0-9_.~!$&'()*+,;=:@-]|[^\\00-\\0177]|%[0-9a-fA-F]{2})*)*");
    private static final Pattern QUERY_AND_FRAGMENT_PATTERN = Pattern.compile("(?:[a-zA-Z0-9_.~!$&'()*+,;=:@/?-]|[^\\00-\\0177]|%[0-9a-fA-F]{2})*");

    private static final URLEncoder COMPATIBLE_CHARACTER_ENCODER = URLEncoder.builder()
            .setPercentEncodingReserved(true)
            .orDontNeedToEncode((
                    Mask.asciiMask("[]\"<>^`{}|")
                            .bitOr(Mask.asciiMask('\1', '\040'))
                            .bitOr(Mask.asciiMask("\177"))
            ).bitNot().predicate())
            .build();

    private static final URLEncoder NON_ASCII_ENCODER = URLEncoder.builder()
            .setPercentEncodingReserved(true)
            .orDontNeedToEncode(AsciiGroup.ALL)
            .build();

    private final String scheme;
    private final String host;
    private final Integer port;
    private final String path;
    private final String query;
    private final String fragment;
    private final List<Query> queries;

    private final Charset charset;

    private URL(
            String scheme, String host, Integer port,
            String path, String query, String fragment,
            List<Query> queries,
            Charset charset) {
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.path = path;
        this.query = query;
        this.fragment = fragment;
        this.queries = queries;

        this.charset = charset;
    }

    private static Pair<String, Integer> scheme(char[] chars, int start) {
        String scheme = DEFAULT_SCHEME;
        int next;
        if (chars[start] == '/') {//Starts with '//' i.e. authority. Use default scheme: http.
            next = start + 2;
        } else if (chars[start] == '[') {//Maybe starts with ip-literal, compatible url.
            next = start;
        } else {
            SubstringFinder finder = new SundaySubstringFinder("://");
            Substring substring = finder.find(chars, start);
            if (substring != null) {
                next = substring.start() + 3;
                scheme = new String(chars, start, substring.start());
            } else {
                next = start;//It does not start with scheme and '//'. Maybe compatible url.
            }
        }
        return Pair.create(scheme.toLowerCase(), next);
    }

    public static boolean isSupportedScheme(String scheme) {
        return HTTP.equalsIgnoreCase(scheme) || HTTPS.equalsIgnoreCase(scheme);
    }

    private static Pair<String, Integer> authority(char[] chars, int start) {
        Pair<String, Integer> a = next(chars, start, "/?#");
        if (a.getFirst().isEmpty()) {
            throw new IllegalArgumentException("No authority.");
        }
        return a;
    }

    private static Pair<String, Integer> host(char[] chars, int start) {
        assertNoUserInfo(chars, start);
        if (chars[start] == '[') {
            return ipv6(chars, start);
        } else {
            return classicHost(chars, start);
        }
    }

    private static void assertNoUserInfo(char[] chars, int start) {
        int atIdx = Strings.indexOf('@', chars, start, chars.length);
        if (atIdx >= 0) {
            throw new IllegalArgumentException("Invalid authority, userinfo is not allowed for http(s).");
        }
    }

    private static Pair<String, Integer> ipv6(char[] chars, int start) {
        int e = Strings.indexOf(']', chars, start, chars.length);
        if (e < 0) {
            throw new IllegalArgumentException("Invalid authority, no matching ']', maybe ipv6 or future ip literal.");
        }
        if (chars.length == e + 1 || Mask.asciiMask(":/?#").predicate().test(chars[e + 1])) {
            String ipv6 = new String(chars, start + 1, e - start - 1);
            if (IPs.isIpv6(ipv6)) {
                return Pair.create(ipv6, e + 1);
            } else {
                throw new IllegalArgumentException("Maybe future ip literal.");
            }
        } else {
            throw new IllegalArgumentException("Invalid authority, too many characters, maybe ipv6 literal.");
        }
    }

    private static Pair<String, Integer> classicHost(char[] chars, int start) {
        Pair<String, Integer> host = next(chars, start, ":");

        String h = URLDecoder.decode(host.getFirst());
        if (h.isEmpty()) {
            throw new IllegalArgumentException("No host.");
        }

        if (!COMPATIBLE_HOST_PATTERN.matcher(h).matches()) {
            throw new IllegalArgumentException("Invalid classic host.");
        }

        return host;
    }

    private static Pair<Integer, Integer> port(char[] chars, int start) {
        if (start == chars.length) {
            return Pair.create(null, start);
        }

        int port = -1;
        start++; //skip colon
        if (start != chars.length) {
            String p = new String(chars, start, chars.length - start);
            try {
                port = Integer.parseInt(p);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid authority, port is not number.");
            }
            if (0 > port || port >= 65536) {
                throw new IllegalArgumentException("Invalid authority, port out of range 0~65535.");
            }
        }
        return Pair.create(port, chars.length);
    }

    private static Pair<String, Integer> path(char[] chars, int start, Charset charset) {
        Pair<String, Integer> p = next(chars, start, "?#");
        p = Pair.create(COMPATIBLE_CHARACTER_ENCODER.parse(p.getFirst(), charset), p.getSecond());
        if (!PATH_PATTERN.matcher(p.getFirst()).matches()) {
            throw new IllegalArgumentException("Invalid path.");
        }
        String s = URLDecoder.decode(p.getFirst(), charset);
        if (s.contains("\0")) {
            throw new IllegalArgumentException("Nul character is not allowed in path.");
        }
        return p;
    }

    private static Pair<String, Integer> query(char[] chars, int start, Charset charset) {
        Pair<String, Integer> q = next(chars, start, "#");
        if (q.getFirst().isEmpty()) {
            return Pair.create(null, q.getSecond());
        }

        String tmp = q.getFirst().substring(1);
        tmp = COMPATIBLE_CHARACTER_ENCODER.parse(tmp, charset);
        if (!QUERY_AND_FRAGMENT_PATTERN.matcher(tmp).matches()) {
            throw new IllegalArgumentException("Invalid query.");
        }
        return Pair.create(tmp, q.getSecond());
    }

    private static Pair<String, Integer> fragment(char[] chars, int start) {
        Pair<String, Integer> f = next(chars, start, "");
        if (f.getFirst().isEmpty()) {
            return Pair.create(null, f.getSecond());
        }

        String tmp = f.getFirst().substring(1);
        if (!QUERY_AND_FRAGMENT_PATTERN.matcher(tmp).matches()) {
            throw new IllegalArgumentException("Invalid fragment.");
        }

        return Pair.create(tmp, f.getSecond());
    }

    private static Pair<String, Integer> next(char[] chars, int start, String s) {
        int e = Strings.indexOf(Mask.asciiMask(s).predicate(), chars, start, chars.length);
        if (e < 0) {
            e = chars.length;
        }
        return Pair.create(new String(chars, start, e - start), e);
    }

    public static URL create(String url) {
        return create(url, DEFAULT_CHARSET);
    }

    public static URL create(String url, Charset charset) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("Url is required.");
        }

        char[] chars = url.toCharArray();
        if (chars[0] == '?') {
            throw new IllegalArgumentException("Incomplete http url which starts with query.");
        }

        if (chars[0] == '#') {
            throw new IllegalArgumentException("Incomplete http url which starts with fragment.");
        }

        if (chars[0] == '/' && (chars.length == 1 || chars[1] != '/')) {
            throw new IllegalArgumentException("Incomplete http url which starts with path.");
        }

        charset = charset == null ? DEFAULT_CHARSET : charset;

        Pair<String, Integer> scheme = scheme(chars, 0);
        if (!isSupportedScheme(scheme.getFirst())) {
            throw new IllegalArgumentException("Scheme is not http(s).");
        }

        Pair<String, Integer> authority = authority(chars, scheme.getSecond());

        char[] authChars = authority.getFirst().toCharArray();
        Pair<String, Integer> host = host(authChars, 0);
        Pair<Integer, Integer> port = port(authChars, host.getSecond());

        Pair<String, Integer> path = path(chars, authority.getSecond(), charset);
        Pair<String, Integer> query = query(chars, path.getSecond(), charset);
        Pair<String, Integer> fragment = fragment(chars, query.getSecond());
        return new URL(
                scheme.getFirst(),
                host.getFirst(),
                port.getFirst(),
                path.getFirst(),
                query.getFirst(),
                fragment.getFirst(),
                Collections.emptyList(),
                charset);
    }

    public HttpURLConnection open() throws IOException {
        return (HttpURLConnection) new java.net.URL(NON_ASCII_ENCODER.parse(toString())).openConnection();
    }

    public HttpURLConnection open(Proxy proxy) throws IOException {
        return (HttpURLConnection) new java.net.URL(NON_ASCII_ENCODER.parse(toString())).openConnection(proxy);
    }

    public String scheme() {
        return scheme;
    }

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public String path() {
        return path;
    }

    public String query() {
        return query;
    }

    public String fragment() {
        return fragment;
    }

    public URL raw() {
        return new URL(scheme, host, port, path, query,
                fragment, Collections.emptyList(),
                charset);
    }

    public URL base() {
        return new URL(scheme, host, port, "", null,
                null, Collections.emptyList(),
                charset);
    }

    public URL truncateToPath() {
        return new URL(scheme, host, port, path, null,
                null, Collections.emptyList(),
                charset);
    }

    public URL truncateToQuery() {
        return new URL(scheme, host, port, path, query,
                null, this.queries,
                charset);
    }

    public URL truncateToFragment() {
        return new URL(scheme, host, port, path, query,
                null, this.queries,
                charset);
    }

    public URL dynamic() {
        if (query == null || query.isEmpty()) {
            return this;
        }

        List<Query> queries = Query.parseQueries(query, charset);
        queries.addAll(this.queries);
        return new URL(scheme, host, port, path, null,
                fragment, Collections.unmodifiableList(queries),
                charset);
    }

    public URL appendDynamicQuery(String name, Object value) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }

        Query q = Query.create(name, Optional.ofNullable(value)
                .map(Object::toString)
                .orElse(null), charset);
        return _appendDynamicQuery(q);
    }

    public URL appendDynamicQuery(Query query) {
        Objects.requireNonNull(query, "Query is required.");
        if (!Objects.equals(query.getCharset(), charset)) {
            String name = query.name();
            String value = query.getValue();
            query = Query.create(name, value, charset);
        }

        return _appendDynamicQuery(query);
    }

    private URL _appendDynamicQuery(Query query) {
        String n = COMPATIBLE_CHARACTER_ENCODER.parse(query.getName());
        String v = query.getValue() == null ? null : COMPATIBLE_CHARACTER_ENCODER.parse(query.getValue());
        if (QUERY_AND_FRAGMENT_PATTERN.matcher(n).matches()) {
            if (v == null || !QUERY_AND_FRAGMENT_PATTERN.matcher(v).matches()) {
                List<Query> queries = new ArrayList<>(this.queries);
                queries.add(Query.create(n, v, charset));
                return new URL(scheme, host, port, path, this.query,
                        fragment, Collections.unmodifiableList(queries),
                        charset);
            }
        }
        throw new IllegalArgumentException("Invalid query.");
    }

    public URL removeDynamicQuery(String name) {
        List<Query> queries = this.queries.stream()
                .filter(q -> !q.nameEquals(name))
                .collect(Collectors.toList());
        return new URL(scheme, host, port, path, this.query,
                fragment, Collections.unmodifiableList(queries),
                charset);
    }

    public List<Query> getDynamicQueries(String name) {
        return this.queries.stream()
                .filter(q -> q.nameEquals(name))
                .collect(Collectors.toList());
    }

    public Query getDynamicQuery(int i) {
        if (queries == null || i < 0 || i >= queries.size()) {
            throw new IndexOutOfBoundsException("" + i);
        }
        return queries.get(i);
    }

    public List<Query> getDynamicQueries() {
        return new ArrayList<>(this.queries);
    }

    @Override
    public String toString() {
        return scheme +
                "://" +
                generateHost(_host(false)) +
                generatePort(_port(false)) +
                generatePath(_path(false)) +
                generateQuery(joinQuery(false)) +
                generateFragment(fragment);
    }

    public String normalizeString() {
        return scheme +
                "://" +
                generateHost(_host(true)) +
                generatePort(_port(true)) +
                generatePath(_path(true)) +
                generateQuery(joinQuery(true));
    }

    private String generateHost(String host) {
        StringBuilder builder = new StringBuilder();
        if (host.contains(":")) {
            builder.append('[').append(host).append(']');
        } else {
            builder.append(host);
        }
        return builder.toString();
    }

    private String generatePort(Integer port) {
        if (port == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        builder.append(':');
        if (port >= 0) {
            builder.append(port);
        }
        return builder.toString();
    }

    private String generatePath(String path) {
        return path;
    }

    private String generateQuery(String query) {
        if (query == null) {
            return "";
        }

        return "?" + query;
    }

    private String joinQuery(boolean normalize) {
        String query = this.query;
        List<Query> queries = this.queries;
        if (query == null && queries.isEmpty()) {
            return normalize ? "" : null;
        }

        String joinedQuery = Query.joinQuery(query, queries);
        return normalize ? normalize(joinedQuery) : joinedQuery;
    }

    private String generateFragment(String fragment) {
        if (fragment == null) {
            return "";
        }

        return "#" + fragment;
    }

    private String _host(boolean normalize) {
        String host = this.host;
        if (normalize) {
            StringBuilder builder = new StringBuilder();
            char[] chars = host.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] != '%') {
                    if (AsciiGroup.UP_ALPHA.test(chars[i])) {
                        builder.append(Character.toLowerCase(chars[i]));
                    } else {
                        builder.append(chars[i]);
                    }
                } else {
                    char c = (char) Bytes.oneFromHex(chars[i + 1], chars[i + 2]);
                    if (URICharGroup.UNRESERVED.test(c)) {
                        if (AsciiGroup.UP_ALPHA.test(c)) {
                            builder.append(Character.toLowerCase(c));
                        } else {
                            builder.append(c);
                        }
                    } else {
                        builder.append(chars[i]);
                        builder.append(Character.toUpperCase(chars[i + 1]));
                        builder.append(Character.toUpperCase(chars[i + 2]));
                    }
                    i += 2;
                }
            }
            host = builder.toString();
        }
        return host;
    }

    private Integer _port(boolean normalize) {
        Integer port = this.port;
        if (normalize) {
            if (port != null && port.equals(-1)) {
                port = null;
            }

            if (port != null) {
                if (scheme.equals(HTTP) && port.equals(80)) {
                    port = null;
                } else if (scheme.equals(HTTPS) && port.equals(443)) {
                    port = null;
                }
            }
        }
        return port;
    }

    private String _path(boolean normalize) {
        String path = this.path;
        if (normalize) {
            if (Strings.isEmpty(path)) {
                path = "/";
            } else {
                path = normalize(path);
            }
        }
        return path;
    }

    private String normalize(String string) {
        char[] chars = string.toCharArray();
        StringBuilder builder = new StringBuilder(chars.length);
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '%') {
                char c = (char) Bytes.oneFromHex(chars[i + 1], chars[i + 2]);
                if (URICharGroup.UNRESERVED.test(c)) {
                    builder.append(c);
                } else {
                    builder.append(chars[i]);
                    builder.append(Character.toUpperCase(chars[i + 1]));
                    builder.append(Character.toUpperCase(chars[i + 2]));
                }
                i += 2;
            } else {
                builder.append(chars[i]);
            }
        }
        return builder.toString();
    }

}
