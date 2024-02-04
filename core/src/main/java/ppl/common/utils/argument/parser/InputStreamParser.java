package ppl.common.utils.argument.parser;

import ppl.common.utils.argument.Fragment;

import java.io.InputStream;
import java.util.stream.Stream;

public interface InputStreamParser<S, V> {
    Stream<Fragment<S, V>> parse(InputStream is);
}
