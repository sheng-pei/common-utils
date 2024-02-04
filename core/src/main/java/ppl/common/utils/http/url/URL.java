package ppl.common.utils.http.url;

import ppl.common.utils.character.ascii.Mask;
import ppl.common.utils.net.URLEncoder;
import ppl.common.utils.string.Strings;
import ppl.common.utils.string.kvpair.Pair;
import ppl.common.utils.string.substring.Substring;
import ppl.common.utils.string.substring.SubstringFinder;
import ppl.common.utils.string.substring.impl.SundaySubstringFinder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Standard or compatible form of http(s) url like 'host[:8888][/path][?query][#fragment]'.
 */
public class URL {

    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static final String DEFAULT_PROTOCOL = HTTP;
    private static final Pattern PORT_PATTERN = Pattern.compile("[0-9]+");
    private static final Pattern REGNAME_PATTERN = Pattern.compile(
            "(?:[a-zA-Z0-9_.~!$&'()*+,;=-]|%[0-9a-fA-F]{2}|[^\\00-\\0177])*");
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
    private static final Pattern PATH_PATTERN = Pattern.compile("(?:/(?:[a-zA-Z0-9_.~!$&'()*+,;=:@-]|[^\\00-\\0177]|%[0-9a-fA-F]{2})*)*");
    private static final Pattern QUERY_AND_FRAGMENT_PATTERN = Pattern.compile("(?:[a-zA-Z0-9_.~!$&'()*+,;=:@/?-]|[^\\00-\\0177]|%[0-9a-fA-F]{2})*");

    private final String scheme;
    private final String host;
    private final int port;
    private final String path;
    private final String query;
    private final String fragment;
    private final List<Query> queries;

    private URL(String scheme, String host, int port, String path, String query, String fragment, List<Query> queries) {
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.path = path;
        this.query = query;
        this.fragment = fragment;
        this.queries = queries;
    }

