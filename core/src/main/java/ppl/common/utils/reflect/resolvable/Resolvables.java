package ppl.common.utils.reflect.resolvable;

import ppl.common.utils.cache.Cache;
import ppl.common.utils.cache.ConcurrentReferenceValueCache;
import ppl.common.utils.cache.ReferenceType;

import java.lang.ref.Reference;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Resolvables {

    private static final Cache<Type, Object> CACHE = new ConcurrentReferenceValueCache<>(ReferenceType.WEAK);
    private static final Resolvable[] ZERO_RESOLVABLE = new Resolvable[0];

    private Resolvables() {}

    public static ClassResolvable getClassResolvable(Class<?> clazz) {
        try {
            @SuppressWarnings("unchecked")
            Reference<ClassResolvable> reference = (Reference<ClassResolvable>) CACHE.get(clazz, () ->
                    new Reference<>(ClassResolvable.createClassResolvable(clazz)));
            ClassResolvable ret = reference.get();
            if (reference.isNew()) {
                ret.init();
            }
            return ret;
        } catch (ExecutionException e) {
            throw new IllegalArgumentException("Failed to resolve class: '" + clazz + "'.", e.getCause());
        }
    }

    public static TypeVariableResolvable getVariableResolvable(TypeVariable<?> variable) {
        try {
            @SuppressWarnings("unchecked")
            Reference<TypeVariableResolvable> reference = (Reference<TypeVariableResolvable>) CACHE.get(variable, () ->
                    new Reference<>(TypeVariableResolvable.createVariableResolvable(variable)));
            TypeVariableResolvable ret = reference.get();
            if (reference.isNew()) {
                ret.init();
            }
            return ret;
        } catch (ExecutionException e) {
            throw new IllegalArgumentException(String.format(
                    "Failed to resolve type variable: '%s' of '%s'.",
                    variable, variable.getGenericDeclaration()), e.getCause());
        }
    }

    private static final class Reference<R> {
        private final R ref;
        private final AtomicBoolean newFlag;

        public Reference(R ref) {
            this.ref = ref;
            this.newFlag = new AtomicBoolean();
        }

        public boolean isNew() {
            return newFlag.compareAndSet(true, false);
        }

        public R get() {
            return this.ref;
        }

    }
}