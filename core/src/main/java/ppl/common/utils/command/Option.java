package ppl.common.utils.command;

import java.util.List;

public interface Option {
    List<String> getLongOptions();

    List<String> getShortOptions();

    default String id() {
        List<String> first = getLongOptions();
        List<String> second = getShortOptions();
        if (!first.isEmpty()) {
            return first.get(0);
        } else {
            return second.get(0);
        }
    }
}
