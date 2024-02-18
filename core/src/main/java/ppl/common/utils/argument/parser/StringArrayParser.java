package ppl.common.utils.argument.parser;

import java.util.stream.Stream;

public interface StringArrayParser<S, V> {
    Stream<Fragment<S, V>> parse(String[] args);
}
