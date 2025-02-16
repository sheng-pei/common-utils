package ppl.common.utils.http.url;

import ppl.common.utils.Bytes;
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
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Standard or compatible form of http(s) url like 'host[:8888][/path][?query][#fragment]'.
 */
public class URL {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static final String DEFAULT_PROTOCOL = HTTP;
    private static final Pattern PORT_PATTERN = Pattern.compile("[0-9]+");
    private static final Pattern REGNAME_PATTERN = Pattern.compile(
            "(?:[a-zA-Z0-9_.~!$&'()*+,;=-]|%[0-9a-fA-F]{2})*|[^\00-\0177]");
    private static final Pattern IPV4ADDRESS_PATTERN = Pattern.compile(
            "(?:(?:[0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}" +
                    "(?:[0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])");
    private static final Pattern IPV6ADDRESS_PATTERN = Pattern.compile(
            "(?:(?:[0-9a-fA-F]{1,4}:){6}|" +
                    "::(?:[0-9a-fA-F]{1,4}:){5}|" +
                    "(?:[0-9a-fA-F]{1,4})?::(?:[0-9a-fA-F]{1,4}:){4}|" +
                    "(?:(?:[0-9a-fA-F]{1,4}:)?[0-9a-fA-F]{1,4})?::(?:[0-9a-fA-F]{1,4}:){3}|" +
                    "(?:(?:[0-9a-fA-F]{1,4}:){0,2}[0-9a-fA-F]{1,4})?::(?:[0-9a-fA-F]{1,4}:){2}|" +
                    "(?:(?:[0-9a-fA-F]{1,4}:){0,3}[0-9a-fA-F]{1,4})?::[0-9a-fA-F]{1,4}:|" +
                    "(?:(?:[0-9a-fA-F]{1,4}:){0,4}[0-9a-fA-F]{1,4})?::)" +
                    "(?:[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}|" +
                    "(?:(?:[0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}(?:[0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))|" +
                    "(?:(?:[0-9a-fA-F]{1,4}:){0,5}[0-9a-fA-F]{1,4})?::[0-9a-fA-F]{1,4}|" +
                    "(?:(?:[0-9a-fA-F]{1,4}:){0,6}[0-9a-fA-F]{1,4})?::");
    private static final Pattern PATH_PATTERN = Pattern.compile("(?:/(?:[a-zA-Z0-9_.~!$&'()\\[\\]*+,;=:@-]|[^\\00-\\0177]|%[0-9a-fA-F]{2})*)*");
    public static final Pattern QUERY_AND_FRAGMENT_PATTERN = Pattern.compile("(?:[a-zA-Z0-9_.~!$&'()*+,;=:@/?-]|[^\\00-\\0177]|%[0-9a-fA-F]{2})*");

    private final String scheme;
    private final String host;
    private final Integer port;
    private final String path;
    private final String query;
    private final String fragment;
    private final List<Query> queries;

    private URL(String scheme, String host, Integer port, String path, String query, String fragment, List<Query> queries) {
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.path = path;
        this.query = query;
        this.fragment = fragment;
        this.queries = queries;
    }

    private static Pair<String, Integer> scheme(char[] chars, int start, boolean strict) {
        String scheme = DEFAULT_PROTOCOL;
        int skip;
        if (chars[start] == '/') {//Starts with '//' i.e. authority. Use default scheme: http.
            skip = start + 2;
        } else if (chars[start] == '[') {//Maybe starts with ip-literal, compatible url.
            if (strict) {
                throw new IllegalArgumentException("No scheme and not starts with '//'");
            }
            skip = start;
        } else {
            SubstringFinder finder = new SundaySubstringFinder("://");
            Substring substring = finder.find(chars, start);
            if (substring != null) {
                skip = substring.start() + 3;
                scheme = new String(chars, start, substring.start());
            } else {
                if (strict) {
                    throw new IllegalArgumentException("No scheme and not starts with '//'");
                }
                skip = start;//It does not start with scheme and '//'. Maybe compatible url.
            }
        }
        return Pair.create(scheme, skip);
    }

    private static boolean isHttp(String scheme) {
        return HTTP.equalsIgnoreCase(scheme) || HTTPS.equalsIgnoreCase(scheme);
    }

