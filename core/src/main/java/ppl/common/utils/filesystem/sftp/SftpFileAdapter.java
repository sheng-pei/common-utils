package ppl.common.utils.filesystem.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import ppl.common.utils.filesystem.core.CFile;
import ppl.common.utils.filesystem.core.FileType;
import ppl.common.utils.filesystem.ftp.FtpException;
import ppl.common.utils.filesystem.path.Path;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class SftpFileAdapter implements CFile {

    private final Path pwd;
    private final ChannelSftp.LsEntry entry;

    public SftpFileAdapter(Path pwd, ChannelSftp.LsEntry entry) {
        this.pwd = pwd;
        this.entry = entry;
    }

    @Override
    public String name() {
        return entry.getFilename();
    }

    @Override
    public Path path() {
        return pwd.resolve(entry.getFilename());
    }

    @Override
    public FileType type() {
        SftpATTRS attrs = entry.getAttrs();
        if (attrs.isReg()) {
            return FileType.FILE;
        } else if (attrs.isDir()) {
            return FileType.DIRECTORY;
        }
        throw new FtpException("Unsupported file type.");
    }

    @Override
    public LocalDateTime modified() {
        SftpATTRS attrs = entry.getAttrs();
        int time = attrs.getMTime();
        Instant instant = Instant.ofEpochSecond(time);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    @Override
    public ZonedDateTime modified(ZoneId zoneId) {
        SftpATTRS attrs = entry.getAttrs();
        int time = attrs.getMTime();
        Instant instant = Instant.ofEpochSecond(time);
        return ZonedDateTime.ofInstant(instant, zoneId);
    }

    @Override
    public String toString() {
        return "SftpFileAdapter{" +
                "pwd=" + pwd +
                ", name=" + name() +
                ", type=" + type() +
                ", modified=" + modified() +
                '}';
    }
}
