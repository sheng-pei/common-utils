package ppl.common.utils;

import java.util.concurrent.atomic.AtomicReference;

public class SetOnce<E> {
    private final AtomicReference<E> e = new AtomicReference<>();

    public E get() {
        E e = this.e.get();
        if (e == null) {
            throw new IllegalStateException("No element.");
        }
        return e;
    }

    public void set(E e) {
        if (!this.e.compareAndSet(null, e)) {
            throw new IllegalStateException("Already set.");
        }
    }
}
