package ppl.common.utils.pair;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.*;
import java.util.stream.*;

public class PairStream<F, S> implements Stream<Pair<F, S>> {
    private final Stream<Pair<F, S>> stream;

    private PairStream(Stream<Pair<F, S>> stream) {
        this.stream = stream;
    }

    public <K, V> PairStream<K, V> pmap(
            Function<? super F, ? extends K> key,
            Function<? super S, ? extends V> value) {
        return new PairStream<>(stream.map(
                p -> Pair.create(key.apply(p.getFirst()),
                value.apply(p.getSecond()))));
    }

    @Override
    public PairStream<F, S> filter(Predicate<? super Pair<F, S>> predicate) {
        return new PairStream<>(stream.filter(predicate));
    }

    @Override
    public <R> Stream<R> map(Function<? super Pair<F, S>, ? extends R> mapper) {
        return stream.map(mapper);
    }

    @Override
    public IntStream mapToInt(ToIntFunction<? super Pair<F, S>> mapper) {
        return stream.mapToInt(mapper);
    }

    @Override
    public LongStream mapToLong(ToLongFunction<? super Pair<F, S>> mapper) {
        return stream.mapToLong(mapper);
    }

    @Override
    public DoubleStream mapToDouble(ToDoubleFunction<? super Pair<F, S>> mapper) {
        return stream.mapToDouble(mapper);
    }

    @Override
    public <R> Stream<R> flatMap(Function<? super Pair<F, S>, ? extends Stream<? extends R>> mapper) {
        return stream.flatMap(mapper);
    }

    @Override
    public IntStream flatMapToInt(Function<? super Pair<F, S>, ? extends IntStream> mapper) {
        return stream.flatMapToInt(mapper);
    }

    @Override
    public LongStream flatMapToLong(Function<? super Pair<F, S>, ? extends LongStream> mapper) {
        return stream.flatMapToLong(mapper);
    }

    @Override
    public DoubleStream flatMapToDouble(Function<? super Pair<F, S>, ? extends DoubleStream> mapper) {
        return stream.flatMapToDouble(mapper);
    }

    @Override
    public PairStream<F, S> distinct() {
        return new PairStream<>(stream.distinct());
    }

    @Override
    public PairStream<F, S> sorted() {
        return new PairStream<>(stream.sorted());
    }

    @Override
    public PairStream<F, S> sorted(Comparator<? super Pair<F, S>> comparator) {
        return new PairStream<>(stream.sorted(comparator));
    }

    @Override
    public PairStream<F, S> peek(Consumer<? super Pair<F, S>> action) {
        return new PairStream<>(stream.peek(action));
    }

    @Override
    public PairStream<F, S> limit(long maxSize) {
        return new PairStream<>(stream.limit(maxSize));
    }

    @Override
    public PairStream<F, S> skip(long n) {
        return new PairStream<>(stream.skip(n));
    }

    @Override
    public void forEach(Consumer<? super Pair<F, S>> action) {
        stream.forEach(action);
    }

    @Override
    public void forEachOrdered(Consumer<? super Pair<F, S>> action) {
        stream.forEachOrdered(action);
    }

    @Override
    public Object[] toArray() {
        return stream.toArray();
    }

    @Override
    public <A> A[] toArray(IntFunction<A[]> generator) {
        return stream.toArray(generator);
    }

    @Override
    public Pair<F, S> reduce(Pair<F, S> identity, BinaryOperator<Pair<F, S>> accumulator) {
        return stream.reduce(identity, accumulator);
    }

    @Override
    public Optional<Pair<F, S>> reduce(BinaryOperator<Pair<F, S>> accumulator) {
        return stream.reduce(accumulator);
    }

    @Override
    public <U> U reduce(U identity, BiFunction<U, ? super Pair<F, S>, U> accumulator, BinaryOperator<U> combiner) {
        return stream.reduce(identity, accumulator, combiner);
    }

    @Override
    public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super Pair<F, S>> accumulator, BiConsumer<R, R> combiner) {
        return stream.collect(supplier, accumulator, combiner);
    }

    @Override
    public <R, A> R collect(Collector<? super Pair<F, S>, A, R> collector) {
        return stream.collect(collector);
    }

    @Override
    public Optional<Pair<F, S>> min(Comparator<? super Pair<F, S>> comparator) {
        return stream.min(comparator);
    }

    @Override
    public Optional<Pair<F, S>> max(Comparator<? super Pair<F, S>> comparator) {
        return stream.max(comparator);
    }

    @Override
    public long count() {
        return stream.count();
    }

    @Override
    public boolean anyMatch(Predicate<? super Pair<F, S>> predicate) {
        return stream.anyMatch(predicate);
    }

    @Override
    public boolean allMatch(Predicate<? super Pair<F, S>> predicate) {
        return stream.allMatch(predicate);
    }

    @Override
    public boolean noneMatch(Predicate<? super Pair<F, S>> predicate) {
        return stream.noneMatch(predicate);
    }

    @Override
    public Optional<Pair<F, S>> findFirst() {
        return stream.findFirst();
    }

    @Override
    public Optional<Pair<F, S>> findAny() {
        return stream.findAny();
    }

    @Override
    public Iterator<Pair<F, S>> iterator() {
        return stream.iterator();
    }

    @Override
    public Spliterator<Pair<F, S>> spliterator() {
        return stream.spliterator();
    }

    @Override
    public boolean isParallel() {
        return stream.isParallel();
    }

    @Override
    public PairStream<F, S> sequential() {
        return new PairStream<>(stream.sequential());
    }

    @Override
    public PairStream<F, S> parallel() {
        return new PairStream<>(stream.parallel());
    }

    @Override
    public PairStream<F, S> unordered() {
        return new PairStream<>(stream.unordered());
    }

    @Override
    public PairStream<F, S> onClose(Runnable closeHandler) {
        return new PairStream<>(stream.onClose(closeHandler));
    }

    @Override
    public void close() {
        stream.close();
    }

    public static <SS, F, S> PairStream<F, S> create(Stream<SS> stream, Function<SS, Pair<F, S>> creator) {
        return new PairStream<>(stream.map(creator));
    }
}
