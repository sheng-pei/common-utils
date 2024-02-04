package ppl.common.utils.argument;

import ppl.common.utils.argument.value.FeedingStream;
import ppl.common.utils.argument.value.ValueArgument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Analyzer<K, S> {
    private final Arguments<K, S, ?> arguments;

    public Analyzer(Arguments<K, S, ?> arguments) {
        this.arguments = arguments;
    }

    public List<Object> analyse(Stream<Fragment<S, String>> entryStream) {
        List<Object> res = new ArrayList<>();
        Map<K, FeedingStream<K, Object>> feedingStreams = new HashMap<>();
        entryStream.forEach(f -> {
            Argument<K, Object> argument = arguments.get(f.getKey());
            if (argument == null) {
                res.add(f);
            } else if (argument instanceof ValueArgument) {
                ValueArgument<K, Object> valueArgument = (ValueArgument<K, Object>) argument;
                feedingStreams
                        .computeIfAbsent(argument.getName(), k -> valueArgument.stream())
                        .feed(f.getValue());
            } else {
                if (f.getValue() != null) {
                    throw new ArgumentException(String.format(
                            "The argument: '%s' is a no value argument and cannot receive value: '%s'.",
                            argument, f.getValue()));
                }
                res.add(argument);
            }
        });

        feedingStreams.forEach((key, value) -> res.add(value.produce()));
        return res;
    }

}
