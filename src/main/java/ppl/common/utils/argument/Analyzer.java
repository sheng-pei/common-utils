package ppl.common.utils.argument;

import java.util.*;
import java.util.stream.Stream;

public class Analyzer<K, S> {
    private final Arguments<K, S> arguments;

    public Analyzer(Arguments<K, S> arguments) {
        this.arguments = arguments;
    }

    public List<ArgumentValue<K, Object>> analyse(Stream<Fragment<S, String>> entryStream) {
        List<ArgumentValue<K, Object>> res = new ArrayList<>();
        Map<K, FeedingStream<Object>> feedingStreams = new HashMap<>();
        entryStream.forEach(e -> {
            AbstractArgument<K, Object> argument = arguments.get(e.getKey());
            if (argument == null) {
                res.add(ArgumentValue.create(null, e));
            } else {
                feedingStreams
                        .computeIfAbsent(argument.getName(), k -> argument.stream())
                        .feed(e.getValue());
            }
        });

        feedingStreams.forEach((key, value) ->
                res.add(ArgumentValue.create(arguments.getByName(key), value.produce())));
        return res;
    }

}
