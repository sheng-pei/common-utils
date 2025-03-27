package ppl.common.utils.net;

import ppl.common.utils.Bytes;
import ppl.common.utils.character.ascii.AsciiGroup;
import ppl.common.utils.exception.UnreachableCodeException;
import ppl.common.utils.string.Strings;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Predicate;

public class URLEncoder {

    private static final String CR = "\r";
    private static final String LN = "\n";
    private static final String CRLN = "\r\n";
    private static final URLEncoder DEFAULT_ENCODER = URLEncoder.builder().build();

    private final Predicate<Character> dontNeedToEncode;
    private final String lineBreak;
    private final boolean percentEncodingReserved;
    private final boolean usePlus;

    private URLEncoder(Builder builder) {
        if (!(Objects.equals(builder.lineBreak, CR) ||
                Objects.equals(builder.lineBreak, LN) ||
                Objects.equals(builder.lineBreak, CRLN))) {
            throw new IllegalArgumentException("Line break is error, use 'CR', 'LN', 'CRLN' instead.");
        }
        this.dontNeedToEncode = builder.dontNeedToEncode.or(URICharGroup.UNRESERVED);
        this.percentEncodingReserved = builder.percentEncodingReserved;
        this.usePlus = builder.usePlus;
        this.lineBreak = builder.lineBreak;
    }

    public String parse(String string) {
        return parse(string, StandardCharsets.UTF_8);
    }

    public String parse(String string, Charset charset) {
        if (Strings.isEmpty(string)) {
            return string;
        }

        int beginToEscape = 0;
        char[] chars = string.toCharArray();
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream(chars.length)) {
            for (int i = 0; i < chars.length; i++) {
                if ('%' == chars[i] && percentEncodingReserved) {
                    if (chars.length - i > 2 &&
                            AsciiGroup.HEX_DIGIT.test(chars[i + 1]) &&
                            AsciiGroup.HEX_DIGIT.test(chars[i + 2])) {
                        parse(chars, beginToEscape, i, charset, stream);
                        stream.write(chars[i]);
                        stream.write(chars[i + 1]);
                        stream.write(chars[i + 2]);
                        i += 2;
                        beginToEscape = i + 1;
                        continue;
                    }
                }

                if (dontNeedToEncode.test(chars[i])) {
                    parse(chars, beginToEscape, i, charset, stream);
                    stream.write(chars[i]);
                    beginToEscape = i + 1;
                    continue;
                }

                if (' ' == chars[i] && usePlus) {
                    parse(chars, beginToEscape, i, charset, stream);
                    stream.write('+');
                    beginToEscape = i + 1;
                }
            }

            parse(chars, beginToEscape, chars.length, charset, stream);

            return stream.toString(StandardCharsets.US_ASCII.name());
        } catch (IOException e) {
            throw new UnreachableCodeException(e);
        }
    }

    private void parse(char[] chars, int begin, int end, Charset charset, ByteArrayOutputStream stream) {
        String string = new String(chars, begin, end - begin);
        string = lineBreak.equals(LN)? string : string.replace("\n", lineBreak);
        byte[] bytes = string.getBytes(charset);
        for (byte aByte : bytes) {
            stream.write('%');
            stream.write(Bytes.hex(aByte).getBytes(), 0, 2);
        }
    }

    public static String encode(String string) {
        return encode(string, StandardCharsets.UTF_8);
    }

    public static String encode(String string, Charset charset) {
        return DEFAULT_ENCODER.parse(string, charset);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Predicate<Character> dontNeedToEncode = AsciiGroup.EMPTY;
        private boolean percentEncodingReserved;
        private boolean usePlus;
        private String lineBreak = LN;

        public Builder() {
        }

        public Builder setPercentEncodingReserved(boolean percentEncodingReserved) {
            this.percentEncodingReserved = percentEncodingReserved;
            return this;
        }

        public Builder orDontNeedToEncode(Predicate<Character> dontNeedToEncode) {
            this.dontNeedToEncode = this.dontNeedToEncode.or(dontNeedToEncode);
            return this;
        }

        public Builder setUsePlus(boolean usePlus) {
            this.usePlus = usePlus;
            return this;
        }

        public Builder setLineBreak(String lineBreak) {
            this.lineBreak = lineBreak;
            return this;
        }

        public URLEncoder build() {
            return new URLEncoder(this);
        }
    }
}