    private static Pair<String, Integer> authority(char[] chars, int start) {
        Pair<String, Integer> a = next(chars, start, "/?#");
        if (a.getFirst().isEmpty()) {
            throw new IllegalArgumentException("No authority.");
        }
        return Pair.create(
                a.getFirst().isEmpty() ? null : a.getFirst(),
                a.getSecond());
    }

    private static Pair<String, Integer> path(char[] chars, int start, boolean strict) {
        Pair<String, Integer> p = next(chars, start, "?#");
        if (strict && !PATH_PATTERN.matcher(p.getFirst()).matches()) {
            throw new IllegalArgumentException("Invalid path.");
        }
        return p;
    }

    private static Pair<String, Integer> query(char[] chars, int start, boolean strict) {
        Pair<String, Integer> q = next(chars, start, "#");
        if (strict && q.getFirst() != null && !QUERY_AND_FRAGMENT_PATTERN.matcher(q.getFirst()).matches()) {
            throw new IllegalArgumentException("Invalid query.");
        }
        return Pair.create(
                q.getFirst().isEmpty() ? null : q.getFirst().substring(1),
                q.getSecond());
    }

    private static Pair<String, Integer> fragment(char[] chars, int start, boolean strict) {
        Pair<String, Integer> f = next(chars, start, "");
        if (strict && f.getFirst() != null && !QUERY_AND_FRAGMENT_PATTERN.matcher(f.getFirst()).matches()) {
            throw new IllegalArgumentException("Invalid fragment.");
        }
        return Pair.create(
                f.getFirst().isEmpty() ? null : f.getFirst().substring(1),
                f.getSecond());
    }

    private static Pair<String, Integer> host(char[] chars, int start) {
        invalidUserInfo(chars, start);
        if (chars[start] == '[') {
            return ipv6(chars, start);
        } else {
            return classicHost(chars, start);
        }
    }

    private static void invalidUserInfo(char[] chars, int start) {
        int atIdx = Strings.indexOf('@', chars, start, chars.length);
        if (atIdx >= 0) {
            throw new IllegalArgumentException("Invalid authority, userinfo is not allowed for http(s).");
        }
    }

    private static Pair<String, Integer> ipv6(char[] chars, int start) {
        int e = Strings.indexOf(']', chars, start, chars.length);
        if (e < 0) {
            throw new IllegalArgumentException("Invalid authority, invalid ip literal, no matching ].");
        }
        if (chars.length == e + 1 || Mask.asciiMask(":/?#").predicate().test(chars[e + 1])) {
            String ipv6 = new String(chars, start + 1, e - start - 1);
            if (IPV6ADDRESS_PATTERN.matcher(ipv6).matches()) {
                return Pair.create(ipv6, e + 1);
            } else {
                throw new IllegalArgumentException("Maybe future ip.");
            }
        } else {
            throw new IllegalArgumentException("Invalid authority, maybe ip literal.");
        }
    }

    private static Pair<String, Integer> classicHost(char[] chars, int start) {
        Pair<String, Integer> host = next(chars, start, ":");

        if (host.getFirst().isEmpty()) {
            throw new IllegalArgumentException("No host.");
        }
        if (!REGNAME_PATTERN.matcher(host.getFirst()).matches()) {
            throw new IllegalArgumentException("Invalid classic host.");
        }

        return host;
    }

    private static Pair<Integer, Integer> port(char[] chars, int start) {
        if (start == chars.length) {
            return null;
        }

        int port = -1;
        start++;
        if (start != chars.length) {
            String p = new String(chars, start, chars.length - start);
            if (!PORT_PATTERN.matcher(p).matches()) {
                throw new IllegalArgumentException("Invalid authority, port is invalid number.");
            }

            port = Integer.parseInt(p);
            if (0 > port || port >= 65536) {
                throw new IllegalArgumentException("Invalid authority, port out of range 0~65535");
            }
        }
        return Pair.create(port, chars.length);
    }

    private static Pair<String, Integer> next(char[] chars, int start, String s) {
        int e = Strings.indexOf(Mask.asciiMask(s).predicate(), chars, start, chars.length);
        if (e < 0) {
            e = chars.length;
        }
        return Pair.create(new String(chars, start, e - start), e);
    }

    public static URL create(String url) {
        return create(url, false);
    }

