package ppl.common.utils.argument.parser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public interface StringParser<S, V> {
    default Stream<Fragment<S, V>> parse(InputStream is) {
        return parse(is, StandardCharsets.UTF_8);
    }

    default Stream<Fragment<S, V>> parse(InputStream is, Charset charset) {
        Reader reader = new InputStreamReader(is, charset);
        return parse(reader);
    }

    default Stream<Fragment<S, V>> parse(String source) {
        Reader reader = new StringReader(source);
        return parse(reader);
    }

    Stream<Fragment<S, V>> parse(Reader reader);
}
