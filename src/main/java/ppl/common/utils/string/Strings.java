package ppl.common.utils.string;

import ppl.common.utils.string.substring.PositionalArguments;
import ppl.common.utils.string.substring.impl.SundaySubstringFinder;
import ppl.common.utils.string.substring.impl.ToStringArguments;
import ppl.common.utils.string.substring.Substring;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Strings {

	private Strings() { }

	public static final String[] EMPTY_STRING_ARRAY = new String[0];

	public static String join(String delimiter, String... strings) {
		return unsafeJoin(delimiter, strings, 0, strings.length);
	}

	public static String join(String delimiter, String[] strings, int offset, int length) {
		if (offset < 0 || length < 0) {
			throw new IllegalArgumentException("Offset and length must not be negative.");
		}
		if (offset + length > strings.length) {
			throw new IllegalArgumentException(Strings.format(
					"End position is beyond length: {}.", strings.length));
		}
		return unsafeJoin(delimiter, strings, offset, length);
	}

	private static String unsafeJoin(String delimiter, String[] strings, int offset, int length) {
		StringJoiner joiner = new StringJoiner(delimiter);
		for (int i = offset; i < offset + length; i++) {
			joiner.add(strings[i]);
		}
		return joiner.toString();
	}

	public static String[] split(String string, String regex) {
		Objects.requireNonNull(regex, "The specified regex is null");

		if (string == null) {
			return EMPTY_STRING_ARRAY;
		}

		List<String> accumulator = new ArrayList<>();
		Matcher matcher = Pattern.compile(regex).matcher(string);
		int eatenLength = 0;
		int next = 0;
		while(next < string.length() && matcher.find(next)) {
			if (empty(matcher)) {
				if (!emptyBeforeMatcher(matcher, eatenLength)) {
					accumulator.add(prefixBeforeMatcher(matcher, string, eatenLength));
					eatenLength = matcher.start();
				}
				next = eatenLength + 1;
			} else {
				accumulator.add(prefixBeforeMatcher(matcher, string, eatenLength));
				eatenLength = next = matcher.end();
			}
		}

		if (eatenLength < string.length()) {
			accumulator.add(string.substring(eatenLength));
		}
		return accumulator.toArray(EMPTY_STRING_ARRAY);
	}

	private static boolean emptyBeforeMatcher(Matcher matcher, int start) {
		return matcher.start() == start;
	}

	private static String prefixBeforeMatcher(Matcher matcher, String string, int start) {
		return string.substring(start, matcher.start());
	}

	private static boolean empty(Matcher matcher) {
		return matcher.end() == matcher.start();
	}

	public static String[] removeDuplicate(String[] strings) {
		return Optional.ofNullable(strings)
				.map(Arrays::stream)
				.map(stream -> stream
						.distinct()
						.toArray(String[]::new)
				)
				.orElse(EMPTY_STRING_ARRAY);
	}

	public static boolean isEmpty(String string) {
		return string == null || string.length() == 0;
	}

	public static boolean isNotEmpty(String string) {
		return !isEmpty(string);
	}

	public static boolean isBlank(String string) {
		if (isEmpty(string)) {
			return true;
		}
		for (int i = 0; i < string.length(); i++) {
			if (!Character.isWhitespace(string.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotBlank(String string) {
		return !isBlank(string);
	}

	public static boolean equals(final CharSequence cs1, final CharSequence cs2) {
		if (cs1 == cs2) {
			return true;
		}
		if (cs1 == null || cs2 == null) {
			return false;
		}
		if (cs1.length() != cs2.length()) {
			return false;
		}
		if (cs1 instanceof String && cs2 instanceof String) {
			return cs1.equals(cs2);
		}

		final int length = cs1.length();
		for (int i = 0; i < length; i++) {
			if (cs1.charAt(i) != cs2.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	public static String toSnakeCase(String string) {
		if (isEmpty(string)) {
			return string;
		}

		StringBuilder resultBuilder = new StringBuilder(string.length());
		Character previousCharacter = null;
		for (char currentCharacter : string.toCharArray()) {
			if (needUnderscore(previousCharacter, currentCharacter)) {
				resultBuilder.append("_");
			}
			resultBuilder.append(Character.toLowerCase(currentCharacter));
			previousCharacter = currentCharacter;
		}
		return resultBuilder.toString();
	}

	private static boolean needUnderscore(Character previous, Character current) {
		return previous != null && Character.isLowerCase(previous) && Character.isUpperCase(current);
	}

	public static int lastIndexOfNot(char c, char[] chars, int begin, int end) {
		if (begin < 0 || begin > chars.length) {
			throw new ArrayIndexOutOfBoundsException(begin);
		}
		if (end < 0 || end > chars.length) {
			throw new ArrayIndexOutOfBoundsException(end);
		}
		return unsafeLastIndexOfNot(c, chars, begin, end);
	}

	public static int lastIndexOfNot(char c, String string, int begin, int end) {
		if (begin < 0 || begin > string.length()) {
			throw new StringIndexOutOfBoundsException(begin);
		}
		if (end < 0 || end > string.length()) {
			throw new StringIndexOutOfBoundsException(end);
		}
		return unsafeLastIndexOfNot(c, string.toCharArray(), begin, end);
	}

	public static int lastIndexOfNot(char c, String string) {
		return unsafeLastIndexOfNot(c, string.toCharArray(), 0, string.length());
	}

	private static int unsafeLastIndexOfNot(char c, char[] chars, int begin, int end) {
		for (int idx = end - 1; idx > begin - 1; idx--) {
			if (chars[idx] != c) {
				return idx;
			}
		}
		return -1;
	}

	public static int lastIndexOf(char c, char[] chars, int begin, int end) {
		if (begin < 0 || begin > chars.length) {
			throw new ArrayIndexOutOfBoundsException(begin);
		}
		if (end < 0 || end > chars.length) {
			throw new ArrayIndexOutOfBoundsException(end);
		}
		return unsafeLastIndexOf(c, chars, begin, end);
	}

	public static int lastIndexOf(char c, String string, int begin, int end) {
		if (begin < 0 || begin > string.length()) {
			throw new StringIndexOutOfBoundsException(begin);
		}
		if (end < 0 || end > string.length()) {
			throw new StringIndexOutOfBoundsException(end);
		}
		return unsafeLastIndexOf(c, string.toCharArray(), begin, end);
	}

	public static int lastIndexOf(char c, String string) {
		return unsafeLastIndexOf(c, string.toCharArray(), 0, string.length());
	}

	private static int unsafeLastIndexOf(char c, char[] chars, int begin, int end) {
		for (int idx = end - 1; idx > begin - 1; idx--) {
			if (chars[idx] == c) {
				return idx;
			}
		}
		return -1;
	}

	public static int indexOfNot(char c, char[] chars, int begin, int end) {
		if (begin < 0 || begin > chars.length) {
			throw new ArrayIndexOutOfBoundsException(begin);
		}
		if (end < 0 || end > chars.length) {
			throw new ArrayIndexOutOfBoundsException(end);
		}
		return unsafeIndexOfNot(c, chars, begin, end);
	}

	public static int indexOfNot(char c, String string, int begin, int end) {
		if (begin < 0 || begin > string.length()) {
			throw new StringIndexOutOfBoundsException(begin);
		}
		if (end < 0 || end > string.length()) {
			throw new StringIndexOutOfBoundsException(end);
		}
		return unsafeIndexOfNot(c, string.toCharArray(), begin, end);
	}

	public static int indexOfNot(char c, String string) {
		return unsafeIndexOfNot(c, string.toCharArray(), 0, string.length());
	}

	private static int unsafeIndexOfNot(char c, char[] chars, int begin, int end) {
		for (int idx = begin; idx < end; idx++) {
			if (chars[idx] != c) {
				return idx;
			}
		}
		return -1;
	}

	public static int indexOf(char c, String string, int begin, int end) {
		if (begin < 0 || begin > string.length()) {
			throw new StringIndexOutOfBoundsException(begin);
		}
		if (end < 0 || end > string.length()) {
			throw new StringIndexOutOfBoundsException(end);
		}
		return unsafeIndexOf(c, string.toCharArray(), begin, end);
	}

	public static int indexOf(char c, char[] chars, int begin, int end) {
		if (begin < 0 || begin > chars.length) {
			throw new ArrayIndexOutOfBoundsException(begin);
		}
		if (end < 0 || end > chars.length) {
			throw new ArrayIndexOutOfBoundsException(end);
		}
		return unsafeIndexOf(c, chars, begin, end);
	}

	public static int indexOf(char c, String string) {
		return unsafeIndexOf(c, string.toCharArray(), 0, string.length());
	}

	private static int unsafeIndexOf(char c, char[] chars, int begin, int end) {
		for (int idx = begin; idx < end; idx++) {
			if (chars[idx] == c) {
				return idx;
			}
		}
		return -1;
	}

	//Please ensure the string REFERENCE has no prefix which is also a suffix.
	private static final String REFERENCE = "{}";

	//Please ensure there is no character ESCAPE in the string REFERENCE.
	private static final char ESCAPE = '\\';

	public static String format(String formatString, Object... parameters) {
		Objects.requireNonNull(formatString, "The specified formatString is null.");
		PositionalArguments arguments = new ToStringArguments(parameters);
		return pFormat(formatString, arguments);
	}

	public static String format(String formatString, PositionalArguments arguments) {
		Objects.requireNonNull(formatString, "The specified formatString is null.");
		return pFormat(formatString, arguments);
	}

	private static String pFormat(String formatString, PositionalArguments arguments) {
		char[] formatCharacters = formatString.toCharArray();
		SundaySubstringFinder finder = new SundaySubstringFinder(REFERENCE);

		int start = 0;
		Substring substring = finder.find(formatCharacters, start);
		if (substring == null) {
			return formatString;
		}

		StringBuilder res = new StringBuilder();
		do {
			int len = lengthOfTheLongestEscapeSuffix(formatString, start, substring.start());
			if ((len & 1) == 0 && !arguments.available()) {
				throw new IllegalArgumentException("Arguments are not enough.");
			}

			res.append(formatCharacters, start, substring.start() - start - len);
			int maintainEscape = len >> 1;
			if ((len & 1) == 1) {
				res.append(formatCharacters, substring.start() - maintainEscape, substring.length() + maintainEscape);
			} else {
				res.append(formatCharacters, substring.start() - len, maintainEscape);
				res.append(arguments.consume());
			}
			start = substring.end();
			substring = finder.find(formatCharacters, start);
		} while (substring != null);
		res.append(formatCharacters, start, formatCharacters.length - start);
		return res.toString();
	}

	/*
	 * Returns the length of the longest suffix of the given substring beginning at start
	 * and extending to end in the given string. The suffix contains only escape character.
	 */
	private static int lengthOfTheLongestEscapeSuffix(String formatString, int start, int end) {
		int lastIndexOfUnescape = lastIndexOfNot(ESCAPE, formatString, start, end);
		int escapeStart = start;
		if (lastIndexOfUnescape != -1) {
			escapeStart = lastIndexOfUnescape + 1;
		}
		return end - escapeStart;
	}

	public static boolean equalsOnContent(final String s1, final String s2) {
		return Strings.equals(s1 == null ? "" : s1.trim(), s2 == null ? "" : s2.trim());
	}

	public static boolean equalsLiterally(final CharSequence cs1, final CharSequence cs2) {
		return Strings.equals(cs1 == null ? "" : cs1, cs2 == null ? "" : cs2);
	}

	public static String trim(String src, char c) {
		return trim(src, c, TrimPosition.ALL);
	}

	public static String trim(String src, char c, TrimPosition pos) {
		if (src == null || src.isEmpty()) {
			return src;
		}

		pos = pos == null ? TrimPosition.NO : pos;
		char[] chars = src.toCharArray();
		int start = 0;
		int end = chars.length;
		if (pos == TrimPosition.ALL || pos == TrimPosition.END) {
			end = unsafeLastIndexOfNot(c, chars, start, end) + 1;
		}
		if (pos == TrimPosition.ALL || pos == TrimPosition.BEFORE) {
			start = unsafeIndexOfNot(c, chars, start, end);
		}
		return src.substring(start, end);
	}

}
