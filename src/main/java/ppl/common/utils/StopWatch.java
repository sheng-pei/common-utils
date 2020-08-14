package ppl.common.utils;

public class StopWatch {

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

}
