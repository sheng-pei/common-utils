package ppl.common.utils;

import java.util.concurrent.TimeUnit;

public final class StopWatch {

    public static StopWatch createStopWatch() {
        return new StopWatch().start();
    }

    private long startTicker;

    private StopWatch start() {
        this.startTicker = System.nanoTime();
        return this;
    }

    public long elapse() {
        return System.nanoTime() - this.startTicker;
    }

    public long elapse(TimeUnit unit) {
        return unit.convert(elapse(), TimeUnit.NANOSECONDS);
    }

}
