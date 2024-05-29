package ppl.common.utils.reflect.resolvable;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

public abstract class BoundResolvable implements Resolvable {

    private final BoundKind kind;
    private volatile Resolvable[] bounds;

    protected BoundResolvable(BoundKind kind, Resolvable[] bounds) {
        this.kind = kind;
        this.bounds = bounds;
    }

    public BoundKind getKind() {
        return kind;
    }

    public Resolvable getBound(int idx) {
        return getBounds()[idx];
    }

    public Resolvable[] getBounds() {
        Resolvable[] bounds = this.bounds;
        if (bounds == null) {
            Type[] typeBounds = bounds();
            if (typeBounds == null || typeBounds.length == 0) {
                bounds = new Resolvable[1];
                bounds[0] = Resolvables.getClassResolvable(Object.class);
            } else {
                bounds = Arrays.stream(typeBounds)
                        .map(Resolvables::getResolvable)
                        .toArray(Resolvable[]::new);
            }
            this.bounds = bounds;
        }
        Resolvable[] ret = new Resolvable[bounds.length];
        System.arraycopy(bounds, 0, ret, 0, ret.length);
        return ret;
    }

    protected abstract Type[] bounds();

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        BoundResolvable that = (BoundResolvable) object;
        return kind == that.kind && Arrays.equals(bounds, that.bounds);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(kind);
        result = 31 * result + Arrays.hashCode(bounds);
        return result;
    }
}
