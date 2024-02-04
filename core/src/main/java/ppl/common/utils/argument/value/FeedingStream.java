package ppl.common.utils.argument.value;

public interface FeedingStream<K, V> {
    void feed(String source);
    ArgumentValue<K, V> produce();
}
