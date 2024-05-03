package ppl.common.utils.test.point.specific;

import ppl.common.utils.test.point.Point;
import ppl.common.utils.test.point.PointType;

import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

@PointType("time")
public class TimePoint implements Point {

    public static final TimePoint ORIGINATION;

    static {
        TimePoint sequence = new TimePoint();
        sequence.setTime(new Date(0L));
        ORIGINATION = sequence;
    }

    private Date time;

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getTime() {
        return time;
    }

    @Override
    public boolean after(Point p) throws UnsupportedOperationException {
        if (!(p instanceof TimePoint)) {
            throw new UnsupportedOperationException("Point class is not match.");
        }

        return Comparator
                .comparing(TimePoint::getTime)
                .compare(this, (TimePoint) p) > 0;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        TimePoint sequence = (TimePoint) object;
        return Objects.equals(time, sequence.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time);
    }
}
