package ppl.common.utils.net;

public interface Mask {
    long lowMask();

    long highMask();

    default Mask not() {
        long lowMask = ~lowMask() & 0xfffffffffffffffeL;
        long highMask = ~highMask();
        return new Mask() {
            @Override
            public long lowMask() {
                return lowMask;
            }

            @Override
            public long highMask() {
                return highMask;
            }
        };
    }

    default Mask or(Mask mask) {
        long lowMask = lowMask() | mask.lowMask();
        long highMask = highMask() | mask.highMask();
        return new Mask() {
            @Override
            public long lowMask() {
                return lowMask;
            }

            @Override
            public long highMask() {
                return highMask;
            }
        };
    }

    default Mask and(Mask mask) {
        long lowMask = lowMask() & mask.lowMask();
        long highMask = highMask() & mask.highMask();
        return new Mask() {
            @Override
            public long lowMask() {
                return lowMask;
            }

            @Override
            public long highMask() {
                return highMask;
            }
        };
    }
}
