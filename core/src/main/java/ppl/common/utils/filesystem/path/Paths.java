package ppl.common.utils.filesystem.path;

import java.util.ArrayList;
import java.util.List;

public final class Paths {

    private Paths() {}

    public static Path get(String first, String... more) {
        List<String> list = new ArrayList<>();
        if (first != null && !first.isEmpty()) {
            list.add(first);
        }
        for (String m : more) {
            if (m != null && !m.isEmpty()) {
                list.add(m);
            }
        }
        if (list.isEmpty()) {
            return Paths.get("");
        } else {
            return Paths.get(String.join(Path.C_SEPARATOR.toString(), list));
        }
    }

    public static Path get(String path) {
        Path ret = new PathImpl(path);
        if (ret.equals(PathImpl.ROOT)) {
            return PathImpl.ROOT;
        }
        return ret;
    }
}
