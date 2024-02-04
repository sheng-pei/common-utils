//package ppl.common.utils.filesystem.sftp;
//
//import com.jcraft.jsch.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import ppl.common.utils.filesystem.FileSystemProperties;
//import ppl.common.utils.filesystem.Protocol;
//import ppl.common.utils.filesystem.FileSystem;
//
//import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.time.Instant;
//import java.time.temporal.ChronoUnit;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Properties;
//import java.util.Vector;
//
//public class Sftp implements FileSystem, AutoCloseable {
//
//    private static final Logger log = LoggerFactory.getLogger(Sftp.class);
//    private final static Protocol protocol = Protocol.SFTP;
//    private static final String CURRENT_DIR = ".";
//    private static final String PARENT_DIR = "..";
//
//    private final Session session;
//    private final ChannelSftp channel;
//
//    private Sftp(String host, int port, String user, String password) {
//        Session session = null;
//        Channel channel = null;
//        try {
//            session = new JSch().getSession(user, host, port);
//            session.setPassword(password);
//
//            Properties config = new Properties();
//            config.put("StrictHostKeyChecking", "no");
//            session.setConfig(config);
//
//            session.connect();
//
//            channel = session.openChannel("sftp");
//            channel.connect();
//        } catch (JSchException e) {
//            close(channel, session);
//            throw new SftpException("Failed to open sftp channel.", e);
//        }
//
//        this.session = session;
//        this.channel = (ChannelSftp) channel;
//    }
//
//    @Override
//    public void cd(String path) {
//        if (path == null) {
//            return;
//        }
//
//        try {
//            channel.cd(path);
//        } catch (com.jcraft.jsch.SftpException e) {
//            throw new SftpException("Failed to change working path.", e);
//        }
//    }
//
//    @Override
//    public void store(String remote, File local) {
//        remote = remote.trim();
//        Path remotePath = Paths.get(remote);
//        if (remotePath.isAbsolute()) {
//            log.warn("Absolute remote path is not allowed for storing. Change to relative path, automatically.");
//            remotePath = remotePath.subpath(0, remotePath.getNameCount());
//        }
//
//        try {
//            mkdirs(remotePath.getParent());
//        } catch (com.jcraft.jsch.SftpException e) {
//            throw new SftpException("Couldn't make parent directory: " + remotePath.getParent(), e);
//        }
//
//        try (InputStream is = Files.newInputStream(local.toPath())) {
//            channel.put(is, remotePath.toString());
//        } catch (IOException e) {
//            throw new SftpException("Local file not found: " + local.getAbsolutePath(), e);
//        } catch (com.jcraft.jsch.SftpException e) {
//            throw new SftpException(String.format(
//                    "Failed to store local file: %s to remote: %s", local.getAbsolutePath(), remotePath), e);
//        }
//    }
//
//    private void mkdirs(Path remotePath) throws com.jcraft.jsch.SftpException {
//        for (Path path : remotePath) {
//            channel.mkdir(path.toString());
//        }
//    }
//
//    @Override
//    public List<String> listFiles(Instant day, boolean isDirectory) {
//        List<String> ret = new ArrayList<>();
//
//        Vector<ChannelSftp.LsEntry> vector;
//        try {
//            vector = channel.ls(CURRENT_DIR);
//        } catch (com.jcraft.jsch.SftpException e) {
//            throw new SftpException("Failed to list file in current directory.", e);
//        }
//
//        for (ChannelSftp.LsEntry entry : vector) {
//            if (CURRENT_DIR.equals(entry.getFilename()) || PARENT_DIR.equals(entry.getFilename())) {
//                continue;
//            }
//
//            long seconds = entry.getAttrs().getMTime();
//            Instant modify = Instant.ofEpochSecond(seconds);
//
//            if (modify.truncatedTo(ChronoUnit.DAYS).equals(day.truncatedTo(ChronoUnit.DAYS)) &&
//                    isDirectory == entry.getAttrs().isDir()) {
//                ret.add(entry.getFilename());
//            }
//        }
//        return ret;
//    }
//
//    @Override
//    public void download(String remote, File local) {
//        try (OutputStream os = new BufferedOutputStream(Files.newOutputStream(local.toPath()))) {
//            channel.get(remote, os);
//        } catch (IOException e) {
//            throw new SftpException("File not found: " + local.getAbsolutePath(), e);
//        } catch (com.jcraft.jsch.SftpException e) {
//            throw new SftpException(String.format(
//                    "Failed to download file: %s to local: %s", remote, local.getPath()), e);
//        }
//    }
//
//    @Override
//    public void close() {
//        close(this.channel, this.session);
//    }
//
//    private static void close(Channel channel, Session session) {
//        if (channel != null && channel.isConnected()) {
//            channel.disconnect();
//        }
//        if (session != null && session.isConnected()) {
//            session.disconnect();
//        }
//    }
//
//    public static Sftp create(FileSystemProperties fileSystemProperties) {
//        protocol.ensure(fileSystemProperties);
//
//        SftpProperties sftpProperties = (SftpProperties) fileSystemProperties;
//        Sftp sftp = new Sftp(sftpProperties.getServer(), sftpProperties.getPort(),
//                sftpProperties.getUsername(), sftpProperties.getPassword());
//        sftp.cd(sftpProperties.getWorking());
//        return sftp;
//    }
//
//}
