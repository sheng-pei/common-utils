package ppl.common.utils.test;

public class ResPageBody<D extends Data> extends ResBody<D> {
    private Pagination pagination;

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
