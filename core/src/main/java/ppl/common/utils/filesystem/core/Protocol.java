package ppl.common.utils.filesystem.core;

import ppl.common.utils.filesystem.ftp.Ftp;
//import ppl.common.utils.filesystem.obs.Obs;
import ppl.common.utils.filesystem.obs.Obs;
import ppl.common.utils.filesystem.sftp.Sftp;

public enum Protocol {

    FTP("ftp", Ftp::create),
    SFTP("sftp",Sftp::create),
    OBS("obs", Obs::create);

    private final String name;
    private final Creator creator;

    Protocol(String name, Creator creator) {
        this.name = name;
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public FileSystem open(FileSystemProperties properties) {
        return this.creator.create(properties);
    }

    interface Creator {
        FileSystem create(FileSystemProperties properties);
    }

}
