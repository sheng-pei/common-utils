package ppl.common.utils.attire.proxy;

import ppl.common.utils.Arrays;

import java.lang.reflect.Method;
import java.util.function.Consumer;

public abstract class AbstractStatefulParameterInterceptor<T> implements ParameterInterceptor<T> {

    public static Object[] unwrap(Object[] objects) {
        Object[] ret = new Object[objects.length];
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] instanceof StatefulParameter) {
                ret[i] = ((StatefulParameter) objects[i]).object;
            } else {
                ret[i] = objects[i];
            }
        }
        return ret;
    }

    @Override
    public T handle(Method method, Object[] parameters, T collector) {
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = wrap(parameters[i]);
        }
        return handleImpl(method, parameters, collector);
    }

    private Object wrap(Object object) {
        if (object instanceof StatefulParameter) {
            return object;
        }
        return new StatefulParameter(object);
    }

    protected abstract T handleImpl(Method method, Object[] parameters, T collector);

    protected final void useShare(Object parameter, Consumer<Object> consumer) {
        StatefulParameter statefulParameter = (StatefulParameter) parameter;
        statefulParameter.useShare(consumer);
    }

    protected final void useExclusive(Object parameter, Consumer<Object> consumer) {
        StatefulParameter statefulParameter = (StatefulParameter) parameter;
        statefulParameter.useExclusive(consumer);
    }

    protected final boolean isUsed(Object parameter) {
        StatefulParameter statefulParameter = (StatefulParameter) parameter;
        return statefulParameter.useType != null;
    }

    protected final int[] remainParameters(Object[] parameters) {
        int[] ret = new int[parameters.length];
        Arrays.fill(ret, -1);
        for (int i = 0; i < parameters.length; i++) {
            if (!isUsed(parameters[i])) {
                ret[i] = i;
            }
        }
        return ret;
    }

    protected static final class StatefulParameter {
        private UseType useType;
        private final Object object;

        private StatefulParameter(Object object) {
            this.object = object;
            this.useType = null;
        }

        public void useShare(Consumer<Object> consumer) {
            if (useType == UseType.EXCLUSIVE) {
                throw new IllegalArgumentException("This parameter couldn't be used, " +
                        "because it has been exclusive used by the other.");
            }

            consumer.accept(object);
            useType = UseType.SHARE;
        }

        public void useExclusive(Consumer<Object> consumer) {
            if (useType != null) {
                throw new IllegalArgumentException("This parameter couldn't be exclusive used, " +
                        "because it it used by the others.");
            }
            consumer.accept(object);
            useType = UseType.EXCLUSIVE;
        }
    }

    protected enum UseType {
        SHARE,
        EXCLUSIVE;
    }

}
