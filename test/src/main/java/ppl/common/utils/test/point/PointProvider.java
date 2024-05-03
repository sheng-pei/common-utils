package ppl.common.utils.test.point;

public interface PointProvider<T, P extends Point> {
    P min();
    P get(T object);
}
