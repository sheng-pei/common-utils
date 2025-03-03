package ppl.common.utils.loadbalance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Function;

public class RandomLoadBalanceStrategy<N, T> implements LoadBalanceStrategy<N, T> {

    private static final Logger log = LoggerFactory.getLogger(RandomLoadBalanceStrategy.class);

    private final Random random = new Random(System.currentTimeMillis());
    private final Map<N, T> services;
    private final CopyOnWriteArraySet<N> availableServices;
    private final Function<T, Boolean> statusMapper;

    public static <T> RandomLoadBalanceStrategy<Integer, T> create(List<T> services, Function<T, Boolean> statusMapper) {
        Map<T, Integer> ts = new IdentityHashMap<>();
        for (int i = 0; i < services.size(); i++) {
            ts.put(services.get(i), i);
        }
        return new RandomLoadBalanceStrategy<>(services, ts::get, statusMapper);
    }

    public static <N, T> RandomLoadBalanceStrategy<N, T> create(List<T> services, Function<T, N> nameMapper, Function<T, Boolean> statusMapper) {
        return new RandomLoadBalanceStrategy<>(services, nameMapper, statusMapper);
    }

    private RandomLoadBalanceStrategy(List<T> services, Function<T, N> nameMapper, Function<T, Boolean> statusMapper) {
        Map<N, T> ts = new HashMap<>();
        for (T t : services) {
            N name = nameMapper.apply(t);
            if (ts.containsKey(name)) {
                log.info("Ignore duplicate service '{}'.", name);
                continue;
            }
            ts.put(name, t);
        }
        this.services = Collections.unmodifiableMap(ts);
        this.availableServices = new CopyOnWriteArraySet<>(ts.keySet());
        this.statusMapper = statusMapper;
    }

    @Override
    public T select() {
        List<N> remains = new ArrayList<>(availableServices);
        while (!remains.isEmpty()) {
            N n = remains.get(random.nextInt(remains.size()));
            T service = services.get(n);
            if (statusMapper.apply(service)) {
                return service;
            } else {
                remains.remove(n);
            }
        }
        throw new IllegalStateException("No available service remains.");
    }

    @Override
    public boolean exclude(N name) {
        Objects.requireNonNull(name);
        return availableServices.remove(name);
    }
}
