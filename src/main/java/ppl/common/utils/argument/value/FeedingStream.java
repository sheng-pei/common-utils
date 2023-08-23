package ppl.common.utils.argument.value;

public interface FeedingStream<V> {
    void feed(String source);
    V produce();
}
