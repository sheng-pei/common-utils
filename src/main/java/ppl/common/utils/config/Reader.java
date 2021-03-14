package ppl.common.utils.config;

import java.util.Iterator;

public interface Reader extends Key, Value, Getter {

    String ROOT_PATH = ".";

    String PATH_SEPARATOR = ".";

    String absolutePath();

    Reader getParent();

    Reader getChild(Object key);

    Iterator<Reader> iterator();

}
