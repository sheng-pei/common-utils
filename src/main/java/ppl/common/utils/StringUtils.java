package ppl.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	public static String[] splitByRegexSeparator(String string, String separator) {
		if (separator == null) {
			throw new IllegalArgumentException("The specified separator is null");
		}

		if (string == null) {
			return ArrayUtils.EMPTY_STRING_ARRAY;
		}

		List<String> list = new ArrayList<>();
		Pattern pattern = Pattern.compile(separator);
		Matcher matcher = pattern.matcher(string);
		int start = 0;
		int matchStart = 0;
		while(matchStart < string.length() && matcher.find(matchStart)) {
			int startMatch = matcher.start();
			int endMatch = matcher.end();

			String pre = string.substring(start, startMatch);
			if (endMatch - startMatch != 0 || !pre.isEmpty()) {
				list.add(pre);
				start = endMatch;
			}

			if (endMatch - startMatch != 0) {
				matchStart = endMatch;
			} else {
				matchStart++;
			}
		}
		String endSub = string.substring(start);
		if (StringUtils.isNotEmpty(endSub)) {
			list.add(endSub);
		}
		return list.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
	}

	public static String[] unique(String[] strings) {
		return Optional.ofNullable(strings)
				.map(Arrays::stream)
				.map(stream -> stream
						.distinct()
						.toArray(String[]::new)
				)
				.orElse(ArrayUtils.EMPTY_STRING_ARRAY);
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

	public static String toSnakeAndLowerCase(String attrName) {
		if (isEmpty(attrName)) {
			return attrName;
		}

		char firstChar = attrName.subSequence(0, 1).charAt(0);
		boolean prevCharIsUpperCaseOrUnderscore = Character.isUpperCase(firstChar) || firstChar == '_';
		StringBuilder resultBuilder = new StringBuilder(attrName.length()).append(Character.toLowerCase(firstChar));
		for (char attrChar : attrName.substring(1).toCharArray()) {
			boolean charIsUpperCase = Character.isUpperCase(attrChar);
			if (!prevCharIsUpperCaseOrUnderscore && charIsUpperCase) {
				resultBuilder.append("_");
			}
			resultBuilder.append(Character.toLowerCase(attrChar));
			prevCharIsUpperCaseOrUnderscore = charIsUpperCase || attrChar == '_';
		}
		return resultBuilder.toString();
	}

	public static String format(String formatString, Object... parameters) {

		if (formatString == null) {
			throw new IllegalArgumentException("The specified formatString must not be null");
		}

		StringBuilder result = new StringBuilder();
		int paramPos = 0;
		int nextStart = 0;
		while (nextStart < formatString.length()) {
			int position = indexOfReference(formatString, nextStart);
			if (position >= 0) {
				int backSlashPos = successionBackslashPos(formatString, position);
				if (backSlashPos == -1) {
					result.append(formatString, nextStart, position);
					result.append(replaceWithParameter(paramPos, parameters));
					paramPos++;
				} else {
					result.append(formatString, nextStart, backSlashPos);
					result.append(formatString, backSlashPos, backSlashPos + (position - backSlashPos) / 2);
					if (((position - backSlashPos) & 0x1) != 0) {
						result.append("{}");
					} else {
						result.append(replaceWithParameter(paramPos, parameters));
						paramPos++;
					}
				}
				nextStart = position + 2;
			} else {
				result.append(formatString, nextStart, formatString.length());
				break;
			}
		}
		return result.toString();
	}

	private static String replaceWithParameter(int paramPos, Object... parameters) {
		if (paramPos < parameters.length) {
			return parameters[paramPos].toString();
		} else {
			throw new IllegalArgumentException("Not enough parameters");
		}
	}

	private static int successionBackslashPos(String formatString, int pos) {
		if (pos < 1 || pos >= formatString.length()) {
			return -1;
		}
		int i = pos - 1;
		if (formatString.charAt(i) != '\\') {
			return -1;
		}
		i--;
		while (i >= 0) {
			if (formatString.charAt(i) == '\\') {
				i--;
			} else {
				return i + 1;
			}
		}
		return 0;
	}

	private static int indexOfReference(String formatString, int pos) {//sunday算法
		char[] reference = new char[] {'{', '}'};

		char[] chars = formatString.toCharArray();
		int i = pos;
		while (i <= chars.length - reference.length) {
			int j;
			for (j = 0; j < reference.length; j++) {
				if (reference[j] != chars[i + j]) {
					if (i + reference.length >= chars.length) {
						return -1;
					}

					int lastIndex = lastIndexOf(reference, chars[i + reference.length]);
					if (lastIndex == -1) {
						i = i + reference.length + 1;
					} else {
						i = i + reference.length - lastIndex;
					}
					break;
				}
			}

			if (j == reference.length) {
				return i;
			}

		}
		return -1;
	}

	private static int lastIndexOf(char[] reference, char c) {
		for (int i = reference.length - 1; i >= 0; i--) {
			if (reference[i] == c) {
				return i;
			}
		}
		return -1;
	}

}
