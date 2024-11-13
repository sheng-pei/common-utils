package ppl.common.utils.filesystem.core;

import ppl.common.utils.filesystem.path.Path;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public interface CFile {
    String name();

    Path path();

    FileType type();

    LocalDateTime modified();

    ZonedDateTime modified(ZoneId zoneId);
}
