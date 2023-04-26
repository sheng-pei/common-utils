package ppl.common.utils;

public class HexUtils {
    private static final byte[] HEX;

    static {
        String base = "0123456789ABCDEF";
        HEX = base.getBytes();
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
