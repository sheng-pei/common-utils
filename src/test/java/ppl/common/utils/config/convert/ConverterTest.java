package ppl.common.utils.config.convert;

import org.junit.jupiter.api.Test;

class ConverterTest {

    @Test
    void convert() {
        Converter<String> converter = Converter.castConverter();
        converter.convert("aa", String.class);
    }

}