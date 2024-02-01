package ppl.common.utils;

import java.util.concurrent.TimeUnit;

/**
 * Thread unsafe
 */
public class AreaWatch {

    public static AreaWatch createAreaWatch(String description) {
        return new AreaWatch(description);
    }

    private final String description;
    private transient long b = -1;
    private transient long t;
    private transient long cnt;

    private AreaWatch(String description) {
        this.description = description;
    }

    public void begin() {
        if (b != -1) {
            throw new IllegalStateException();
        }
        this.b = System.nanoTime();
        this.cnt ++;
    }

    public void save() {
        if (b == -1) {
            throw new IllegalStateException();
        }
        long now = System.nanoTime();
        long b = this.b;
        this.b = -1;
        this.t += now - b;
    }

    private long elapse() {
        if (b != -1) {
            throw new IllegalStateException();
        }

        return t;
    }

    public String summary() {
        return summary(TimeUnit.NANOSECONDS);
    }

    public String summary(TimeUnit unit) {
        long e = elapse();
        if (unit != TimeUnit.NANOSECONDS) {
            e = unit.convert(e, TimeUnit.NANOSECONDS);
        }

        return String.format(
                "Area '%s': complete %d times / cost %d %s",
                this.description, this.cnt, e, unit.name());
    }

}
