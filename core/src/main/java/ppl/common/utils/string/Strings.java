package ppl.common.utils.string;

import ppl.common.utils.pair.Pair;
import ppl.common.utils.string.substring.PositionalArguments;
import ppl.common.utils.string.substring.Substring;
import ppl.common.utils.string.substring.impl.SundaySubstringFinder;
import ppl.common.utils.string.substring.impl.ToStringArguments;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class Strings {

	private Strings() { }

	private static final Predicate<Character> WHITESPACE = c -> c <= ' ';

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

	public static Pair<String, String> kv(String string, char separator) {
		return kv(string, 0, string.length(), separator);
	}

	public static Pair<String, String> kv(String string, int begin, int end, char separator) {
		Objects.requireNonNull(string);
		char[] chars = string.toCharArray();
		checkCharArrayBeginEnd(chars, begin, end);
		String key = string;
		String value = null;
		int idx = indexOf(separator, chars, begin, end);
		if (idx >= 0) {
			key = string.substring(begin, idx);
			value = string.substring(idx+1, end);
		}
		return Pair.create(key, value);
	}

	public static Stream<String> streaming(String string, String regex) {
		Objects.requireNonNull(string, "The source string is null.");
		Objects.requireNonNull(regex, "The specified regex is null.");
		List<String> accumulator = new ArrayList<>();
		Matcher matcher = Pattern.compile(regex).matcher(string);
		int eatenLength = 0;
		int next = 0;
		while (next < string.length() && matcher.find(next)) {
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
		return accumulator.stream();
	}

	public static String[] split(String string, String regex) {
		return streaming(string, regex).toArray(String[]::new);
	}

	private static boolean emptyBeforeMatcher(Matcher matcher, int start) {
		return matcher.start() == start;
	}

	private static String prefixBeforeMatcher(Matcher matcher, String string, int start) {
		return string.substring(start, matcher.start());
	}

	public static String emptyIfNull(String string) {
		return string == null ? "" : string;
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
		return string == null || string.isEmpty();
	}

	public static boolean isNotEmpty(String string) {
		return !isEmpty(string);
	}

	public static boolean isBlank(String string) {
		if (isEmpty(string)) {
			return true;
		}

		char[] chars = string.toCharArray();
		int idx = indexOf(WHITESPACE.negate(), chars, 0, chars.length);
		return idx < 0;
	}

	public static boolean isNotBlank(String string) {
		return !isBlank(string);
	}

	public static boolean equalsIgnoreCase(final CharSequence cs1, final CharSequence cs2) {
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
			return ((String) cs1).equalsIgnoreCase((String) cs2);
		}

		final int length = cs1.length();
		for (int i = 0; i < length; i++) {
			char c1 = cs1.charAt(i);
			char c2 = cs2.charAt(i);
			if (c1 != c2) {
				c1 = Character.toUpperCase(c1);
				c2 = Character.toUpperCase(c2);
				if (c1 != c2) {
					c1 = Character.toLowerCase(c1);
					c2 = Character.toLowerCase(c2);
					if (c1 != c2) {
						return false;
					}
				}
			}
		}
		return true;
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

	private static final Pattern CAMEL_CASE_CHAR_PATTERN = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]*$");

	public static boolean isCamelCase(String string) {
		if (isEmpty(string)) {
			return true;
		}

		return CAMEL_CASE_CHAR_PATTERN.matcher(string).matches();
	}

	public static String toCamelCase(String string) {
		if (isEmpty(string)) {
			return string;
		}

		if (!SNAKE_CASE_CHAR_PATTERN.matcher(string).matches()) {
			throw new IllegalArgumentException(format("The string '{}' is not snake case.", string));
		}

		StringBuilder resultBuilder = new StringBuilder(string.length());
		boolean upper = false;
		for (char currentCharacter : string.toCharArray()) {
			if (currentCharacter == '_') {
				upper = true;
			} else {
				if (upper) {
					resultBuilder.append(Character.toUpperCase(currentCharacter));
					upper = false;
				} else {
					resultBuilder.append(currentCharacter);
				}
			}
		}
		return resultBuilder.toString();
	}

	private static final Pattern SNAKE_CASE_CHAR_PATTERN = Pattern.compile("^_?[a-z][a-z0-9]*(?:_[a-z][a-z0-9]*)*$");

	public static boolean isSnakeCase(String string) {
		if (isEmpty(string)) {
			return true;
		}

		return SNAKE_CASE_CHAR_PATTERN.matcher(string).matches();
	}

	public static String toSnakeCase(String string) {
		if (isEmpty(string)) {
			return string;
		}

		if (!isCamelCase(string)) {
			throw new IllegalArgumentException(format("The string '{}' is not camel case.", string));
		}

		StringBuilder resultBuilder = new StringBuilder(string.length());
		for (char currentCharacter : string.toCharArray()) {
			if (needUnderscore(currentCharacter)) {
				resultBuilder.append("_");
			}
			resultBuilder.append(Character.toLowerCase(currentCharacter));
		}
		return resultBuilder.toString();
	}

	private static boolean needUnderscore(Character current) {
		return Character.isUpperCase(current);
	}

	public static boolean startsWith(String prefix, char[] chars, int begin, int end) {
		Objects.requireNonNull(prefix);
		Objects.requireNonNull(chars);
		checkCharArrayBeginEnd(chars, begin, end);

		if (prefix.length() > chars.length - begin) {
			return false;
		}

		char[] p = prefix.toCharArray();
		for (int i = 0, j = begin; i < p.length; i++, j++) {
			if (p[i] != chars[j]) {
				return false;
			}
		}
		return true;
	}

	public static int lastIndexOfNot(char c, char[] chars, int begin, int end) {
		Objects.requireNonNull(chars);
		checkCharArrayBeginEnd(chars, begin, end);
		return unsafeLastIndexOf(Predicate.<Character>isEqual(c).negate(), chars, begin, end);
	}

	public static int lastIndexOfNot(char c, String string, int begin, int end) {
		Objects.requireNonNull(string);
		checkStringBeginEnd(string, begin, end);
		return unsafeLastIndexOf(Predicate.<Character>isEqual(c).negate(), string.toCharArray(), begin, end);
	}

	public static int lastIndexOfNot(char c, String string) {
		Objects.requireNonNull(string);
		return unsafeLastIndexOf(Predicate.<Character>isEqual(c).negate(),
				string.toCharArray(), 0, string.length());
	}

	public static int lastIndexOf(char c, char[] chars, int begin, int end) {
		Objects.requireNonNull(chars);
		checkCharArrayBeginEnd(chars, begin, end);
		return unsafeLastIndexOf(Predicate.isEqual(c), chars, begin, end);
	}

	public static int lastIndexOf(char c, String string, int begin, int end) {
		Objects.requireNonNull(string);
		checkStringBeginEnd(string, begin, end);
		return unsafeLastIndexOf(Predicate.isEqual(c), string.toCharArray(), begin, end);
	}

	public static int lastIndexOf(char c, String string) {
		return unsafeLastIndexOf(Predicate.isEqual(c), string.toCharArray(), 0, string.length());
	}

	public static int lastIndexOf(Predicate<Character> predicate, char[] chars, int begin, int end) {
		Objects.requireNonNull(chars);
		Objects.requireNonNull(predicate);
		checkCharArrayBeginEnd(chars, begin, end);
		return unsafeLastIndexOf(predicate, chars, begin, end);
	}

	public static int lastIndexOf(Predicate<Character> predicate, String string, int begin, int end) {
		Objects.requireNonNull(string);
		Objects.requireNonNull(predicate);
		checkStringBeginEnd(string, begin, end);
		return lastIndexOf(predicate, string.toCharArray(), begin, end);
	}

	public static int lastIndexOf(Predicate<Character> predicate, String string) {
		Objects.requireNonNull(string);
		Objects.requireNonNull(predicate);
		return unsafeLastIndexOf(predicate, string.toCharArray(), 0, string.length());
	}

	private static int unsafeLastIndexOf(Predicate<Character> predicate, char[] chars, int begin, int end) {
		for (int idx = end - 1; idx > begin - 1; idx--) {
			if (predicate.test(chars[idx])) {
				return idx;
			}
		}
		return -1;
	}

	public static int indexOfNot(char c, char[] chars, int begin, int end) {
		Objects.requireNonNull(chars);
		checkCharArrayBeginEnd(chars, begin, end);
		return unsafeIndexOf(Predicate.<Character>isEqual(c).negate(), chars, begin, end);
	}

	public static int indexOfNot(char c, String string, int begin, int end) {
		Objects.requireNonNull(string);
		checkStringBeginEnd(string, begin, end);
		return unsafeIndexOf(Predicate.<Character>isEqual(c).negate(), string.toCharArray(), begin, end);
	}

	public static int indexOfNot(char c, String string) {
		Objects.requireNonNull(string);
		return unsafeIndexOf(Predicate.<Character>isEqual(c).negate(), string.toCharArray(), 0, string.length());
	}

	public static int indexOf(char c, String string, int begin, int end) {
		Objects.requireNonNull(string);
		checkStringBeginEnd(string, begin, end);
		return unsafeIndexOf(Predicate.isEqual(c), string.toCharArray(), begin, end);
	}

	public static int indexOf(char c, char[] chars, int begin, int end) {
		Objects.requireNonNull(chars);
		checkCharArrayBeginEnd(chars, begin, end);
		return unsafeIndexOf(Predicate.isEqual(c), chars, begin, end);
	}

	public static int indexOf(char c, String string) {
		Objects.requireNonNull(string);
		return unsafeIndexOf(Predicate.isEqual(c), string.toCharArray(), 0, string.length());
	}

	public static int indexOf(Predicate<Character> predicate, String string, int begin, int end) {
		Objects.requireNonNull(string);
		checkStringBeginEnd(string, begin, end);
		return unsafeIndexOf(predicate, string.toCharArray(), begin, end);
	}

	public static int indexOf(Predicate<Character> predicate, char[] chars, int begin, int end) {
		Objects.requireNonNull(chars);
		checkCharArrayBeginEnd(chars, begin, end);
		return unsafeIndexOf(predicate, chars, begin, end);
	}

	public static int indexOf(Predicate<Character> predicate, String string) {
		Objects.requireNonNull(string);
		return unsafeIndexOf(predicate, string.toCharArray(), 0, string.length());
	}

	private static int unsafeIndexOf(Predicate<Character> predicate, char[] chars, int begin, int end) {
		for (int idx = begin; idx < end; idx++) {
			if (predicate.test(chars[idx])) {
				return idx;
			}
		}
		return -1;
	}

	private static void checkStringBeginEnd(String string, int begin, int end) {
		if (begin < 0 || begin > string.length()) {
			throw new StringIndexOutOfBoundsException(begin);
		}
		if (end < 0 || end > string.length()) {
			throw new StringIndexOutOfBoundsException(end);
		}
	}

	private static void checkCharArrayBeginEnd(char[] array, int begin, int end) {
		if (begin < 0 || begin > array.length) {
			throw new ArrayIndexOutOfBoundsException(begin);
		}
		if (end < 0 || end > array.length) {
			throw new ArrayIndexOutOfBoundsException(end);
		}
	}

	//Please ensure the string REFERENCE has no prefix which is also a suffix.
	private static final String REFERENCE = "{}";

	//Please ensure there is no character ESCAPE in the string REFERENCE.
	private static final char ESCAPE = '\\';

	public static String format(String formatString, Object... parameters) {
		Objects.requireNonNull(formatString, "The specified formatString is null.");
		PositionalArguments arguments = new ToStringArguments(parameters);
			return pFormat(formatString, false, arguments);
	}

	public static String format(String formatString, PositionalArguments arguments) {
		Objects.requireNonNull(formatString, "The specified formatString is null.");
		return pFormat(formatString, false, arguments);
	}

	public static String enoughFormat(String formatString, Object... parameters) {
		Objects.requireNonNull(formatString, "The specified formatString is null.");
		PositionalArguments arguments = new ToStringArguments(parameters);
		return pFormat(formatString, true, arguments);
	}

	public static String enoughFormat(String formatString, PositionalArguments arguments) {
		Objects.requireNonNull(formatString, "The specified formatString is null.");
		return pFormat(formatString, true, arguments);
	}

	private static String pFormat(String formatString, boolean errIfArgumentsInsufficient, PositionalArguments arguments) {
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
			res.append(formatCharacters, start, substring.start() - start - len);
			int maintainEscape = len >> 1;
			if ((len & 1) == 1) {
				res.append(formatCharacters, substring.start() - maintainEscape, substring.length() + maintainEscape);
			} else {
				if (arguments.available()) {
					res.append(formatCharacters, substring.start() - len, maintainEscape);
					res.append(arguments.consume());
				} else if (errIfArgumentsInsufficient) {
					throw new IllegalArgumentException("Arguments are not enough.");
				} else {
					res.append(formatCharacters, substring.start() - len, maintainEscape);
				}
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

	public static boolean equalsContent(final String s1, final String s2) {
		return Strings.equals(trim(emptyIfNull(s1)), trim(emptyIfNull(s2)));
	}

	public static boolean equalsIgnoreNull(final String s1, final String s2) {
		return Strings.equals(emptyIfNull(s1), emptyIfNull(s2));
	}

	public static String trim(String src) {
		return trim(src, WHITESPACE);
	}

	public static String trim(String src, TrimPosition position) {
		return trim(src, WHITESPACE, position);
	}

	public static String trim(String src, char c) {
		return trim(src, Predicate.isEqual(c), TrimPosition.ALL);
	}

	public static String trim(String src, Predicate<Character> predicate) {
		if (isEmpty(src)) {
			return src;
		}

		Substring substring = unsafeTrim(src.toCharArray(), predicate, TrimPosition.ALL);
		return substring.string();
	}

	public static String trim(String src, Predicate<Character> predicate, TrimPosition pos) {
		if (isEmpty(src)) {
			return src;
		}

		Substring substring = unsafeTrim(src.toCharArray(), predicate, pos);
		return substring.string();
	}

	public static Substring trim(char[] chars, char c) {
		Objects.requireNonNull(chars);
		return unsafeTrim(chars, Predicate.isEqual(c), TrimPosition.ALL);
	}

	public static Substring trim(char[] chars, Predicate<Character> predicate) {
		return trim(chars, predicate, TrimPosition.ALL);
	}

	public static Substring trim(char[] chars, Predicate<Character> predicate, TrimPosition pos) {
		Objects.requireNonNull(chars);
		Objects.requireNonNull(predicate);
		return unsafeTrim(chars, predicate, pos);
	}

	private static Substring unsafeTrim(char[] chars, Predicate<Character> predicate, TrimPosition pos) {
		pos = pos == null ? TrimPosition.NO : pos;
		int start = 0;
		int end = chars.length;
		if (pos == TrimPosition.ALL || pos == TrimPosition.AFTER) {
			int l = unsafeLastIndexOf(predicate.negate(), chars, start, end);
			if (l < 0) {
				return new Substring(chars, chars.length, chars.length);
			}
			end = l + 1;
		}
		if (pos == TrimPosition.ALL || pos == TrimPosition.BEFORE) {
			start = unsafeIndexOf(predicate.negate(), chars, start, end);
		}
		return new Substring(chars, start, end);
	}

}
