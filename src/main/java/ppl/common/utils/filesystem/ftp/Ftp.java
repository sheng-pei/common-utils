//package ppl.common.utils.filesystem.ftp;
//
//import org.apache.commons.net.ftp.*;
//import ppl.common.utils.filesystem.FileSystemProperties;
//import ppl.common.utils.filesystem.Path;
//import ppl.common.utils.filesystem.Protocol;
//import ppl.common.utils.filesystem.FileSystem;
//
//import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.time.Instant;
//import java.time.temporal.ChronoUnit;
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class Ftp implements FileSystem, AutoCloseable {
//    private static final Protocol protocol = Protocol.FTP;
//
//    private final Path ROOT;
//    private final FTPClient ftp;
//    private final Set<String> SUPPORTED_COMMANDS = new HashSet<>();
//
//    private Ftp() {
//        this.ftp = new FTPClient();
//    }
//
//    private void connect(String server, int port) {
//        pConnect(server, port);
//        checkReply();
//    }
//
//    private void pConnect(String server, int port) {
//        try {
//            if (port > 0) {
//                ftp.connect(server, port);
//            } else {
//                ftp.connect(server);
//            }
//        } catch (IOException e) {
//            throw new FtpException("Failed to connect.", e);
//        }
//    }
//
//    private void checkReply() {
//        if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
//            throw new FtpException("Failed to connect.");
//        }
//    }
//
//    private void login(String username, String password) {
//        if (!pLogin(username, password)) {
//            throw new FtpException("Failed to login.");
//        }
//    }
//
//    private boolean pLogin(String username, String password) {
//        try {
//            return ftp.login(username, password);
//        } catch (IOException e) {
//            throw new FtpException("Failed to login.", e);
//        }
//    }
//
//    private void config(boolean isPassive) {
//        if (!isPassive) {
//            ftp.enterLocalActiveMode();
//        } else {
//            ftp.enterLocalPassiveMode();
//        }
//    }
//
//    @Override
//    public void cd(String path) {
//        if (!pChangeWorkingPath(path)) {
//            throw new FtpException("Failed to change working path.");
//        }
//    }
//
//    private boolean pChangeWorkingPath(String path) {
//        if (path == null) {
//            return true;
//        }
//
//        try {
//            return ftp.changeWorkingDirectory(path);
//        } catch (IOException e) {
//            throw new FtpException("Failed to change working path.", e);
//        }
//    }
//
//    @Override
//    public void store(String remote, File local) {
//        Path remotePath = Paths.get(remote.trim());
//        mkdirs(remotePath.getParent());
//        setFileType(FTP.BINARY_FILE_TYPE);
//        pStore(remote, local);
//    }
//
//    private void mkdirs(Path remote) {
//        for (Path path : remote) {
//            if (!pMakeDirectory(path)) {
//                throw new FtpException("Failed to make parent directory.");
//            }
//        }
//    }
//
//    private boolean pMakeDirectory(Path parent) {
//        try {
//            if (parent != null) {
//                return ftp.makeDirectory(parent.toString());
//            }
//            return true;
//        } catch (IOException e) {
//            throw new FtpException("Failed to make parent directory.", e);
//        }
//    }
//
//    private void setFileType(int fileType) {
//        if (!pSetFileType(fileType)) {
//            throw new FtpException("Failed to set binary file type.");
//        }
//    }
//
//    private boolean pSetFileType(int fileType) {
//        try {
//            return ftp.setFileType(fileType);
//        } catch (IOException e) {
//            throw new FtpException("Failed to set binary file type.", e);
//        }
//    }
//
//    private void pStore(String remote, File local) {
//        if (!ppStore(remote, local)) {
//            throw new FtpException("Failed to store file: " + remote);
//        }
//    }
//
//    private boolean ppStore(String remote, File local) {
//        try (final InputStream input = new FileInputStream(local)) {
//            return ftp.storeFile(remote, input);
//        } catch (IOException e) {
//            throw new FtpException("Failed to store file: " + remote, e);
//        }
//    }
//
//    @Override
//    public List<String> listFiles(Instant day, boolean isDirectory) {
//        return null;
////        try {
////            FTPFile[] ftpFiles = ftp.listFiles(".", file -> {
////                Calendar calendar = file.getTimestamp();
////                Instant instant = calendar.toInstant();
////                return instant.truncatedTo(ChronoUnit.DAYS).equals(day.truncatedTo(ChronoUnit.DAYS)) &&
////                        isDirectory == file.isDirectory();
////            });
////            return Arrays.stream(ftpFiles)
////                    .map(f -> Paths.get(f.getName()))
////                    .collect(Collectors.toList());
////        } catch (IOException e) {
////            throw new FtpException("Failed to list files.", e);
////        }
//    }
//
//    /**
//     *
//     * @param day
//     * @param isDirectory
//     * @return
//     */
//    public List<Path> fastListFiles(Instant day, boolean isDirectory) {
//        try {
//            FTPFile[] ftpFiles = ftp.listFiles(".", file -> {
//                Calendar calendar = file.getTimestamp();
//                Instant instant = calendar.toInstant();
//                return instant.truncatedTo(ChronoUnit.DAYS).equals(day.truncatedTo(ChronoUnit.DAYS)) &&
//                        isDirectory == file.isDirectory();
//            });
//            return Arrays.stream(ftpFiles)
//                    .map(f -> Paths.get(f.getName()))
//                    .collect(Collectors.toList());
//        } catch (IOException e) {
//            throw new FtpException("Failed to list files.", e);
//        }
//    }
//
//    @Override
//    public void download(String remote, File local) {
//        try (OutputStream os = new BufferedOutputStream(Files.newOutputStream(local.toPath()))) {
//            ftp.retrieveFile(remote, os);
//        } catch (IOException e) {
//            throw new FtpException(String.format(
//                    "Failed to download file: %s to local: %s", remote, local.getPath()), e);
//        }
//
//    }
//
//    @Override
//    public void close() {
//        try {
//            ftp.logout();
//            if (ftp.isConnected()) {
//                ftp.disconnect();
//            }
//        } catch (IOException e) {
//            throw new FtpException("Failed to close connection to ftp.");
//        }
//    }
//
//    public static FileSystem create(FileSystemProperties ftpProperties) {
//        protocol.ensure(ftpProperties);
//        FtpProperties props = (FtpProperties) ftpProperties;
//        Ftp ftp = new Ftp();
//        try {
//            ftp.connect(props.getServer(), props.getPort());
//            ftp.login(props.getUsername(), props.getPassword());
//            ftp.config(!props.getActive());
//            ftp.cd(props.getWorking());
//            return ftp;
//        } catch (Exception e) {
//            try {
//                ftp.close();
//            } catch (Exception e1) {
//                // do nothing
//            }
//            throw new FtpException("Failed to create ftp session.", e);
//        }
//    }
//
//}
