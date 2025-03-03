package ppl.common.utils.loadbalance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RoundRobinLoadBalanceStrategy<N, T> implements LoadBalanceStrategy<N, T> {

    private static final Logger log = LoggerFactory.getLogger(RoundRobinLoadBalanceStrategy.class);
    private final AtomicInteger index = new AtomicInteger();
    private final Map<N, T> services;
    private final CopyOnWriteArrayList<N> availableServices;
    private final Function<T, Boolean> statusMapper;

    public static <T> RoundRobinLoadBalanceStrategy<Integer, T> create(List<T> services, Function<T, Boolean> statusMapper) {
        Map<T, Integer> ts = new IdentityHashMap<>();
        for (int i = 0; i < services.size(); i++) {
            ts.put(services.get(i), i);
        }
        return new RoundRobinLoadBalanceStrategy<>(services, ts::get, statusMapper);
    }

    public static <N, T> RoundRobinLoadBalanceStrategy<N, T> create(List<T> services, Function<T, N> nameMapper, Function<T, Boolean> statusMapper) {
        return new RoundRobinLoadBalanceStrategy<>(services, nameMapper, statusMapper);
    }

    private RoundRobinLoadBalanceStrategy(List<T> services, Function<T, N> nameMapper, Function<T, Boolean> statusMapper) {
        Map<N, T> ts = new HashMap<>();
        for (T t : services) {
            N name = nameMapper.apply(t);
            if (ts.containsKey(name)) {
                log.info("Ignore duplicate service '{}'.", name);
                continue;
            }
            ts.put(name, t);
        }

        this.availableServices = services.stream()
                .map(nameMapper)
                .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
        this.services = Collections.unmodifiableMap(ts);
        this.statusMapper = statusMapper;
    }

    @Override
    public T select() {
        Object[] remains = availableServices.toArray();
        int size = remains.length;
        int start = index.getAndIncrement() % size;
        start = start < 0 ? start + size : start;
        T service = getAvailableService(remains, start);
        if (service != null) {
            return service;
        }
        for (int i = (start != size - 1 ? start + 1 : 0); i != start; i = (i + 1) % services.size()) {
            service = getAvailableService(remains, i);
            if (service != null) {
                return service;
            }
        }
        throw new IllegalStateException("No available service remains.");
    }

    private T getAvailableService(Object[] remains, int idx) {
        @SuppressWarnings("unchecked")
        N name = (N) remains[idx];
        T service = services.get(name);
        if (statusMapper.apply(service)) {
            return service;
        }
        return null;
    }

    @Override
    public boolean exclude(N name) {
        Objects.requireNonNull(name);
        return availableServices.remove(name);
    }
}
