package ppl.common.utils.ext.parser;

import ppl.common.utils.ext.Ext;
import ppl.common.utils.ext.selector.SelectorKind;

public interface ExtParser {
    String name();
    boolean isAccept(String item, SelectorKind selector);
    Ext parse(String name);
}
