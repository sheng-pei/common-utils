package ppl.common.utils.argument.value.collector;

class One<T> {

    private final Type type;
    private final boolean required;
    private T t;

    public One(Type type, boolean required) {
        this.type = type;
        this.required = required;
    }

    public T get() {
        if (required && t == null) {
            throw new CollectorException("No data provided.");
        }
        return t;
    }

    public void accumulate(T t) {
        switch (type) {
            case ONLY_ONE:
                if (this.t != null) {
                    throw new CollectorException("Only one is allowed in the stream.");
                }
                this.t = t;
                break;
            case LAST_SEEN:
                this.t = t;
                break;
            case FIRST_SEEN:
                if (this.t == null) {
                    this.t = t;
                }
        }
    }

    public One<T> combine(One<T> one) {
        T other = one.get();
        if (other != null) {
            this.accumulate(other);
        }
        return this;
    }
}
