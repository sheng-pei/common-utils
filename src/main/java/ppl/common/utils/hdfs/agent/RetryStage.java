package ppl.common.utils.hdfs.agent;

public interface RetryStage<P, T> {
    T init(P p);
    T next(P p);
    void finish();
}
