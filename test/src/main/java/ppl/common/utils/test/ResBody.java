package ppl.common.utils.test;

public class ResBody<D extends Data> {
    private Meta meta;
    private D data;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public D getData() {
        return data;
    }

    public void setData(D data) {
        this.data = data;
    }
}
