package ppl.common.utils.attire.proxy;

public interface ContextInterceptor<T> {
    T apply(T req);
}
