package ppl.common.utils.http.symbol;

import ppl.common.utils.character.ascii.Mask;
import ppl.common.utils.string.Strings;

import java.util.Objects;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Lexer {

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("[ \t]*");
    private static final Pattern LINE_PATTERN = Pattern.compile("\r|\n|\r\n");
    private static final Pattern INTERNAL_HEADER_PATTERN = Pattern.compile(
            "\\([A-Za-z](?:[A-Za-z0-9-]*[A-Za-z0-9])?\\)");
    private static final Pattern CLASSIC_HEADER_PATTERN = Pattern.compile(
            "[A-Za-z][A-Za-z0-9-]*");
    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            "[A-Za-z0-9!#$%&'*+.^_`|~-]+");
    private static final Pattern STRICT_FOLD_FIELD_VALUE_PATTERN = Pattern.compile(
            "[\\041-\\0176\\0200-\\0377](?:(?:[\\041-\\0176\\0200-\\0377]|(?:\\r\\n)?[ \\t]+)*[\\041-\\0176\\0200-\\0377])?");
    private static final Pattern LINE_FEED_FOLD_FIELD_VALUE_PATTERN = Pattern.compile(
            "[\\041-\\0176\\0200-\\0377](?:(?:[\\041-\\0176\\0200-\\0377]|\\n?[ \\t]+)*[\\041-\\0176\\0200-\\0377])?");
    private static final Pattern CARRIAGE_RETURN_FOLD_FIELD_VALUE_PATTERN = Pattern.compile(
            "[\\041-\\0176\\0200-\\0377](?:(?:[\\041-\\0176\\0200-\\0377]|\\r?[ \\t]+)*[\\041-\\0176\\0200-\\0377])?");
    private static final Pattern SINGLE_LINE_FIELD_VALUE_PATTERN = Pattern.compile(
            "[\\041-\\0176\\0200-\\0377](?:[ \\t\\041-\\0176\\0200-\\0377]*[\\041-\\0176\\0200-\\0377])?");
    private static final Pattern QUOTED_STRING_PATTERN = Pattern.compile(
            "\"(?:[ \\t\\041\\043-\\0133\\0135-\\0176\\0200-\\0377]|" +
                    "\\\\[ \\t\\041-\\0176\\0200-\\0377])*\"");
    private static final Pattern PRODUCT_PATTERN = Pattern.compile("[A-Za-z0-9!#$%&'*+.^_`|~-]+(?:/[A-Za-z0-9!#$%&'*+.^_`|~-]+)?");
    private static final Pattern CTEXT_PATTERN = Pattern.compile("[ \\t\\041-\\0047\\052-\\0133\\0135-\\0176\\0200-\\0377]");
    private static final Pattern QUOTED_PAIR = Pattern.compile("\\\\([ \\t\\041-\\0176\\0200-\\0377])");

    private Lexer() {
    }

    public static boolean isWhitespace(String string) {
        if (string == null) {
            return false;
        }

        return WHITESPACE_PATTERN.matcher(string).matches();
    }

    public static boolean isLine(String string) {
        if (string == null) {
            return false;
        }

        return LINE_PATTERN.matcher(string).matches();
    }

    public static boolean isPseudoHeader(String string) {
        return string.startsWith(":") && isToken(string.substring(1));
    }

    public static boolean isInternalHeader(String string) {
        if (string == null) {
            return false;
        }

        return INTERNAL_HEADER_PATTERN.matcher(string).matches();
    }

    public static boolean isClassicHeader(String string) {
        if (string == null) {
            return false;
        }

        return CLASSIC_HEADER_PATTERN.matcher(string).matches();
    }

    public static boolean isToken(String string) {
        if (string == null) {
            return false;
        }

        return TOKEN_PATTERN.matcher(string).matches();
    }

    public static boolean isStrictFoldableFieldValue(String string) {
        if (string == null) {
            return false;
        }

        return STRICT_FOLD_FIELD_VALUE_PATTERN.matcher(string).matches();
    }

    public static boolean isLineFeedFoldableFieldValue(String string) {
        if (string == null) {
            return false;
        }

        return LINE_FEED_FOLD_FIELD_VALUE_PATTERN.matcher(string).matches();
    }

    public static boolean isCarriageReturnFoldableFieldValue(String string) {
        if (string == null) {
            return false;
        }

        return CARRIAGE_RETURN_FOLD_FIELD_VALUE_PATTERN.matcher(string).matches();
    }

    public static boolean isSingleLineFieldValue(String string) {
        if (string == null) {
            return false;
        }

        return SINGLE_LINE_FIELD_VALUE_PATTERN.matcher(string).matches();
    }

    public static boolean isQuotedString(String string) {
        if (string == null) {
            return false;
        }

        return QUOTED_STRING_PATTERN.matcher(string).matches();
    }

    public static boolean isProduct(String string) {
        if (string == null) {
            return false;
        }

        return PRODUCT_PATTERN.matcher(string).matches();
    }

    public static boolean isComment(String string) {
        if (string == null || string.isEmpty()) {
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

    public static String quoteString(String string) {
        Predicate<Character> predicate = HttpCharGroup.TOKEN.negate()
                .and(HttpCharGroup.VCHAR)
                .or(HttpCharGroup.SP)
                .or(HttpCharGroup.HT)
                .or(Mask.NON_ASCII.bitAnd(Mask.OCTET).predicate());
        int idx = Strings.indexOf(predicate, string);
        if (idx >= 0) {
            StringBuilder builder = new StringBuilder(string.length());
            builder.append('"');
            char[] chars = string.toCharArray();
            for (char c : chars) {
                if (HttpCharGroup.QM.or(HttpCharGroup.BS).test(c)) {
                    builder.append('\\');
                }
                builder.append(c);
            }
            builder.append('"');
            string = builder.toString();
        }
        return string;
    }

    public static String eraseQuotedPair(String string) {
        if (string == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        int start = 0;
        Matcher matcher = QUOTED_PAIR.matcher(string);
        while (matcher.find()) {
            builder.append(string, start, matcher.start())
                    .append(matcher.group(1));
            start = matcher.end();
        }
        builder.append(string, start, string.length());
        return builder.toString();
    }

    public static String extractProduct(String string, int begin) {
        Objects.requireNonNull(string, "Couldn't extract product from null.");
        checkIndex(string, begin);

        if (begin == string.length()) {
            return "";
        }

        int wsIdx = Strings.indexOf(HttpCharGroup.WS.or(HttpCharGroup.L_COMMENT), string, begin, string.length());
        if (wsIdx == begin) {
            return "";
        }

        if (wsIdx < 0) {
            wsIdx = string.length();
        }
        String product = string.substring(begin, wsIdx);
        if (!isProduct(product)) {
            throw new RuntimeException("Invalid product: '" + product + "'.");
        }
        return product;
    }

    public static String extractComment(String string, int begin) {
        Objects.requireNonNull(string, "Couldn't extract comment from null.");
        checkIndex(string, begin);

        if (begin == string.length()) {
            return "";
        }

        if (!isCommentStart(string, begin)) {
            return "";
        }

        return string.substring(begin, endOfComment(string, begin));
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
                    if (!HttpCharGroup.QUOTED_TEXT.test(c)) {
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
