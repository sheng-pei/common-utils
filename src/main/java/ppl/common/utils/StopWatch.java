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
        long now = System.nanoTime();
        long res = now - this.startTicker;
        this.startTicker = now;
        return res;
    }

    public long elapse(TimeUnit unit) {
        return unit.convert(elapse(), TimeUnit.NANOSECONDS);
    }

}
