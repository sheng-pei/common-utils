package ppl.common.utils.asserts;

import ppl.common.utils.character.ascii.AsciiGroup;
import ppl.common.utils.character.ascii.Mask;
import ppl.common.utils.string.Strings;

import java.util.function.Predicate;

public final class IPs {
    private IPs() {
    }

    private static final Predicate<Character> IPV6_DELIMITER = Mask.asciiMask(":%").predicate();
    private static final Predicate<Character> NOT_HEX = AsciiGroup.HEX_DIGIT.negate();

    public static boolean isIpv6(String ipv6) {
        if (Strings.isEmpty(ipv6)) {
            return false;
        }

        int count = 0;
        boolean doubleColons = false;

        char[] chars = ipv6.toCharArray();
        int start = 0;
        while (start < chars.length) {
            char c = chars[start];

            if (c == ':') {
                int idx = Strings.indexOfNot(':', chars, start, chars.length);
                if (idx < 0) {
                    idx = chars.length;
                }

                int l = idx - start;
                if (l > 2) {
                    return false;
                }

                if (l == 2) {
                    if (doubleColons) {
                        return false;
                    } else {
                        doubleColons = true;
                        if (idx == chars.length || chars[idx] == '%') {
                            break;
                        }
                    }
                } else {
                    //l == 1
                    if (idx == chars.length || chars[idx] == '%') {
                        return false;
                    } else if (start == 0) {
                        return false;
                    }
                }

                start = idx;
            } else {
                int idx = Strings.indexOf(IPV6_DELIMITER, chars, start, chars.length);
                if (idx < 0) {
                    idx = chars.length;
                }

                int i = start;
                for (; i < idx; i++) {
                    if (chars[i] != '0') {
                        break;
                    }
                }

                int uHex = Strings.indexOf(NOT_HEX, chars, i, idx);
                if (uHex >= 0) {
                    if (chars[uHex] == '.' && (idx == chars.length || chars[idx] == '%')) {//maybe ipv4
                        if (isIpv4(chars, i, idx)) {
                            if (count == 7) {
                                return false;
                            }
                            count += 2;
                            break;
                        }
                    }
                    return false;
                } else if (idx - i > 4) {
                    return false;
                }

                if (count == 8) {
                    return false;
                }
                count++;
                if (idx == chars.length || chars[idx] == '%') {
                    break;
                }
                start = idx;
            }
        }
        return doubleColons ? count <= 8 : count == 8;
    }

    public static void assertIpv6(String ipv6) {
        if (!isIpv6(ipv6)) {
            throw new IllegalArgumentException("Invalid ipv6.");
        }
    }

    public static boolean isIpv4(String ipv4) {
        if (Strings.isEmpty(ipv4)) {
            return false;
        }

        return isIpv4(ipv4.toCharArray(), 0, ipv4.length());
    }

    public static void assertIpv4(String ipv4) {
        if (!isIpv4(ipv4)) {
            throw new IllegalArgumentException("Invalid ipv4.");
        }
    }

    private static boolean isIpv4(char[] chars, int start, int end) {
        int count = 0;
        while (start < chars.length) {
            if (chars[start] == '.') {
                return false;
            }

            int idx = Strings.indexOf('.', chars, start, end);
            int e = idx < 0 ? chars.length : idx;
            int num = 0;
            for (int i = start; i < e; i++) {
                if (num != 0 || chars[i] != '0') {
                    num = num * 10 + Character.digit(chars[i], 10);
                }
                if (num > 255) {
                    return false;
                }
            }

            if (count == 4) {
                return false;
            } else {
                count++;
            }
            start = e + 1;
        }
        return count == 4;
    }
}
