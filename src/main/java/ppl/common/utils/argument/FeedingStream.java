package ppl.common.utils.argument;

public interface FeedingStream<V> {
    void feed(String source);
    V produce();
}
