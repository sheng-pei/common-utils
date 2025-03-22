package ppl.common.utils.attire.proxy.server.param;

public class QueryPojo {
    private final String[] names;

    public QueryPojo(Query query) {
        String[] names = query.value();
        for (String name : names) {
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Empty name is not allowed.");
            }
        }
        this.names = names.clone();
    }

    public String[] names() {
        return names;
    }
}
