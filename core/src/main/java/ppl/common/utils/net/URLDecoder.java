package ppl.common.utils.net;

import ppl.common.utils.Bytes;
import ppl.common.utils.character.ascii.AsciiGroup;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class URLDecoder {

    private static final URLDecoder DEFAULT_URL_DECODER = URLDecoder.builder().build();

    private static final String CR = "\r";
    private static final String LN = "\n";
    private static final String CRLN = "\r\n";

    private final String lineBreak;

    private URLDecoder(Builder builder) {
        if (!(Objects.equals(builder.lineBreak, CR) ||
                Objects.equals(builder.lineBreak, LN) ||
                Objects.equals(builder.lineBreak, CRLN))) {
            throw new IllegalArgumentException("Line break is error, use 'CR', 'LN', 'CRLN' instead.");
        }
        this.lineBreak = builder.lineBreak;
    }

    public static String decode(String string) {
        return decode(string, StandardCharsets.UTF_8);
    }

    public static String decode(String string, Charset charset) {
        return DEFAULT_URL_DECODER.unparse(string, charset);
    }

    public String unparse(String string) {
        return unparse(string, StandardCharsets.UTF_8);
    }

    public String unparse(String string, Charset charset) {
        StringBuilder builder = new StringBuilder(string.length());
        ByteBuffer byteBuffer = ByteBuffer.allocate(string.length() / 3);
        char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            while (i < chars.length && '%' == chars[i]) {
                if (chars.length - i > 2 &&
                        AsciiGroup.HEX_DIGIT.test(chars[i+1]) &&
                        AsciiGroup.HEX_DIGIT.test(chars[i+2])) {
                    byteBuffer.put(Bytes.oneFromHex(chars[i+1], chars[i+2]));
                    i += 3;
                } else {
                    throw new IllegalArgumentException("Illegal escaped pattern.");
                }
            }
            byteBuffer.flip();
            String s = new String(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit() - byteBuffer.position(), charset);
            builder.append(lineBreak.equals(LN) ? s : s.replace(lineBreak, LN));
            byteBuffer.clear();

            if (i < chars.length) {
                if ('+' == chars[i]) {
                    builder.append(' ');
                } else {
                    builder.append(chars[i]);
                }
            }
        }
        return builder.toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String lineBreak = LN;

        public Builder() {
        }

        public Builder setLineBreak(String lineBreak) {
            this.lineBreak = lineBreak;
            return this;
        }

        public URLDecoder build() {
            return new URLDecoder(this);
        }
    }
}