    public static URL create(String url, boolean strict) {
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

        Pair<String, Integer> scheme = scheme(chars, 0, strict);
        if (!isHttp(scheme.getFirst())) {
            throw new IllegalArgumentException("Scheme is not http(s).");
        }

        Pair<String, Integer> authority = authority(chars, scheme.getSecond());
        char[] authChars = authority.getFirst().toCharArray();
        Pair<String, Integer> host = host(authChars, 0);
        Pair<Integer, Integer> port = port(authChars, host.getSecond());
        Pair<String, Integer> path = path(chars, authority.getSecond(), strict);
        Pair<String, Integer> query = (chars.length == path.getSecond() || chars[path.getSecond()] == '#') ? null :
                query(chars, path.getSecond(), strict);
        Pair<String, Integer> fragment = fragment(chars, query == null ? path.getSecond() : query.getSecond(), strict);
        return new URL(
                scheme.getFirst(),
                host.getFirst(),
                port == null ? null : port.getFirst(),
                path.getFirst(),
                query == null ? null : query.getFirst(),
                fragment.getFirst(),
                Collections.emptyList());
    }

    public HttpURLConnection open() throws IOException {
        return (HttpURLConnection) new java.net.URL(toString()).openConnection();
    }

    public HttpURLConnection open(Proxy proxy) throws IOException {
        return (HttpURLConnection) new java.net.URL(toString()).openConnection(proxy);
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
        return new URL(scheme, host, port, path, query, fragment, Collections.emptyList());
    }

    public URL base() {
        return new URL(scheme, host, port, "", null, null, Collections.emptyList());
    }

    public URL truncateToPath() {
        return new URL(scheme, host, port, path, null, null, Collections.emptyList());
    }

    public URL noQuery() {
        return new URL(scheme, host, port, path, null, fragment, Collections.emptyList());
    }

    public URL dynamic() {
        if (query == null || query.isEmpty()) {
            return this;
        }

        List<Query> queries = Queries.unsafeParseQueries(query);
        return new URL(scheme, host, port, path, null, fragment, Collections.unmodifiableList(queries));
    }

