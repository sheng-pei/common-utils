package ppl.common.utils.hdfs.retrier;

import ppl.common.utils.http.NetworkException;

import java.util.List;
import java.util.function.Function;

public abstract class Retrier<P, U, R> implements Function<P, R> {

    private final int maxAttempts;
    private final RetryStage<P, U> stage;
    private final List<Class<? extends Throwable>> errorsToContinue;

    public Retrier(int maxAttempts, RetryStage<P, U> stage, List<Class<? extends Throwable>> errorsToContinue) {
        this.maxAttempts = maxAttempts;
        this.stage = stage;
        this.errorsToContinue = errorsToContinue;
    }

    @Override
    public R apply(P p) {
        U u = stage.init(p);
        int i = 0;
        do {
            try {
                R res = execute(u);
                stage.finish();
                return res;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Throwable e) {
                if (errorsToContinue.stream()
                                .anyMatch(c -> c.isInstance(e))) {
                    u = stage.next(p);
                    i++;
                } else {
                    throw e;
                }
            }
        } while (i < maxAttempts && !Thread.currentThread().isInterrupted());

        if (Thread.currentThread().isInterrupted()) {
            throw new RetriesInterruptedException("Task is interrupted.");
        } else {
            throw new FailedRetryException("Beyond max retry attempts: " + maxAttempts);
        }

    }

    /**
     *
     * @return some result you want.
     * @throws InterruptedException stop retring when it is interrupted.
     * @throws NetworkException retry when there is a network problem.
     */
    protected abstract R execute(U u) throws InterruptedException;

}
