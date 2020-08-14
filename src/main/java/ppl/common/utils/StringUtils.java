package ppl.common.utils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	public static final String[] EMPTY_STRING_ARRAY = new String[0];

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
			if (isZeroLength(matcher)) {
				if (!isZeroLengthPrefixBeforeMatcher(matcher, string, eatenLength)) {
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

	private static boolean isZeroLengthPrefixBeforeMatcher(Matcher matcher, String string, int start) {
		return matcher.start() == start;
	}

	private static String prefixBeforeMatcher(Matcher matcher, String string, int start) {
		return string.substring(start, matcher.start());
	}

	private static boolean isZeroLength(Matcher matcher) {
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

	public static int indexOfNot(char c, char[] chars, int pos, int end) {
		for (int idx = pos; idx < end; idx++) {
			if (chars[idx] != c) {
				return idx;
			}
		}
		return -1;
	}

	public static int indexOfNot(char c, String string, int pos, int end) {
		return indexOfNot(c, string.toCharArray(), pos, end);
	}

	public static int indexOfNot(char c, String string) {
		return indexOfNot(c, string, 0, string.length());
	}

	public static int indexOf(char c, char[] chars, int pos, int end) {
		for (int idx = pos; idx < end; idx++) {
			if (chars[idx] == c) {
				return idx;
			}
		}
		return -1;
	}

	private static final char[] REFERENCE = "{}".toCharArray();

	public static String format(String formatString, Object... parameters) {
		Objects.requireNonNull(formatString, "The specified formatString is null");

		char[] formatCharacters = formatString.toCharArray();

		ReferenceMatcher matcher = new ReferenceMatcher(formatCharacters);
		StringBuilder result = new StringBuilder();
		int paramPos = 0;
		int start = 0;
		while (matcher.find(start, REFERENCE)) {
			result.append(formatString, start, matcher.start());
			result.append(formatCharacters, matcher.start(), escapeCharacterLength(matcher) >> 1);
			if (((escapeCharacterLength(matcher)) & 0x1) != 0) {
				result.append("{}");
			} else {
				result.append(getTarget(paramPos++, parameters));
			}
			start = matcher.end();
		}

		result.append(formatString, start, formatString.length());
		return result.toString();
	}

	private static int escapeCharacterLength(ReferenceMatcher matcher) {
		return matcher.end() - matcher.start() - REFERENCE.length;
	}

	private static String getTarget(int paramPos, Object... parameters) {
		if (paramPos < parameters.length) {
			return parameters[paramPos].toString();
		} else {
			throw new IllegalArgumentException("Not enough parameters");
		}
	}

	private static class ReferenceMatcher {
		private static final char ESCAPE_CHARACTER = '\\';

		private final char[] chars;

		private int start;
		private int end;

		public ReferenceMatcher(char[] chars) {
			this.chars = chars;
		}

		public int start() {
			return start;
		}

		public int end() {
			return end;
		}

		public boolean find(int pos, char[] reference) {
			int start = pos;
			int end = pos;
			while (end <= this.chars.length - reference.length) {

				if (this.chars[end] == ESCAPE_CHARACTER) {
					end++;
					continue;
				}

				if (isMatch(end, reference)) {
					this.start = start;
					this.end = end + reference.length;
					return true;
				} else {
					if (end + reference.length >= this.chars.length) {
						break;
					}
					end = start = moveTo(end, reference);
				}

			}
			return false;
		}

		private boolean isMatch(int pos, char[] reference) {
			for (int j = 0; j < reference.length; j++) {
				if (reference[j] != chars[pos + j]) {
					return false;
				}
			}
			return true;
		}

		private int moveTo(int pos, char[] reference) {
			int lastIndex = lastIndexOf(reference, chars[pos + reference.length]);
			return pos + reference.length + (lastIndex == -1 ? 1 : -lastIndex);
		}

		private int lastIndexOf(char[] reference, char c) {
			for (int i = reference.length - 1; i >= 0; i--) {
				if (reference[i] == c) {
					return i;
				}
			}
			return -1;
		}

	}

}
