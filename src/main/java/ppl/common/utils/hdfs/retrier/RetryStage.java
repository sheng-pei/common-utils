package ppl.common.utils.hdfs.retrier;

/**
 * Make sure there is no exception thrown.
 */
public interface RetryStage<P, T> {
    T init(P p);
    T next(P p);
    void finish();
}
