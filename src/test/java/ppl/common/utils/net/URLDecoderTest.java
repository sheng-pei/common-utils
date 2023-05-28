package ppl.common.utils.net;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class URLDecoderTest {

    @Test
    void decode() {
        Assertions.assertEquals("a-中%20%*", URLDecoder.decode("a-%E4%B8%AD%2520%25%2A", StandardCharsets.UTF_8));
    }

}