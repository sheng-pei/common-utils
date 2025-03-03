package ppl.common.utils.loadbalance;

public interface LoadBalanceStrategy<N, T> {
    T select();
    boolean exclude(N name);
}
