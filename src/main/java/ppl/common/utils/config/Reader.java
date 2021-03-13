package ppl.common.utils.config;

import java.util.Iterator;

public interface Reader extends Key, Value, Getter {

    String ROOT_PATH = ".";

    String absolutePath();

    boolean isNull();

    Reader getParent();

    Reader getChild(Object key);

    Iterator<Reader> iterator();

}
