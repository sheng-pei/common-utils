package ppl.common.utils.test.point.specific;

import ppl.common.utils.test.point.Point;
import ppl.common.utils.test.point.PointType;

import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

@PointType("time_seq")
public class TimeSeqPoint implements Point {

    public static final TimeSeqPoint ORIGINATION;

    static {
        TimeSeqPoint sequence = new TimeSeqPoint();
        sequence.setTime(new Date(0L));
        sequence.setSeq(Long.MIN_VALUE);
        ORIGINATION = sequence;
    }

    private Date time;
    private Long seq;

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getTime() {
        return time;
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }

    @Override
    public boolean after(Point p) throws UnsupportedOperationException {
        if (!(p instanceof TimeSeqPoint)) {
            throw new UnsupportedOperationException("Point class is not match.");
        }
        return Comparator
                .comparing(TimeSeqPoint::getTime)
                .thenComparing(TimeSeqPoint::getSeq)
                .compare(this, (TimeSeqPoint) p) > 0;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        TimeSeqPoint sequence = (TimeSeqPoint) object;
        return Objects.equals(time, sequence.time) && Objects.equals(seq, sequence.seq);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, seq);
    }
}
