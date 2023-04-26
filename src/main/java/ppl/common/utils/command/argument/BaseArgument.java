package ppl.common.utils.command.argument;

import ppl.common.utils.string.Strings;
import ppl.common.utils.command.CommandLineException;
import ppl.common.utils.command.argument.splitter.Splitters;
import ppl.common.utils.exception.UnreachableCodeException;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class BaseArgument<V> implements Argument<V> {

    private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z][0-9a-zA-Z]*");

    private final String name;
    private final Splitter splitter;
    @SuppressWarnings("rawtypes")
    private final List mappers;
    @SuppressWarnings("rawtypes")
    private final Collector collector;
    private final Value<V> value;

    protected BaseArgument(String name, Splitter splitter, List<Mapper<?, ?>> mappers, Collector<?, ?> collector) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Argument name is required.");
        }

        this.name = checkName(name.trim());
        this.splitter = splitter;
        @SuppressWarnings({"rawtypes"})
        List rawMappers = mappers;
        this.mappers = mappers == null ? Collections.emptyList() : rawMappers;
        this.collector = collector;
        this.value = new Value<>();
    }

    private static String checkName(String name) {
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("Invalid argument name: " + name);
        }
        return name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void receive() {
        receive(null);
    }

    @Override
    public void receive(String value) {
        this.value.add(value);
    }

    @Override
    public boolean received() {
        return this.value.received();
    }

    @Override
    public Optional<V> resolve() {
        return this.value.resolve();
    }

    private <V> Optional<V> resolve(String originValue) {
        Optional<String> input = Optional.ofNullable(originValue);
        Optional<List<String>> splitted = input.map(splitter::split);
        List<Optional<String>> optionalValues = Collections.singletonList(Optional.empty());
        if (splitted.isPresent()) {
            optionalValues = splitted.get().stream()
                    .map(Optional::ofNullable)
                    .collect(Collectors.toList());
        }

        @SuppressWarnings("unchecked")
        List<Mapper<Object, Object>> mappers = this.mappers;
        Optional<Object> output = Optional.empty();
        for (Optional<String> optionalValue : optionalValues) {
            Optional<Object> tmp = apply(optionalValue, mappers);
            if (tmp.isPresent()) {
                output = tmp;
            }
        }

        @SuppressWarnings("unchecked")
        Optional<V> res = (Optional<V>) output;
        return res;
    }

    private Optional<Object> apply(Optional<String> input, List<Mapper<Object, Object>> mappers) {
        @SuppressWarnings({"rawtypes", "unchecked"})
        Optional<Object> res = (Optional) input;
        for (Mapper<Object, Object> mapper : mappers) {
            try {
                if (isNullableFunction(mapper.getClass())) {
                    res = Optional.ofNullable(mapper.map(res.orElse(null)));
                } else {
                    res = res.map(mapper::map);
                }
            } catch (Throwable t) {
                throw new CommandLineException(Strings.format(
                        "Invalid argument '{}'.", this), t);
            }
        }
        return res;
    }

    private boolean isNullableFunction(@SuppressWarnings("rawtypes") Class<? extends Mapper> handler) {
        try {
            Method method = handler.getMethod("map", Object.class);
            Nullable nullable = method.getAnnotation(Nullable.class);
            return nullable != null;
        } catch (NoSuchMethodException e) {
            throw new UnreachableCodeException(e);
        } catch (SecurityException e) {
            throw new IllegalStateException(Strings.format(
                    "The handle method of the Handler implementation class '{}' is not accessible.",
                    handler.getCanonicalName()), e);
        }
    }

    @Override
    public final int hashCode() {
        return getName().hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        if (!(obj instanceof BaseArgument)) {
            return false;
        }
        BaseArgument<?> baseArgument = (BaseArgument<?>) obj;
        return getName().equals(baseArgument.getName());
    }

    public static abstract class Builder<V, A extends BaseArgument<V>, T extends Builder<V, A, T>> implements Argument.Builder<V> {
        private String name;
        private Splitter splitter = Splitters.NOT_SPLIT;
        private List<Mapper<?, ?>> mappers;
        private Collector<?, ?> collector;

        protected Builder() {
            this.mappers = new ArrayList<>();
        }

        public final T with(BaseArgument<V> baseArgument) {
            this.name = baseArgument.name;
            this.splitter = baseArgument.splitter;
            @SuppressWarnings("unchecked")
            List<Mapper<?, ?>> tmp = (List<Mapper<?, ?>>) baseArgument.mappers;
            this.mappers = tmp;
            this.collector = baseArgument.collector;
            return self();
        }

        public final T withName(String name) {
            this.name = name;
            return self();
        }

        private T self() {
            @SuppressWarnings("unchecked")
            T self = (T) this;
            return self;
        }

        @Override
        public Argument.Builder<V> split(Splitter splitter) {
            this.splitter = splitter;
            return this;
        }

        public <R> Argument.Builder<R> map(Mapper<V, R> mapper) {
            this.mappers.add(mapper);
            @SuppressWarnings("unchecked")
            Argument.Builder<R> builder = (Argument.Builder<R>) this;
            return builder;
        }

        public Argument<V> collect() {
            this.collector = ppl.common.utils.command.argument.collector.Collectors.one();
            return build();
        }

        public <R> Argument<R> collect(Collector<V, R> collector) {
            this.collector = collector;
            @SuppressWarnings("unchecked")
            Argument<R> res = (Argument<R>) build();
            return res;
        }

        protected abstract Argument<V> build();

        protected final String getName() {
            return name;
        }

        protected final Splitter getSplitter() {
            return splitter;
        }

        protected final List<Mapper<?, ?>> getMappers() {
            return Collections.unmodifiableList(mappers);
        }

        protected final Collector<?, ?> getCollector() {
            return collector;
        }
    }
    
    private class Value<V> {
        private final List<String> originValues;
        private Optional<V> value;

        public Value() {
            this.originValues = new ArrayList<>();
        }

        public void add(String value) {
            this.originValues.add(value);
        }

        public boolean received() {
            return !originValues.isEmpty();
        }

        public Optional<V> resolve() {
            if (this.value == null) {
                Optional<V> value = Optional.empty();
                for (String originValue : originValues) {
                    Optional<V> resolved = BaseArgument.this.resolve(originValue);
                    if (resolved.isPresent()) {
                        value = resolved;
                    }
                }
                this.value = value;
            }
            return this.value;
        }

        @Override
        public String toString() {
            return BaseArgument.this +
                    ", value: '" +
                    toString(originValues) +
                    "'";
        }

        private String toString(List<String> originValues) {
            if (originValues.size() == 1) {
                return toString(originValues.get(0));
            } else {
                return originValues.stream()
                        .map(this::toString)
                        .collect(Collectors.joining(", "));
            }
        }

        private String toString(String originValue) {
            if (originValue == null) {
                return "null";
            } else {
                return "\"" + originValue.replace("\"", "\\\"") + "\"";
            }
        }
    }

}
