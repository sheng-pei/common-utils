package ppl.common.utils.net;

import ppl.common.utils.pair.Pair;
import ppl.common.utils.string.Strings;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class HierURI {
    private static final Predicate<Character> PATH_SEPARATOR = c -> '/' == c || '?' == c || '#' == c;
    private static final Predicate<Character> QUERY_SEPARATOR = c -> '?' == c || '#' == c;
    private static final Predicate<Character> FRAGMENT_SEPARATOR = c -> '#' == c;
    private static final Pattern SCHEME_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9+.-]*$");
    private final String scheme;
    private final String authority;
    private final String path;
    private final String query;
    private final String fragment;

    public static HierURI create(String string) {
        Objects.requireNonNull(string);
        if (string.isEmpty()) {
            return new HierURI(null, null, "", null, null);
        }

        char[] chars = string.toCharArray();
        Pair<String, Integer> scheme = scheme(chars, 0);
        Pair<String, Integer> authority = authority(chars, scheme.getSecond());
        Pair<String, Integer> path = path(chars, authority.getSecond());
        Pair<String, Integer> query = query(chars, path.getSecond());
        Pair<String, Integer> fragment = fragment(chars, query.getSecond());
        return new HierURI(
                scheme.getFirst(),
                authority.getFirst(),
                path.getFirst(),
                query.getFirst(),
                fragment.getFirst());
    }

    private static Pair<String, Integer> scheme(char[] chars, int start) {
        if (chars[start] == '/' && chars.length - start >= 2 && chars[start + 1] == '/') {//Starts with '//' i.e. authority. Use default scheme: http.
            return Pair.create(null, start);
        }

        if (PATH_SEPARATOR.test(chars[start])) {
            return Pair.create(null, start);
        }

        String scheme = null;
        int next = start;
        int idx = Strings.indexOf(':', chars, start, chars.length);
        if (idx >= 0 && idx < chars.length - 1) {
            scheme = new String(chars, start, idx - start);
            if (SCHEME_PATTERN.matcher(scheme).matches()) {
                next = idx + 1;
            } else {
                scheme = null;
            }
        }
        return Pair.create(scheme, next);
    }

    private static Pair<String, Integer> authority(char[] chars, int start) {
        if (chars.length - start < 2 || chars[start] != '/' || chars[start + 1] != '/') {
            return Pair.create(null, start);
        }

        start += 2;
        int idx = Strings.indexOf(PATH_SEPARATOR, chars, start, chars.length);
        if (idx < 0) {
            idx = chars.length;
        }
        return Pair.create(new String(chars, start, idx - start), idx);
    }

    private static Pair<String, Integer> path(char[] chars, int start) {
        if (start == chars.length || QUERY_SEPARATOR.test(chars[start])) {
            return Pair.create("", start);
        }

        int idx = Strings.indexOf(QUERY_SEPARATOR, chars, start, chars.length);
        if (idx < 0) {
            idx = chars.length;
        }
        return Pair.create(new String(chars, start, idx - start), idx);
    }

    private static Pair<String, Integer> query(char[] chars, int start) {
        if (start == chars.length || FRAGMENT_SEPARATOR.test(chars[start])) {
            return Pair.create(null, start);
        }

        start = start + 1;
        int idx = Strings.indexOf(FRAGMENT_SEPARATOR, chars, start, chars.length);
        if (idx < 0) {
            idx = chars.length;
        }
        return Pair.create(new String(chars, start, idx - start), idx);
    }

    private static Pair<String, Integer> fragment(char[] chars, int start) {
        if (start == chars.length) {
            return Pair.create(null, start);
        }

        start += 1;
        return Pair.create(new String(chars, start, chars.length - start), chars.length);
    }

    private HierURI(String scheme, String authority, String path, String query, String fragment) {
        this.scheme = scheme;
        this.authority = authority;
        this.path = path;
        this.query = query;
        this.fragment = fragment;
    }

    public String getScheme() {
        return scheme;
    }

    public String getAuthority() {
        return authority;
    }

    public String getPath() {
        return path;
    }

    public String getQuery() {
        return query;
    }

    public String getFragment() {
        return fragment;
    }

    public boolean isAbsolute() {
        return scheme != null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(32);
        if (scheme != null) {
            builder.append(scheme).append(":");
        }
        if (authority != null) {
            builder.append("//").append(authority);
        }
        builder.append(path);
        if (query != null) {
            builder.append("?").append(query);
        }
        if (fragment != null) {
            builder.append("#").append(fragment);
        }
        return builder.toString();
    }
}
