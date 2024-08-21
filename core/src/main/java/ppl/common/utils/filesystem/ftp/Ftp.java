package ppl.common.utils.filesystem.ftp;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppl.common.utils.filesystem.core.FileSystem;
import ppl.common.utils.filesystem.core.*;
import ppl.common.utils.filesystem.path.Path;
import ppl.common.utils.filesystem.path.Paths;
import ppl.common.utils.string.Strings;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Ftp implements FileSystem, AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(Ftp.class);
    private static final Protocol protocol = Protocol.FTP;

    public static Ftp create(FileSystemProperties properties) {
        if (!(properties instanceof FtpProperties)) {
            throw new FtpException("Invalid configuration for protocol: " + protocol.name());
        }

        FtpProperties ftpProperties = (FtpProperties) properties;
        Ftp ftp = new Ftp(ftpProperties);
        try (SimpleFTPClientWrapper client = ftp.new SimpleFTPClientWrapper()) {
            client.init();
            if (ftpProperties.isAutoCreateWorking()) {
                client.autoCreateWorking();
            }
            Set<String> commands = client.help();
            ftp.commands.addAll(commands);
        }
        return ftp;
    }

    private final Set<String> commands = new HashSet<>();

    private final AtomicBoolean shutdown = new AtomicBoolean();
    private final Set<Connection> connections = new HashSet<>();

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final boolean active;
    private final Path working;
    private final Charset charset;

    private Ftp(FtpProperties prop) {
        this.host = prop.getHost();
        this.port = prop.getPort();
        this.username = prop.getUsername();
        this.password = prop.getPassword();
        this.active = prop.isActive();
        this.working = Paths.get(prop.getWorking())
                .normalize();
        this.charset = prop.getCharset() == null ? StandardCharsets.UTF_8 : prop.getCharset();
    }

    @Override
    public Connection getConnection() {
        checkShutdown();
        Connection connection;
        synchronized (connections) {
            checkShutdown();
            connection = new Conn(this);
            connections.add(connection);
        }
        return connection;
    }

    @Override
    public void close() {
        if (shutdown.compareAndSet(false, true)) {
            Connection[] conns;
            synchronized (connections) {
                conns = connections.toArray(new Connection[0]);
                connections.clear();
            }
            for (Connection conn : conns) {
                conn.close();
            }
        }
    }

    private void checkShutdown() {
        if (shutdown.get()) {
            throw new FtpException(String.format("Ftp '%s:%d' is already closed.", host, port));
        }
    }

    private class Conn implements Connection {

        private final SimpleFTPClientWrapper ftp;
        private final Path ancestor;
        private final AtomicBoolean shutdown = new AtomicBoolean();
        private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        private final ReentrantReadWriteLock.ReadLock rl = lock.readLock();
        private final ReentrantReadWriteLock.WriteLock wl = lock.writeLock();

        private Conn(Ftp ftp) {
            SimpleFTPClientWrapper client = ftp.new SimpleFTPClientWrapper();
            Path ancestor;
            try {
                client.init();
                client.cd(working);
                ancestor = client.pwd();
            } catch (Throwable t) {
                try {
                    ftp.close();
                } catch (Throwable t1) {
                    t.addSuppressed(t1);
                }
                throw t;
            }
            this.ftp = client;
            this.ancestor = ancestor;
        }

        @Override
        public String actualPath(String path) {
            Ftp.this.checkShutdown();
            Path pwd = pwd();
            return pwd.resolve(path)
                    .normalize()
                    .toString();
        }

        @Override
        public String actualPath(Path path) {
            Ftp.this.checkShutdown();
            Path pwd = pwd();
            return pwd.resolve(path)
                    .normalize()
                    .toString();
        }

        @Override
        public Path pwd() {
            Ftp.this.checkShutdown();
            return lockedPwd();
        }

        private Path lockedPwd() {
            return readLocked(ftp::pwd);
        }

        @Override
        public void cd(String path) {
            Ftp.this.checkShutdown();
            Path w = Paths.get(path);
            lockedCheckPath(w);
            readLocked(() -> {
                ftp.cd(w);
                return null;
            });
        }

        @Override
        public void cd(Path path) {
            Ftp.this.checkShutdown();
            lockedCheckPath(path);
            readLocked(() -> {
                ftp.cd(path);
                return null;
            });
        }

        @Override
        public void store(String remote, File local) {
            Ftp.this.checkShutdown();
            Path r = Paths.get(remote);
            Path[] ps = lockedCheckPath(r);
            lockedMkdirs(ps[0], ps[1]);
            readLocked(() -> {
                ftp.setFileType(FTP.BINARY_FILE_TYPE);
                ftp.store(remote, local);
                return null;
            });
        }

        @Override
        public void store(String remote, InputStream is) {
            Ftp.this.checkShutdown();
            Path r = Paths.get(remote);
            Path[] ps = lockedCheckPath(r);
            lockedMkdirs(ps[0], ps[1]);
            readLocked(() -> {
                ftp.setFileType(FTP.BINARY_FILE_TYPE);
                ftp.store(remote, is);
                return null;
            });
        }

        @Override
        public void mkdir(Path path) {
            Ftp.this.checkShutdown();
            lockedCheckPath(path);
            lockedMkdir(path);
        }

        private void lockedMkdir(Path path) {
            readLocked(() -> {
                ftp.mkdir(path);
                return null;
            });
        }

        @Override
        public void mkdirs(Path path) {
            Ftp.this.checkShutdown();
            Path[] ps = lockedCheckPath(path);
            lockedMkdirs(ps[0], ps[1]);
        }

        private void lockedMkdirs(Path pwd, Path normalizedAbsolutePath) {
            readLocked(() -> {
                ftp.mkdirs(pwd, normalizedAbsolutePath);
                return null;
            });
        }

        @Override
        public List<CFile> listFiles() {
            Ftp.this.checkShutdown();
            Path pwd = pwd();
            FTPFile[] ftpFiles = ftp.listFiles();
            return Arrays.stream(ftpFiles)
                    .map(f -> new FtpFileAdapter(pwd, f))
                    .collect(Collectors.toList());
        }

        @Override
        public List<CFile> listFiles(LocalDateTime day, boolean isDirectory) {
            Ftp.this.checkShutdown();
            LocalDateTime d = day.truncatedTo(ChronoUnit.DAYS);
            Path pwd = pwd();
            FTPFile[] ftpFiles = ftp.listFiles();
            return Arrays.stream(ftpFiles)
                    .filter(Objects::nonNull)
                    .map(f -> new FtpFileAdapter(pwd, f))
                    .filter(f -> f.modified().truncatedTo(ChronoUnit.DAYS).equals(d))
                    .filter(f -> isDirectory == (f.type() == FileType.DIRECTORY))
                    .collect(Collectors.toList());
        }

        @Override
        public List<CFile> listFiles(Predicate<CFile> predicate) {
            Ftp.this.checkShutdown();
            Path pwd = pwd();
            FTPFile[] ftpFiles = ftp.listFiles();
            return Arrays.stream(ftpFiles)
                    .filter(Objects::nonNull)
                    .map(f -> new FtpFileAdapter(pwd, f))
                    .filter(predicate)
                    .collect(Collectors.toList());
        }

        @Override
        public void download(String remote, File local) {
            Ftp.this.checkShutdown();
            Path remotePath = Paths.get(remote);
            lockedCheckPath(remotePath);
            readLocked(() -> {
                ftp.download(remote, local);
                return null;
            });
        }

        @Override
        public void download(String remote, OutputStream os) {
            Ftp.this.checkShutdown();
            Path remotePath = Paths.get(remote);
            lockedCheckPath(remotePath);
            readLocked(() -> {
                ftp.download(remote, os);
                return null;
            });
        }

        @Override
        public void close() {
            if (shutdown.compareAndSet(false, true)) {
                wl.lock();
                try {
                    ftp.close();
                } finally {
                    synchronized (connections) {
                        connections.remove(this);
                    }
                    wl.unlock();
                }
            }
        }

        private Path[] lockedCheckPath(Path path) {
            Path pwd = lockedPwd();
            Path p = checkPath(pwd, path);
            return new Path[] {pwd, p};
        }

        private Path checkPath(Path pwd, Path path) {
            Path p = pwd.resolve(path).normalize();
            if (!p.startsWith(ancestor)) {
                throw new FtpException(String.format("The ancestor of path '%s' is not '%s'.", p, ancestor));
            }
            return p;
        }

        private <R> R readLocked(Supplier<R> supplier) {
            checkShutdown();
            rl.lock();
            try {
                checkShutdown();
                return supplier.get();
            } finally {
                rl.unlock();
            }
        }

        private void checkShutdown() {
            if (shutdown.get()) {
                throw new FtpException(String.format(
                        "Ftp connection '%s:%d' is already closed.", host, port));
            }
        }
    }

    private class SimpleFTPClientWrapper implements AutoCloseable {
        private final FTPClient ftp;

        public SimpleFTPClientWrapper() {
            FTPClient ftp = new FTPClient();
            ftp.setControlEncoding(Ftp.this.charset.name());
            this.ftp = ftp;
        }

        public void init() {
            try {
                connect(host, port);
                login(username, password);
                config(!active);
            } catch (Exception e) {
                try {
                    close();
                } catch (Exception e1) {
                    // do nothing
                }
                throw new FtpException("Failed to initialize ftp session.", e);
            }
        }

        private void connect(String server, int port) {
            pConnect(server, port);
            pCheckReply();
        }

        private void pConnect(String server, int port) {
            try {
                if (port > 0) {
                    ftp.connect(server, port);
                } else {
                    ftp.connect(server);
                }
            } catch (IOException e) {
                throw new FtpException("Failed to connect.", e);
            }
        }

        private void pCheckReply() {
            if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                throw new FtpException("Failed to connect.");
            }
        }

        private void login(String username, String password) {
            if (!pLogin(username, password)) {
                throw new FtpException("Failed to login.");
            }
        }

        private boolean pLogin(String username, String password) {
            try {
                return ftp.login(username, password);
            } catch (IOException e) {
                throw new FtpException("Failed to login.", e);
            }
        }

        private void config(boolean isPassive) {
            if (!isPassive) {
                ftp.enterLocalActiveMode();
            } else {
                ftp.enterLocalPassiveMode();
            }
        }

        public Path pwd() {
            String string = pPwd();
            if (string == null) {
                throw new FtpException("Couldn't pwd.");
            }
            return Paths.get(string).normalize();
        }

        private String pPwd() {
            try {
                return ftp.printWorkingDirectory();
            } catch (IOException e) {
                throw new FtpException("Pwd error.", e);
            }
        }

        public void cd(Path path) {
            if (!pCd(path)) {
                throw new FtpException(String.format(
                        "Cd error. The target '%s' is not exists or not a directory.", path));
            }
        }

        private boolean pCd(Path path) {
            try {
                return ftp.changeWorkingDirectory(path.toString());
            } catch (IOException e) {
                throw new FtpException("Cd error.", e);
            }
        }

        public void setFileType(int fileType) {
            if (!pSetFileType(fileType)) {
                throw new FtpException("Failed to set binary file type.");
            }
        }

        private boolean pSetFileType(int fileType) {
            try {
                return ftp.setFileType(fileType);
            } catch (IOException e) {
                throw new FtpException("Failed to set binary file type.", e);
            }
        }

        public void store(String remote, File local) {
            if (!pStore(remote, local)) {
                throw new FtpException("Failed to store file: " + remote);
            }
        }

        public void store(String remote, InputStream is) {
            if (!pStore(remote, is)) {
                throw new FtpException("Failed to store file: " + remote);
            }
        }

        private boolean pStore(String remote, File local) {
            try (final InputStream input = Files.newInputStream(local.toPath())) {
                return ftp.storeFile(remote, input);
            } catch (IOException e) {
                throw new FtpException("Failed to store file: " + remote, e);
            }
        }

        private boolean pStore(String remote, InputStream is) {
            try {
                return ftp.storeFile(remote, is);
            } catch (IOException e) {
                throw new FtpException("Failed to store file: " + remote, e);
            }
        }

        public void mkdirs(Path pwd, Path absolutePath) {
            Path created = Paths.get(Path.C_ROOT_DIR);
            Path toBeCreated = created.relativize(absolutePath);
            if (absolutePath.startsWith(pwd)) {
                created = pwd;
                toBeCreated = pwd.relativize(absolutePath);
            }

            for (Path path : toBeCreated) {
                created = created.resolve(path);
                pMkdir(created);
            }

            try {
                String status = ftp.getStatus(absolutePath.toString());
                if (status == null) {
                    throw new FtpException(String.format("Failed to create '%s'", absolutePath));
                }
            } catch (IOException e) {
                throw new FtpException("Mkdirs error.", e);
            }
        }

        public void mkdir(Path dir) {
            pMkdir(dir);

            try {
                String status = ftp.getStatus(dir.toString());
                if (status == null) {
                    throw new FtpException(String.format("Failed to create '%s'", dir));
                }
            } catch (IOException e) {
                throw new FtpException("Mkdir error.", e);
            }
        }

        private void pMkdir(Path dir) {
            try {
                if (dir != null) {
                    ftp.makeDirectory(dir.toString());
                }
            } catch (IOException e) {
                throw new FtpException(String.format(
                        "Failed to make directory: '%s'.", dir), e);
            }
        }

        public FTPFile[] listFiles() {
            try {
                if (commands.contains("MLSD") || hasFeature("MLSD") || hasFeature("MLST")) {
                    return ftp.mlistDir();
                } else {
                    log.warn("Use old command 'LIST' to list files in current working directory.");
                    return ftp.listFiles();
                }
            } catch (IOException e) {
                throw new FtpException("Failed to list files.", e);
            }
        }

        public void download(String remote, File local) {
            if (!pDownload(remote, local)) {
                throw new FtpException(String.format(
                        "Failed to download file: %s to local: %s", remote, local.getPath()));
            }
        }

        public void download(String remote, OutputStream os) {
            if (!pDownload(remote, os)) {
                throw new FtpException(String.format(
                        "Failed to download file: %s.", remote));
            }
        }

        private boolean pDownload(String remote, File local) {
            try (OutputStream os = new BufferedOutputStream(Files.newOutputStream(local.toPath()))) {
                return ftp.retrieveFile(remote, os);
            } catch (IOException e) {
                throw new FtpException(String.format(
                        "Failed to download file: %s to local: %s", remote, local.getPath()), e);
            }
        }

        private boolean pDownload(String remote, OutputStream os) {
            try {
                return ftp.retrieveFile(remote, os);
            } catch (IOException e) {
                throw new FtpException(String.format(
                        "Failed to download file: %s.", remote), e);
            }
        }

        @Override
        public void close() {
            try {
                ftp.logout();
                if (ftp.isConnected()) {
                    ftp.disconnect();
                }
            } catch (IOException e) {
                throw new FtpException("Failed to close connection to ftp.");
            }
        }

        public void autoCreateWorking() {
            Path pwd = pwd();
            Path an = pwd.resolve(working).normalize();
            mkdirs(pwd, an);
        }

        public Set<String> help() {
            try {
                String help = ftp.listHelp();
                Set<String> res = new HashSet<>();
                try (BufferedReader reader = new BufferedReader(new StringReader(help))) {
                    String line = reader.readLine();
                    while (line != null) {
                        if (!line.startsWith("214")) {
                            String[] cs = Strings.split(line, " +");
                            res.addAll(Arrays.stream(cs)
                                    .filter(Strings::isNotBlank)
                                    .collect(Collectors.toList()));
                        }
                        line = reader.readLine();
                    }
                }
                return res;
            } catch (IOException e) {
                throw new FtpException("Failed to list command.", e);
            }
        }

        public boolean hasFeature(String feature) {
            try {
                return ftp.hasFeature(feature);
            } catch (IOException e) {
                throw new FtpException(String.format("Failed to check feature: %s.", feature), e);
            }
        }
    }

}
