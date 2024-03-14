package ppl.common.utils.argument.argument.value;

public interface FeedingStream<V> {
    void feed(String source);
    ArgumentValue<V> produce();
}
