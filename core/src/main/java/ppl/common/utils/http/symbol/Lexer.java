package ppl.common.utils.http.symbol;

import java.util.Objects;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Lexer {

    // @formatter:off
    private static final Pattern INTERNAL_HEADER_PATTERN = Pattern.compile(
            "\\([A-Za-z](?:[A-Za-z0-9-]*[A-Za-z0-9])?\\)");
    private static final Pattern CLASSIC_HEADER_PATTERN = Pattern.compile(
            "[A-Za-z][A-Za-z0-9-]*");

    //RFC 7230
    private static final Pattern FIELD_CONTENT_PATTERN = Pattern.compile(
            "[" +
                    HttpCharGroup.FIELD_VCHAR.mask().patternString() +
            "](?:[" +
                    HttpCharGroup.WHITESPACE.mask().patternString() +
            "]+[" +
                    HttpCharGroup.FIELD_VCHAR.mask().patternString() +
            "])?");

    private static final Pattern STRICT_OBS_FOLD = Pattern.compile(
            "\r\n[" +
                    HttpCharGroup.WHITESPACE.mask().patternString() +
            "]");
    private static final Pattern LINE_FEED_OBS_FOLD = Pattern.compile(
            "\n[" +
                    HttpCharGroup.WHITESPACE.mask().patternString() +
            "]");
    private static final Pattern CARRIAGE_RETURN_OBS_FOLD = Pattern.compile(
            "\r[" +
                    HttpCharGroup.WHITESPACE.mask().patternString() +
            "]");

    private static final Pattern STRICT_FOLD_FIELD_VALUE_PATTERN = Pattern.compile(
            "(?:" +
                    FIELD_CONTENT_PATTERN.pattern() +
                        "|" +
                    STRICT_OBS_FOLD.pattern() +
            ")*");
    private static final Pattern LINE_FEED_FOLD_FIELD_VALUE_PATTERN = Pattern.compile(
            "(?:" +
                    FIELD_CONTENT_PATTERN.pattern() +
                        "|" +
                    LINE_FEED_OBS_FOLD.pattern() +
            ")*");
    private static final Pattern CARRIAGE_RETURN_FOLD_FIELD_VALUE_PATTERN = Pattern.compile(
            "(?:" +
                    FIELD_CONTENT_PATTERN.pattern() +
                        "|" +
                    CARRIAGE_RETURN_OBS_FOLD.pattern() +
            ")*");
    private static final Pattern SINGLE_LINE_FIELD_VALUE_PATTERN = Pattern.compile(
            "(?:" +
                    FIELD_CONTENT_PATTERN.pattern() +
            ")*");

    //RFC 7230
    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            "[" + HttpCharGroup.TCHAR.mask().patternString() + "]+");

    //RFC 7231
    private static final Pattern PRODUCT_PATTERN = Pattern.compile(TOKEN_PATTERN.pattern() +
            "(?:/" +
                    TOKEN_PATTERN.pattern() +
            ")?");

    //RFC 7230
    private static final Pattern REQUIRED_WHITESPACE_PATTERN = Pattern.compile(
            "[" +
                    HttpCharGroup.WHITESPACE.mask().patternString() +
            "]+");
    //RFC 7230
    private static final Pattern OPTIONAL_WHITESPACE_PATTERN = Pattern.compile(
            "[" +
                    HttpCharGroup.WHITESPACE.mask().patternString() +
            "]*");

    //RFC 7320
    private static final Pattern FIELD_NAME_PATTERN = TOKEN_PATTERN;
    //RFC 7230
    private static final Pattern FIELD_VALUE_PATTERN = STRICT_FOLD_FIELD_VALUE_PATTERN;
    //RFC 7230
    private static final Pattern HEADER_FIELD_PATTERN = Pattern.compile(
            "(?:" +
                    FIELD_NAME_PATTERN.pattern() +
            "):(?:" +
                    OPTIONAL_WHITESPACE_PATTERN.pattern() +
            ")(?:" +
                    FIELD_VALUE_PATTERN.pattern() +
            ")(?:" +
                    OPTIONAL_WHITESPACE_PATTERN.pattern() +
            ")");
    //RFC 7230
    private static final Pattern QUOTED_STRING_PATTERN = Pattern.compile(
            "\"(?:[" +
                    HttpCharGroup.QDTEXT.mask().patternString() +
            "]|\\\\[" +
                    HttpCharGroup.QDPTEXT.mask().patternString() +
            "])*\"");
    //RFC 7230
    private static final Pattern QUOTED_PAIR_PATTERN = Pattern.compile(
            "\\\\[" +
                    HttpCharGroup.QDPTEXT.mask().patternString() +
            "]");
    //RFC 7230
    private static final Pattern QDPTEXT_PATTERN = Pattern.compile(
            "[" +
                    HttpCharGroup.QDPTEXT.mask().patternString() +
            "]*");
    //RFC 2046
    private static final Pattern BOUNDARY_PATTERN = Pattern.compile(
            "[" +
                HttpCharGroup.BCHARS.mask().patternString() +
            "]{0,69}[" +
                HttpCharGroup.BCHARS_NO_SPACE.mask().patternString() +
            "]");
    // @formatter:on

    private Lexer() {
    }

    public static boolean isBoundary(String string) {
        Objects.requireNonNull(string);
        return BOUNDARY_PATTERN.matcher(string).matches();
    }

    public static boolean isRequiredWhitespace(String string) {
        Objects.requireNonNull(string);
        return REQUIRED_WHITESPACE_PATTERN.matcher(string).matches();
    }

    private static final Pattern STARTS_WITH_REQUIRED_WHITESPACE_PATTERN = Pattern.compile(
            "^" + REQUIRED_WHITESPACE_PATTERN.pattern());

    public static String extractRequiredWhitespace(String string, int begin) {
        Objects.requireNonNull(string);
        checkIndex(string, begin);
        Matcher matcher = STARTS_WITH_REQUIRED_WHITESPACE_PATTERN.matcher(string.substring(begin));
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public static boolean isPseudoHeader(String string) {
        Objects.requireNonNull(string);
        return string.startsWith(":") && isToken(string.substring(1));
    }

    public static boolean isInternalHeader(String string) {
        Objects.requireNonNull(string);
        return INTERNAL_HEADER_PATTERN.matcher(string).matches();
    }

    public static boolean isClassicHeader(String string) {
        Objects.requireNonNull(string);
        return CLASSIC_HEADER_PATTERN.matcher(string).matches();
    }

    public static boolean isToken(String string) {
        Objects.requireNonNull(string);
        return TOKEN_PATTERN.matcher(string).matches();
    }

    public static boolean isStrictFoldableFieldValue(String string) {
        Objects.requireNonNull(string);
        return STRICT_FOLD_FIELD_VALUE_PATTERN.matcher(string).matches();
    }

    public static boolean isLineFeedFoldableFieldValue(String string) {
        Objects.requireNonNull(string);
        return LINE_FEED_FOLD_FIELD_VALUE_PATTERN.matcher(string).matches();
    }

    public static boolean isCarriageReturnFoldableFieldValue(String string) {
        Objects.requireNonNull(string);
        return CARRIAGE_RETURN_FOLD_FIELD_VALUE_PATTERN.matcher(string).matches();
    }

    public static boolean isSingleLineFieldValue(String string) {
        Objects.requireNonNull(string);
        return SINGLE_LINE_FIELD_VALUE_PATTERN.matcher(string).matches();
    }

    public static boolean isQuotedString(String string) {
        Objects.requireNonNull(string);
        return QUOTED_STRING_PATTERN.matcher(string).matches();
    }

    public static boolean isQuotedPairText(String string) {
        Objects.requireNonNull(string);
        return QDPTEXT_PATTERN.matcher(string).matches();
    }

    private static final Predicate<Character> NEED_TO_PAIR_QUOTE = HttpCharGroup.QDPTEXT
            .and(HttpCharGroup.QDTEXT.negate());

    public static String readValue(String string) {
        if (string == null) {
            return null;
        }
        if (Lexer.isToken(string)) {
            return string;
        }
        if (Lexer.isQuotedString(string)) {
            return eraseQuotedString(string);
        }
        throw new IllegalArgumentException("Not token or quoted-string.");
    }

    public static String writeValue(String string) {
        if (string == null) {
            return null;
        }
        if (Lexer.isToken(string)) {
            return string;
        }
        if (Lexer.isQuotedPairText(string)) {
            return quoteString(string);
        }
        throw new IllegalArgumentException("Couldn't convert to token or quoted-string.");
    }

    public static String quoteString(String string) {
        Objects.requireNonNull(string);
        StringBuilder builder = new StringBuilder(string.length());
        builder.append('"');
        char[] chars = string.toCharArray();
        for (char c : chars) {
            if (!HttpCharGroup.QDPTEXT.test(c)) {
                throw new IllegalArgumentException("Some characters are unquoted.");
            }

            if (NEED_TO_PAIR_QUOTE.test(c)) {
                builder.append('\\');
            }
            builder.append(c);
        }
        builder.append('"');
        return builder.toString();
    }

    public static String eraseQuotedString(String string) {
        Objects.requireNonNull(string);
        if (string.length() >= 2 && HttpCharGroup.DQUOTE.test(string.charAt(0)) && HttpCharGroup.DQUOTE.test(string.charAt(string.length() - 1))) {
            return eraseQuotedPair(string, 1, string.length() - 1);
        }
        throw new IllegalArgumentException("Not quoted string.");
    }

    private static String eraseQuotedPair(String string, int begin, int end) {
        StringBuilder builder = new StringBuilder(string.length());
        char[] chars = string.toCharArray();
        for (int i = begin; i < end; i++) {
            char c = chars[i];
            if (HttpCharGroup.BS.test(c)) {
                int next = i + 1;
                if (next >= end) {
                    throw new IllegalArgumentException("Incomplete quoted pair.");
                }
                if (!HttpCharGroup.QDPTEXT.test(chars[next])) {
                    throw new IllegalArgumentException("Error quoted pair.");
                }
                builder.append(chars[next]);
                i = next;
            } else if (HttpCharGroup.QDTEXT.test(chars[i])) {
                builder.append(chars[i]);
            } else {
                throw new IllegalArgumentException("Invalid quoted string character.");
            }
        }
        return builder.toString();
    }

    public static boolean isProduct(String string) {
        Objects.requireNonNull(string);
        return PRODUCT_PATTERN.matcher(string).matches();
    }

    private static final Pattern STARTS_WITH_PRODUCT_PATTERN = Pattern.compile("^" + PRODUCT_PATTERN.pattern());

    public static boolean startWithProduct(String string) {
        Objects.requireNonNull(string);
        return STARTS_WITH_PRODUCT_PATTERN.matcher(string).find();
    }

    public static String extractProduct(String string, int begin) {
        Objects.requireNonNull(string, "Couldn't extract product from null.");
        checkIndex(string, begin);
        Matcher matcher = STARTS_WITH_PRODUCT_PATTERN.matcher(string.substring(begin));
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public static boolean isComment(String string) {
        Objects.requireNonNull(string);
        if (string.isEmpty()) {
            return false;
        }

        if (!isCommentStart(string, 0)) {
            return false;
        }

        try {
            return string.length() == endOfComment(string, 0);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean startsWithComment(String string) {
        Objects.requireNonNull(string);
        try {
            extractComment(string, 0);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public static String extractComment(String string, int begin) {
        Objects.requireNonNull(string, "Couldn't extract comment from null.");
        checkIndex(string, begin);
        try {
            if (begin != string.length() && isCommentStart(string, begin)) {
                return string.substring(begin, endOfComment(string, begin));
            }
        } catch (RuntimeException ignored) {
        }
        return null;
    }

    private static boolean isCommentStart(String string, int begin) {
        return HttpCharGroup.L_COMMENT.test(string.charAt(begin));
    }

    private static int endOfComment(String string, int begin) {
        Stack<Character> stack = new Stack<>();
        char[] chars = string.toCharArray();
        boolean quoted = false;
        int i = begin;
        for (; i < string.length(); i++) {
            char c = chars[i];
            if ('(' == c) {
                if (!quoted) {
                    stack.push(c);
                } else {
                    quoted = false;
                }
            } else if (')' == c) {
                if (!quoted) {
                    stack.pop();
                    if (stack.isEmpty()) {
                        break;
                    }
                } else {
                    quoted = false;
                }
            } else if ('\\' == c) {
                quoted = !quoted;
            } else {
                if (quoted) {
                    if (!HttpCharGroup.QDPTEXT.test(c)) {
                        throw new IllegalArgumentException("Invalid quoted pair in comment.");
                    }
                    quoted = false;
                } else {
                    if (!HttpCharGroup.CTEXT.test(c)) {
                        throw new IllegalArgumentException("Invalid text in comment.");
                    }
                }
            }
        }

        if (!stack.isEmpty()) {
            throw new IllegalArgumentException("Incomplete comment.");
        }
        return i + 1;
    }

    private static void checkIndex(String string, int idx) {
        if (idx < 0 || idx > string.length()) {
            throw new StringIndexOutOfBoundsException("Out of range: " + idx + ".");
        }
    }

}
