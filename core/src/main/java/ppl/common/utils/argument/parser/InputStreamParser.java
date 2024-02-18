package ppl.common.utils.argument.parser;

import java.io.InputStream;
import java.util.stream.Stream;

public interface InputStreamParser<S, V> {
    Stream<Fragment<S, V>> parse(InputStream is);
}
