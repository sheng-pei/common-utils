package ppl.common.utils.ext;

import java.util.List;

interface ExtSelector {
    List<OrderedExtPattern> select(String item);
    void addPattern(OrderedExtPattern pattern);
}
