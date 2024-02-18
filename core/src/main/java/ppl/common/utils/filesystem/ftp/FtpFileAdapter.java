package ppl.common.utils.filesystem.ftp;

import org.apache.commons.net.ftp.FTPFile;
import ppl.common.utils.filesystem.core.CFile;
import ppl.common.utils.filesystem.core.FileType;
import ppl.common.utils.filesystem.path.Path;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.TimeZone;

public class FtpFileAdapter implements CFile {

    private final Path pwd;
    private final FTPFile file;

    public FtpFileAdapter(Path pwd, FTPFile file) {
        this.pwd = pwd;
        this.file = file;
    }

    @Override
    public String name() {
        return file.getName();
    }

    @Override
    public Path path() {
        return pwd.resolve(file.getName());
    }

    @Override
    public FileType type() {
        if (file.isFile()) {
            return FileType.FILE;
        } else if (file.isDirectory()) {
            return FileType.DIRECTORY;
        }
        throw new FtpException("Unsupported file type.");
    }

    @Override
    public LocalDateTime modified() {
        Calendar calendar = file.getTimestamp();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault());
    }

    @Override
    public ZonedDateTime modified(ZoneId zoneId) {
        Calendar calendar = file.getTimestamp();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return ZonedDateTime.ofInstant(calendar.toInstant(), zoneId);
    }

    @Override
    public String toString() {
        return "FtpFileAdapter{" +
                "pwd=" + pwd +
                ", name=" + name() +
                ", type=" + type() +
                ", modified=" + modified() +
                '}';
    }
}
