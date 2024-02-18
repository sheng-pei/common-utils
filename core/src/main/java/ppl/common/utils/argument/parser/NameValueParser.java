package ppl.common.utils.argument.parser;

import java.util.stream.Stream;

public interface NameValueParser<S, V> {
    Stream<Fragment<S, V>> parse(String name, String value);
}
