package ppl.common.utils.ext;

import ppl.common.utils.enumerate.EnumUtils;
import ppl.common.utils.enumerate.UnknownEnumException;

import java.util.regex.Pattern;

public class ExtPatternParser {
    private static final char PATTERN_SEPARATOR = '/';

    private static final int RULE_KIND_FLAG_INDEX = 0;
    private static final char EMPTY_RULE_PATTERN_FLAG = '0';
    private static final char REGEX_RULE_PATTERN_FLAG = 'r';
    private static final int CASE_FLAG_INDEX = 1;
    private static final char CASE_SENSITIVE_FLAG = 'c';
    private static final char CASE_INSENSITIVE_FLAG = 'i';
    private static final int EXT_KIND_FLAG_INDEX = 2;
    private static final char EQUALS_EXT_FLAG = 'e';
    private static final char PREFIX_EXT_FLAG = 'p';
    private static final int EXT_POSITION_FLAG_INDEX = 3;
    private static final char LEFT_FLAG = 'l';
    private static final char RIGHT_FLAG = 'r';

    public static ExtPattern compile(String pattern) {
        char patternRuleKind = EMPTY_RULE_PATTERN_FLAG;
        char caseSensitive = CASE_INSENSITIVE_FLAG;
        ExtKind kind = ExtKind.EQUALS;
        ExtPosition position = ExtPosition.RIGHT;

        char[] chars = pattern.toCharArray();
        int s = 0;
        int e = chars.length;
        int extStart = s;
        int i = s;
        while (i < e) {
            if (chars[i] == PATTERN_SEPARATOR) {
                extStart = i + 1;
                for (int j = s; j < i; j++) {
                    int idx = j - s;
                    if (RULE_KIND_FLAG_INDEX == idx) {
                        patternRuleKind = chars[j];
                        if (patternRuleKind != REGEX_RULE_PATTERN_FLAG &&
                                patternRuleKind != EMPTY_RULE_PATTERN_FLAG) {
                            throw new IllegalArgumentException("Invalid pattern, invalid pattern kind.");
                        }
                    } else if (CASE_FLAG_INDEX == idx) {
                        caseSensitive = chars[j];
                        if (caseSensitive != CASE_SENSITIVE_FLAG &&
                                caseSensitive != CASE_INSENSITIVE_FLAG) {
                            throw new IllegalArgumentException("Error case flag.");
                        }
                    } else if (EXT_KIND_FLAG_INDEX == idx) {
                        try {
                            kind = EnumUtils.enumOf(ExtKind.class, chars[j]);
                        } catch (UnknownEnumException ex) {
                            throw new IllegalArgumentException("Invalid pattern, unknown selector flag.", ex);
                        }
                    } else if (EXT_POSITION_FLAG_INDEX == idx) {
                        try {
                            position = EnumUtils.enumOf(ExtPosition.class, chars[j]);
                        } catch (UnknownEnumException ex) {
                            throw new IllegalArgumentException("Invalid position, unknown position flag.", ex);
                        }
                    } else {
                        throw new IllegalArgumentException("Too many flags.");
                    }
                }
                break;
            }
            i++;
        }

        ExtPattern.Builder builder = ExtPattern.builder().kind(kind).position(position);
        String ext = new String(chars, extStart, e - extStart);
        int ruleStart = e;
        int j = extStart;
        while (j < e) {
            if (chars[j] == PATTERN_SEPARATOR) {
                ruleStart = j + 1;
                ext = new String(chars, extStart, j - extStart);
                break;
            }
            j++;
        }
        builder.ext(ext);

        if (patternRuleKind == REGEX_RULE_PATTERN_FLAG && ruleStart == e) {
            throw new IllegalArgumentException("Invalid pattern, regex rule pattern must have regex part.");
        } else if (patternRuleKind == EMPTY_RULE_PATTERN_FLAG && ruleStart != e) {
            throw new IllegalArgumentException("Invalid pattern, empty rule pattern must not have regex part.");
        }

        String rule = ext;
        if (patternRuleKind == REGEX_RULE_PATTERN_FLAG) {
            rule = new String(chars, ruleStart, e - ruleStart);
        }

        if (position == ExtPosition.LEFT) {
            rule = "^" + rule + "(\\.|$)";
        } else {
            rule = "(^|\\.)" + rule + "$";
        }

        rule = (caseSensitive == CASE_SENSITIVE_FLAG ? "" : "(?i)") + rule;
        Pattern p = Pattern.compile(rule);
        builder.pattern(p);
        return builder.build();
    }
}