    public URL appendDynamicQuery(String name, Object value) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name is required.");
        }

        String v = Optional.ofNullable(value)
                .map(Object::toString)
                .map(s -> DYNAMIC_QUERY_VALUE_ENCODER.parse(s, DEFAULT_CHARSET))
                .orElse(null);
        Query q = Query.create(DYNAMIC_QUERY_NAME_ENCODER.parse(name, DEFAULT_CHARSET), v);
        List<Query> queries = new ArrayList<>(this.queries);
        queries.add(q);
        return new URL(scheme, host, port, path, this.query, fragment, Collections.unmodifiableList(queries));
    }

    public URL appendDynamicQuery(Query query) {
        Objects.requireNonNull(query, "Query is required.");
        List<Query> queries = new ArrayList<>(this.queries);
        queries.add(query);
        return new URL(scheme, host, port, path, this.query, fragment, Collections.unmodifiableList(queries));
    }

    public URL removeDynamicQuery(String name) {
        List<Query> queries = this.queries.stream()
                .filter(q -> !(q.name(DEFAULT_CHARSET).equals(URLDecoder.decode(name, DEFAULT_CHARSET))))
                .collect(Collectors.toList());
        return new URL(scheme, host, port, path, this.query, fragment, Collections.unmodifiableList(queries));
    }

    public List<Query> getDynamicQuery(String name) {
        return this.queries.stream()
                .filter(q -> q.name(DEFAULT_CHARSET).equals(URLDecoder.decode(name, DEFAULT_CHARSET)))
                .collect(Collectors.toList());
    }

    public Query getDynamicQuery(int i) {
        if (queries == null || i < 0 || i >= queries.size()) {
            throw new IndexOutOfBoundsException("" + i);
        }
        return queries.get(i);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(scheme).append(":")
                .append("//").append(generateAuthority());
        if (path != null) {
            builder.append(path);
        }

        String[] dynamicQueries = dynamicQueries();
        if (this.query != null || dynamicQueries.length != 0) {
            builder.append('?')
                    .append(concatQuery(this.query, dynamicQueries));
        }

        if (fragment != null) {
            builder.append('#')
                    .append(fragment);
        }
        return builder.toString();
    }

    /**
     * Security case: change character to pct-encoded with utf-8.
     */
    public String normalizeString() {
        StringBuilder builder = new StringBuilder(scheme.toLowerCase()).append(":")
                .append("//");
        builder.append(generateHost(normalizeHost()));
        builder.append(generatePort(normalizePort()));
        builder.append(normalizePath());

        String[] dynamicQueries = dynamicQueries();
        if (this.query != null || dynamicQueries.length != 0) {
            builder.append('?')
                    .append(normalize(concatQuery(this.query, dynamicQueries)));
        }

        if (fragment != null) {
            builder.append('#')
                    .append(normalize(fragment));
        }
        return builder.toString();
    }

    private String normalizeHost() {
        StringBuilder builder = new StringBuilder();
        String host = this.host;
        char[] chars = host.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] != '%') {
                if (AsciiGroup.UP_ALPHA.test(chars[i])) {
                    builder.append((char) (chars[i] - 'A' + 'a'));
                } else {
                    builder.append(chars[i]);
                }
            } else {
                char c = (char) Bytes.oneFromHex(chars[i+1], chars[i+2]);
                if (URICharGroup.UNRESERVED.test(c)) {
                    if (AsciiGroup.UP_ALPHA.test(c)) {
                        builder.append((char) (c - 'A' + 'a'));
                    } else {
                        builder.append(c);
                    }
                } else {
                    builder.append(chars[i]);
                    builder.append(Character.toUpperCase(chars[i+1]));
                    builder.append(Character.toUpperCase(chars[i+2]));
                }
                i += 2;
            }
        }
        return builder.toString();
    }

    private Integer normalizePort() {
        Integer port = this.port;
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
        return port;
    }

    private String normalizePath() {
        String path = this.path;
        if (Strings.isEmpty(path)) {
            return "/";
        } else {
            return normalize(path);
        }
    }

    private String normalize(String string) {
        char[] chars = string.toCharArray();
        StringBuilder builder = new StringBuilder(chars.length);
        Predicate<Character> pct = Mask.NON_ASCII.bitOr(Mask.asciiMask("[]")).predicate();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] != '%') {
                if (pct.test(chars[i])) {
                    int j = i;
                    i++;
                    for (; i < chars.length; i++) {
                        if (!pct.test(chars[i])) {
                            break;
                        }
                    }
                    String temp = new String(chars, j, i - j);
                    byte[] bytes = temp.getBytes(DEFAULT_CHARSET);
                    for (byte b : bytes) {
                        builder.append('%').append(Bytes.hex(b));
                    }
                    i--;
                } else {
                    builder.append(chars[i]);
                }
            } else {
                char c = (char) Bytes.oneFromHex(chars[i + 1], chars[i + 2]);
                if (URICharGroup.UNRESERVED.test(c)) {
                    builder.append(c);
                } else {
                    builder.append(chars[i]);
                    builder.append(Character.toUpperCase(chars[i + 1]));
                    builder.append(Character.toUpperCase(chars[i + 2]));
                }
                i += 2;
            }
        }
        return builder.toString();
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
        StringBuilder builder = new StringBuilder();
        if (port != null) {
            builder.append(':');
            if (port >= 0) {
                builder.append(port);
            }
        }
        return builder.toString();
    }

    private String generateAuthority() {
        return generateHost(this.host) +
                generatePort(this.port);
    }

    public static final String QUERY_DELIMITER = "&";
    public static final String NV_SEPARATOR = "=";
    private static final URLEncoder DYNAMIC_QUERY_NAME_ENCODER = URLEncoder.builder()
            .setPercentEncodingReserved(true)
            .or(Mask.asciiMask("!$'()*+,;").predicate())
            .build();
    private static final URLEncoder DYNAMIC_QUERY_VALUE_ENCODER = URLEncoder.builder()
            .setPercentEncodingReserved(true)
            .or(Mask.asciiMask("!$'()*+,;=").predicate())
            .build();

    private String[] dynamicQueries() {
        List<Query> queries = this.queries;
        List<String> ret = new ArrayList<>();
        if (queries != null && !queries.isEmpty()) {
            for (Query q : queries) {
                ret.add(q.getName() +
                        NV_SEPARATOR +
                        q.getValue());
            }
        }
        return ret.toArray(new String[0]);
    }

    private String concatQuery(String query, String[] dynamicQueries) {
        String ret = "";
        if (query != null && !query.isEmpty()) {
            ret += query;
        }
        if (dynamicQueries.length != 0) {
            if (!ret.isEmpty()) {
                ret = ret + QUERY_DELIMITER;
            }
            ret = ret + String.join(QUERY_DELIMITER, dynamicQueries);
        }
        return ret;
    }

}
