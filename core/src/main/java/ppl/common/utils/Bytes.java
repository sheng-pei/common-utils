package ppl.common.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class Bytes {
    private static final char[] HEX;

    static {
        String base = "0123456789ABCDEF";
        HEX = base.toCharArray();
    }

    private Bytes() {}

    public static byte[] fromHex(String hex) {
        if (hex == null) {
            return null;
        }
        if (hex.isEmpty()) {
            return Arrays.zeroByte();
        }

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

    public static byte oneFromHex(char high, char low) {
        return unsafeByte(high, low);
    }

    public static byte oneFromHex(String hex) {
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
        } else if ('a' <= hexDigit && hexDigit <= 'z') {
            i = hexDigit - 'a' + 10;
        } else {
            throw new IllegalArgumentException("Invalid hex digit: " + hexDigit + ".");
        }
        return i;
    }

    public static String hex(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(bytes.length * 2);
        char[] chars = new char[2];
        for (byte b : bytes) {
            builder.append(hex(b, chars));
        }
        return builder.toString();
    }

    public static String hex(byte b) {
        return new String(hex(b, new char[2]));
    }

    private static char[] hex(byte b, char[] in) {
        int higher = (b >> 4) & 0x0f;
        int lower = b & 0x0f;
        in[0] = HEX[higher];
        in[1] = HEX[lower];
        return in;
    }

    public static String hex(int i) {
        char[] chars = new char[8];
        for (int j = 7; j >= 0; j--) {
            chars[j] = HEX[i & 0x0f];
            i = i >> 4;
        }
        return new String(chars);
    }

    public static String hex(long l) {
        char[] chars = new char[16];
        for (int j = 15; j >= 0; j--) {
            chars[j] = HEX[(int) (l & 0x0f)];
            l = l >> 4;
        }
        return new String(chars);
    }

    public static String base64(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return new String(Base64.getEncoder().encode(bytes),
                StandardCharsets.ISO_8859_1);
    }

    public static byte[] fromBase64(String base64) {
        if (base64 == null) {
            return null;
        }
        return Base64.getDecoder().decode(base64);
    }

    public static byte[] fromBase64(byte[] base64) {
        if (base64 == null) {
            return null;
        }
        return Base64.getDecoder().decode(base64);
    }
}