    private static Pair<String, Integer> scheme(char[] chars, int start) {
        String scheme = DEFAULT_PROTOCOL;
        int skip;
        if (chars[start] == '/') {//Starts with '//' i.e. authority. Use default scheme: http.
            skip = start + 2;
        } else if (chars[start] == '[') {//Maybe starts with ip-literal, incompatible url.
            skip = start;
        } else {
            SubstringFinder finder = new SundaySubstringFinder("://");
            Substring substring = finder.find(chars, start);
            if (substring != null) {
                skip = substring.start() + 3;
                scheme = new String(chars, start, substring.start());
            } else {
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

    private static Pair<String, Integer> path(char[] chars, int start) {
        Pair<String, Integer> p = next(chars, start, "?#");
        if (!PATH_PATTERN.matcher(p.getFirst()).matches()) {
            throw new IllegalArgumentException("Invalid path.");
        }
        return p;
    }

    private static Pair<String, Integer> query(char[] chars, int start) {
        Pair<String, Integer> q = next(chars, start, "#");
        if (q.getFirst() != null && !QUERY_AND_FRAGMENT_PATTERN.matcher(q.getFirst()).matches()) {
            throw new IllegalArgumentException("Invalid query.");
        }
        return Pair.create(
                q.getFirst().isEmpty() ? null : q.getFirst().substring(1),
                q.getSecond());
    }

    private static Pair<String, Integer> fragment(char[] chars, int start) {
        Pair<String, Integer> f = next(chars, start, "");
        if (f.getFirst() != null && !QUERY_AND_FRAGMENT_PATTERN.matcher(f.getFirst()).matches()) {
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
            throw new IllegalArgumentException("Invalid authority, userinfo is not allowed.");
        }
    }

    private static Pair<String, Integer> ipv6(char[] chars, int start) {
        int e = Strings.indexOf(']', chars, start, chars.length);
        if (e < 0) {
            throw new IllegalArgumentException("Invalid authority, invalid ip literal, no matching ].");
        }
        if (chars.length == e + 1 || Mask.mask(":/?#").predicate().test(chars[e + 1])) {
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
        int port = -1;
        if (start != chars.length) {
            start++;
            if (start == chars.length) {
                throw new IllegalArgumentException("Invalid authority, empty port is not allowed when a colon is given.");
            }

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
        int e = Strings.indexOf(Mask.mask(s).predicate(), chars, start, chars.length);
        if (e < 0) {
            e = chars.length;
        }
        return Pair.create(new String(chars, start, e - start), e);
    }

    public static URL create(String url) {
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

        Pair<String, Integer> scheme = scheme(chars, 0);
        if (!isHttp(scheme.getFirst())) {
            throw new IllegalArgumentException("Scheme is not http(s).");
        }

        Pair<String, Integer> authority = authority(chars, scheme.getSecond());
        char[] authChars = authority.getFirst().toCharArray();
        Pair<String, Integer> host = host(authChars, 0);
        Pair<Integer, Integer> port = port(authChars, host.getSecond());
        Pair<String, Integer> path = path(chars, authority.getSecond());
        Pair<String, Integer> query = query(chars, path.getSecond());
        Pair<String, Integer> fragment = fragment(chars, query.getSecond());
        return new URL(
                scheme.getFirst(),
                host.getFirst(),
                port.getFirst(),
                path.getFirst(),
                query.getFirst(),
                fragment.getFirst(),
                Collections.emptyList());
    }

    public HttpURLConnection open() throws IOException {
        return (HttpURLConnection) new java.net.URL(toString()).openConnection();
    }

    public HttpURLConnection open(Proxy proxy) throws IOException {
        return (HttpURLConnection) new java.net.URL(toString()).openConnection(proxy);
    }

    public URL base() {
        return new URL(scheme, host, port, "", null, null, Collections.emptyList());
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

    public URL appendQuery(String name, String value) {
        Query q = Query.create(name, value);
        checkQuery(q);
        List<Query> queries = new ArrayList<>(this.queries);
        queries.add(q);
        return new URL(scheme, host, port, path, this.query, fragment, Collections.unmodifiableList(queries));
    }

    public URL replaceQuery(int i, Query query) {
        checkQuery(query);
        List<Query> queries = new ArrayList<>(this.queries);
        queries.set(i, query);
        return new URL(scheme, host, port, path, this.query, fragment, Collections.unmodifiableList(queries));
    }

    private void checkQuery(Query query) {
        String qStr = query.toString();
        if (!QUERY_AND_FRAGMENT_PATTERN.matcher(qStr).matches()) {
            throw new IllegalArgumentException("Invalid query: '" + qStr + "'.");
        }
    }

    public Query getQuery(int i) {
        if (queries == null || i < 0 || i >= queries.size()) {
            throw new IndexOutOfBoundsException("" + i);
        }
        return queries.get(i);
    }

    public URL clearQuery() {
        return new URL(scheme, host, port, path, null, fragment, Collections.emptyList());
    }

    public URL rawUrl() {
        return new URL(scheme, host, port, path, query, fragment, Collections.emptyList());
    }

    @Override
    public String toString() {
        URLEncoder encoder = URLEncoder.builder()
                .setPercentEncodingReserved(true)
                .or(Predicate.<Character>isEqual('&').negate())
                .build();
        StringBuilder builder = new StringBuilder(scheme).append(":")
                .append("//").append(generateAuthority());
                if (path != null) {
                    builder.append(path);
                }
        String query = appendToUrlQuery(concatAppendQueries(encoder));
        if (query != null) {
            builder.append('?').append(query);
        }
        if (fragment != null) {
            builder.append('#').append(fragment);
        }
        return builder.toString();
    }

    private String generateAuthority() {
        StringBuilder builder = new StringBuilder();
        if (host.contains(":")) {
            builder.append('[').append(host).append(']');
        } else {
            builder.append(host);
        }
        if (port >= 0) {
            builder.append(':').append(port);
        }
        return builder.toString();
    }

    private String concatAppendQueries(URLEncoder encoder) {
        StringBuilder appendQueries = new StringBuilder();
        List<Query> queries = this.queries;
        if (queries != null && !queries.isEmpty()) {
            for (Query q : queries) {
                appendQueries.append(encoder.parse(q.toString())).append('&');
            }
            appendQueries.setLength(appendQueries.length() - 1);
        }
        return appendQueries.toString();
    }

    private String appendToUrlQuery(String appendQueries) {
        String query = this.query;
        if (query != null && !query.isEmpty()) {
            if (!appendQueries.isEmpty()) {
                query = query + '&' + appendQueries;
            }
        } else {
            if (!appendQueries.isEmpty()) {
                query = appendQueries;
            }
        }
        return query;
    }

}