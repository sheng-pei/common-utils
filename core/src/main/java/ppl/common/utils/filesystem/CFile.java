package ppl.common.utils.filesystem;

import java.time.LocalDateTime;

public interface CFile {
    String name();
    Path path();
    FileType type();
    LocalDateTime modified();
}
