package ppl.common.utils.http.url;

import ppl.common.utils.string.Strings;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Queries {
    public static List<Query> parseQueries(String queries) {
        if (queries == null || queries.isEmpty()) {
            return Collections.emptyList();
        }
        Matcher matcher = URL.QUERY_AND_FRAGMENT_PATTERN.matcher(queries);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid http url query string: '" + queries + "'.");
        }
        String[] strings = Strings.split(queries, Pattern.quote(URL.QUERY_DELIMITER));
        return Arrays.stream(strings)
                .filter(Strings::isNotEmpty)
                .map(Query::parse)
                .collect(Collectors.toList());
    }

    public static Query parseQuery(String query) {
        if (query == null || query.isEmpty()) {
            return null;
        }
        if (query.contains(URL.QUERY_DELIMITER)) {
            throw new IllegalArgumentException("Has query delimiter.");
        }
        Matcher matcher = URL.QUERY_AND_FRAGMENT_PATTERN.matcher(query);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid http url query string: '" + query + "'.");
        }

        return Query.parse(query);
    }
}
