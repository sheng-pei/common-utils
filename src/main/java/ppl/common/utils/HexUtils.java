package ppl.common.utils;

import ppl.common.utils.string.Strings;

public class HexUtils {
    private static final byte[] HEX;

    static {
        String base = "0123456789ABCDEF";
        HEX = base.getBytes();
    }

    public static byte[] bytes(String hex) {
        if (Strings.isEmpty(hex)) {
            return new byte[0];
        }
        hex = hex.toUpperCase();
        if ((hex.length() & 1) != 0) {
            hex = "0" + hex;
        }
        char[] chars = hex.toCharArray();
        byte[] res = new byte[chars.length >> 1];
        for (int i = 0; i < chars.length; i+=2) {
            res[i>>1] = unsafeByte(chars[i], chars[i+1]);
        }
        return res;
    }

    public static byte aByte(char high, char low) {
        return unsafeByte(Character.toUpperCase(high), Character.toUpperCase(low));
    }

    public static byte aByte(String hex) {
        if (hex == null || hex.length() != 1 && hex.length() != 2) {
            throw new IllegalArgumentException("One or two hex digit is required for one byte.");
        }

        hex = hex.toUpperCase();
        hex = (hex.length() == 1 ? "0" : "") + hex;
        return unsafeByte(hex);
    }

    private static byte unsafeByte(String hex) {
        return unsafeByte(hex.charAt(0), hex.charAt(1));
    }

    private static byte unsafeByte(char high, char low) {
        int h = value(high);
        int l = value(low);
        return (byte) (h << 4 | l);
    }

    private static int value(char hexDigit) {
        int i;
        if ('0' <= hexDigit && hexDigit <= '9') {
            i = hexDigit - '0';
        } else if ('A' <= hexDigit && hexDigit <= 'Z') {
            i = hexDigit - 'A' + 10;
        } else {
            throw new IllegalArgumentException("Invalid hex digit: " + hexDigit + ".");
        }
        return i;
    }

    public static String hex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(hex(b));
        }
        return builder.toString();
    }

    public static String hex(byte b) {
        byte[] bytes = new byte[2];
        int higher = (b >> 4) & 0x0f;
        int lower = b & 0x0f;
        bytes[0] = HEX[higher];
        bytes[1] = HEX[lower];
        return new String(bytes);
    }
}
