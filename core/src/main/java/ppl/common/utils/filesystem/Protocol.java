package ppl.common.utils.filesystem;

//import ppl.common.utils.filesystem.ftp.Ftp;
import ppl.common.utils.filesystem.obs.Obs;
import ppl.common.utils.filesystem.path.BasePath;
//import ppl.common.utils.filesystem.sftp.Sftp;


public enum Protocol {

//    FTP("ftp", Ftp::create, BasePath::new),
//    SFTP("sftp", Sftp::create, BasePath::new),
    OBS("obs", Obs::create, BasePath::get);

    private final String name;
    private final Creator creator;
    private final Path.Creator pathCreator;

    Protocol(String name,
             Creator creator,
             Path.Creator pathCreator) {
        this.name = name;
        this.creator = creator;
        this.pathCreator = pathCreator;
    }

    public String getName() {
        return name;
    }

    public FileSystem open(FileSystemProperties fileSystemProperties) {
        return this.creator.create(fileSystemProperties, pathCreator);
    }

    interface Creator {
        FileSystem create(FileSystemProperties properties, Path.Creator pathCreator);
    }

}
