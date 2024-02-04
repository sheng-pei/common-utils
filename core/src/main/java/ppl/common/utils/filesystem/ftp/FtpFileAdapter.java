//package ppl.common.utils.filesystem.ftp;
//
//import org.apache.commons.net.ftp.FTPFile;
//import ppl.common.utils.exception.UnreachableCodeException;
//import ppl.common.utils.filesystem.CFile;
//import ppl.common.utils.filesystem.FileType;
//
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.time.LocalDateTime;
//import java.util.Calendar;
//
//public class FtpFileAdapter implements CFile {
//
//    private final String parent;
//    private final FTPFile file;
//
//    private FtpFileAdapter(String parent, FTPFile file) {
//        this.parent = parent;
//        this.file = file;
//    }
//
//    @Override
//    public String name() {
//        return file.getName();
//    }
//
//    @Override
//    public Path path() {
//        return Paths.get(parent, file.getName());
//    }
//
//    @Override
//    public FileType type() {
//        if (file.isFile()) {
//            return FileType.FILE;
//        } else if (file.isDirectory()) {
//            return FileType.DIRECTORY;
//        }
//        throw new FtpException("Unsupported file type.");
//    }
//
//    @Override
//    public LocalDateTime modified() {
//        Calendar calendar = file.getTimestamp();
//        return null;
//    }
//}
