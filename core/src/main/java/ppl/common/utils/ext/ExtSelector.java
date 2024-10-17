package ppl.common.utils.ext;

import java.util.Collection;
import java.util.List;

public interface ExtSelector {
    List<ExtPattern> select(String item);
    void addPattern(ExtPattern pattern);
}
