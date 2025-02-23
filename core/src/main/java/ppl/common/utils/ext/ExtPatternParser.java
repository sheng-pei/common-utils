package ppl.common.utils.ext;

import ppl.common.utils.enumerate.EnumUtils;
import ppl.common.utils.enumerate.UnknownEnumException;
import ppl.common.utils.ext.parser.ExtPattern;
import ppl.common.utils.ext.parser.ExtPatternPosition;
import ppl.common.utils.ext.selector.SelectorKind;

import java.util.regex.Pattern;

public class ExtPatternParser {
    private static final char PATTERN_SEPARATOR = '/';

    private static final int RULE_KIND_FLAG_INDEX = 0;
    private static final char EMPTY_RULE_PATTERN_FLAG = '0';
    private static final char REGEX_RULE_PATTERN_FLAG = 'r';
    private static final int CASE_FLAG_INDEX = 1;
    private static final char CASE_SENSITIVE_FLAG = 'c';
    private static final char CASE_INSENSITIVE_FLAG = 'i';
    private static final int SELECTOR_KIND_FLAG_INDEX = 2;
    private static final char SUFFIX_SELECTOR_KIND_FLAG = 's';
    private static final char PREFIX_SELECTOR_KIND_FLAG = 'p';
    private static final char ROOT_SELECTOR_KIND_FLAG = 'r';
    private static final int EXT_POSITION_FLAG_INDEX = 3;
    private static final char LEFT_FLAG = 'l';
    private static final char RIGHT_FLAG = 'r';
    private static final int IS_EXACT_SELECTOR_FLAG_INDEX = 4;
    private static final char EXACT_SELECTOR_FLAG = 't';
    private static final char NOT_EXACT_SELECTOR_FLAG = 'f';

    public static ExtPattern compile(String pattern) {
        char patternRuleKind = EMPTY_RULE_PATTERN_FLAG;
        char caseSensitive = CASE_INSENSITIVE_FLAG;
        char exactSelector = EXACT_SELECTOR_FLAG;
        SelectorKind kind = SelectorKind.PREFIX;
        ExtPatternPosition position = ExtPatternPosition.RIGHT;

        char[] chars = pattern.toCharArray();
        int s = 0;
        int e = chars.length;
        int nameStart = s;
        int i = s;
        while (i < e) {
            if (chars[i] == PATTERN_SEPARATOR) {
                nameStart = i + 1;
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
                    } else if (SELECTOR_KIND_FLAG_INDEX == idx) {
                        try {
                            kind = EnumUtils.enumOf(SelectorKind.class, chars[j]);
                        } catch (UnknownEnumException ex) {
                            throw new IllegalArgumentException("Invalid pattern, unknown selector flag.", ex);
                        }
                    } else if (EXT_POSITION_FLAG_INDEX == idx) {
                        try {
                            position = EnumUtils.enumOf(ExtPatternPosition.class, chars[j]);
                        } catch (UnknownEnumException ex) {
                            throw new IllegalArgumentException("Invalid position, unknown position flag.", ex);
                        }
                    } else if (IS_EXACT_SELECTOR_FLAG_INDEX == idx) {
                        exactSelector = chars[j];
                        if (exactSelector != EXACT_SELECTOR_FLAG &&
                                exactSelector != NOT_EXACT_SELECTOR_FLAG) {
                            throw new IllegalArgumentException("Error exact selector flag.");
                        }
                    } else {
                        throw new IllegalArgumentException("Too many flags.");
                    }
                }
                break;
            }
            i++;
        }

        ExtPattern.Builder builder = ExtPattern.builder()
                .supportedSelector(kind)
                .position(position)
                .exact(EXACT_SELECTOR_FLAG == exactSelector);
        String name = new String(chars, nameStart, e - nameStart);
        int ruleStart = e;
        int j = nameStart;
        while (j < e) {
            if (chars[j] == PATTERN_SEPARATOR) {
                ruleStart = j + 1;
                name = new String(chars, nameStart, j - nameStart);
                break;
            }
            j++;
        }
        builder.name(name);

        if (patternRuleKind == REGEX_RULE_PATTERN_FLAG && ruleStart == e) {
            throw new IllegalArgumentException("Invalid pattern, regex rule pattern must have regex part.");
        } else if (patternRuleKind == EMPTY_RULE_PATTERN_FLAG && ruleStart != e) {
            throw new IllegalArgumentException("Invalid pattern, empty rule pattern must not have regex part.");
        }

        String rule = name;
        if (patternRuleKind == REGEX_RULE_PATTERN_FLAG) {
            rule = new String(chars, ruleStart, e - ruleStart);
        }

        if (position == ExtPatternPosition.LEFT) {
            rule = "^" + rule + "\\.";
        } else {
            rule = "\\." + rule + "$";
        }

        rule = (caseSensitive == CASE_SENSITIVE_FLAG ? "" : "(?i)") + rule;
        Pattern p = Pattern.compile(rule);
        builder.pattern(p);
        builder.caseSensitive(caseSensitive == CASE_SENSITIVE_FLAG);
        return builder.build();
    }
}
