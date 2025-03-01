package ppl.common.utils.net;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

class URLEncoderTest {

    @Test
    void parse() {
        URLEncoder encoder = URLEncoder.builder()
                .setPercentEncodingReserved(true)
                .orDontNeedToEncode(c -> c == '%')
                .build();
        Assertions.assertEquals("a-%E4%B8%AD%20%%2A", encoder.parse("a-中%20%*", StandardCharsets.UTF_8));
    }

    @Test
    void encode() {
        Assertions.assertEquals("a-%E4%B8%AD%2520%25%2A", URLEncoder.encode("a-中%20%*", StandardCharsets.UTF_8));
    }

}