package ppl.common.utils.attire.proxy.server.param;

import ppl.common.utils.http.url.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DQueries {
    private final List<Query> queries;

    private DQueries(Builder builder) {
        this.queries = Collections.unmodifiableList(builder.queries);
    }

    public List<Query> getQueries() {
        return queries;
    }

    public static final class Builder {
        private final List<Query> queries = new ArrayList<>();

        public Builder add(Query query) {
            this.queries.add(query);
            return this;
        }

        public Builder add(String queries) {
            this.queries.addAll(Query.parseQueries(queries));
            return this;
        }

        public Builder add(String name, Object value) {
            this.queries.add(Query.create(name, value == null ? null : value.toString()));
            return this;
        }

        public DQueries build() {
            return new DQueries(this);
        }
    }
}
