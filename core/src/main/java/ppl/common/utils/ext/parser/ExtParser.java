package ppl.common.utils.ext.parser;

import ppl.common.utils.ext.ExtMatcher;
import ppl.common.utils.ext.selector.SelectorKind;

public interface ExtParser {
    String name();
    boolean isAccept(String item, SelectorKind selector);
    ExtMatcher parse(String name);
}
