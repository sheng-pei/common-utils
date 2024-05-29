package ppl.common.utils.reflect.resolvable;

import ppl.common.utils.exception.UnreachableCodeException;
import ppl.common.utils.reflect.resolvable.variableresolver.VariableResolver;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class WildcardTypeResolvable extends BoundResolvable {

    public static final WildcardTypeResolvable ANY = new WildcardTypeResolvable();

    private final WildcardType type;

    static WildcardTypeResolvable createResolvable(WildcardType wildcardType) {
        if (wildcardType.equals(ANY.type)) {
            return ANY;
        }
        return new WildcardTypeResolvable(wildcardType);
    }

    private WildcardTypeResolvable() {
        super(BoundKind.UPPER, new Resolvable[]{Resolvables.getClassResolvable(Object.class)});
        class B {
            private List<?> list;
        }

        try {
            this.type = (WildcardType) ((ParameterizedType) B.class
                    .getDeclaredField("list")
                    .getGenericType()).getActualTypeArguments()[0];
        } catch (Exception e) {
            throw new UnreachableCodeException();
        }
    }

    private WildcardTypeResolvable(WildcardType type) {
        super(kind(type), bounds(type));
        this.type = type;
    }

    private static BoundKind kind(WildcardType type) {
        Type[] lower = type.getLowerBounds();
        if (lower.length == 0) {
            return BoundKind.UPPER;
        }
        return BoundKind.LOWER;
    }

    private static Resolvable[] bounds(WildcardType type) {
        return Arrays.stream(boundsType(type))
                .map(Resolvables::getResolvable)
                .toArray(Resolvable[]::new);
    }

    private static Type[] boundsType(WildcardType type) {
        Type[] bounds = type.getLowerBounds();
        if (bounds.length == 0) {
            bounds = type.getUpperBounds();
        }
        return bounds;
    }

    @Override
    public Resolvable resolve(VariableResolver variableResolver) {
        Resolvable[] s = getBounds();
        Resolvable[] bounds = Arrays.stream(s)
                .map(variableResolver::resolve)
                .toArray(Resolvable[]::new);
        if (Arrays.equals(s, bounds)) {
            return this;
        }
        return new ResolvedWildcardType(this, bounds);
    }

    @Override
    protected Type[] bounds() {
        return boundsType(type);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        WildcardTypeResolvable that = (WildcardTypeResolvable) object;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
