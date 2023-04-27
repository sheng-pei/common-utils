package ppl.common.utils.filesystem;

//import ppl.common.utils.filesystem.ftp.Ftp;
import ppl.common.utils.filesystem.obs.Obs;
//import ppl.common.utils.filesystem.sftp.Sftp;

import java.util.function.BiFunction;
import java.util.function.Function;

public enum Protocol {

//    FTP("ftp", Ftp::create, BasePath::new),
//    SFTP("sftp", Sftp::create, BasePath::new),
    OBS("obs", Obs::create, BasePath::get, BasePath::get);

    private final String name;
    private final Creator creator;
    private final Path.Creator pathCreator;
    private final Path.MoreCreator morePathCreator;

    Protocol(String name,
             Creator creator,
             Path.Creator pathCreator,
             Path.MoreCreator morePathCreator) {
        this.name = name;
        this.creator = creator;
        this.pathCreator = pathCreator;
        this.morePathCreator = morePathCreator;
    }

    public String getName() {
        return name;
    }

    public FileSystem open(FileSystemProperties fileSystemProperties) {
        return this.creator.create(fileSystemProperties, pathCreator, morePathCreator);
    }

    interface Creator {
        FileSystem create(FileSystemProperties properties, Path.Creator pathCreator, Path.MoreCreator morePathCreator);
    }

}
