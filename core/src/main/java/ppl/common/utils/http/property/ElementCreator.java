package ppl.common.utils.http.property;

public interface ElementCreator<E> {
    Element<E> create(Object value);
}
