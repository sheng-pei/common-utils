package ppl.common.utils.filesystem.sftp;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppl.common.utils.filesystem.core.FileSystem;
import ppl.common.utils.filesystem.core.*;
import ppl.common.utils.filesystem.path.Path;
import ppl.common.utils.filesystem.path.Paths;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.jcraft.jsch.ChannelSftp.SSH_FX_FAILURE;

public class Sftp implements FileSystem, AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(Sftp.class);
    private final static Protocol protocol = Protocol.SFTP;

    public static Sftp create(FileSystemProperties properties) {
        if (!(properties instanceof SftpProperties)) {
            throw new SftpException("Invalid configuration for protocol: " + protocol.name());
        }

        SftpProperties sftpProperties = (SftpProperties) properties;
        Sftp sftp = new Sftp(sftpProperties);
        Session session = sftp.newSession();
        try {
            if (sftpProperties.isAutoCreateWorking()) {
                try (SimpleSftpClientWrapper sftpClient = new Sftp.SimpleSftpClientWrapper(session, sftpProperties.getCharset())) {
                    sftpClient.mkdirs(sftp.working);
                }
            }
        } finally {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
        return sftp;
    }

    private final AtomicBoolean shutdown = new AtomicBoolean();
    private final Map<Session, Set<Connection>> connections = new IdentityHashMap<>();

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final Path working;
    private final Charset charset;
    private final boolean channelFirst;
    private final int coreSession;
    private final int maxChannel;


    private Sftp(SftpProperties properties) {
        this.host = properties.getHost();
        this.port = properties.getPort();
        this.username = properties.getUsername();
        this.password = properties.getPassword();
        this.working = Paths.get(properties.getWorking())
                .normalize();
        this.charset = properties.getCharset();
        this.channelFirst = properties.isChannelFirst();
        this.coreSession = properties.getCoreSession();
        this.maxChannel = properties.getMaxChannel();
    }

    @Override
    public Connection getConnection() {
        checkShutdown();
        Connection connection;
        synchronized (connections) {
            checkShutdown();
            Map<Session, Set<Connection>> connections = this.connections;
            Session session = getSession(connections);
            if (session == null) {
                session = newSession();
            }
            connection = new Conn(session);
            Set<Connection> set = connections.computeIfAbsent(session, s -> new HashSet<>());
            set.add(connection);
        }
        return connection;
    }

    private Session getSession(Map<Session, Set<Connection>> connections) {
        Session[] sessions = connections.keySet()
                .toArray(new Session[0]);
        int sessionCount = sessions.length;
        int[] channelCounts = new int[sessions.length];
        for (int i = 0; i < sessionCount; i++) {
            channelCounts[i] = connections.get(sessions[i]).size();
        }
        if (channelFirst || sessionCount >= coreSession) {
            for (int i = 0; i < sessionCount; i++) {
                if (channelCounts[i] < maxChannel) {
                    return sessions[i];
                }
            }
        }
        return null;
    }

    private Session newSession() {
        Session session = null;
        try {
            session = new JSch().getSession(
                    username,
                    host,
                    port);
            session.setPassword(password);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect();
            return session;
        } catch (JSchException e) {
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
            throw new SftpException("Failed to open jsch session.", e);
        }
    }

    @Override
    public void close() {
        if (shutdown.compareAndSet(false, true)) {
            Map<Session, Set<Connection>> conns;
            synchronized (connections) {
                conns = new IdentityHashMap<>(connections);
                connections.clear();
            }
            for (Session session : conns.keySet()) {
                try {
                    conns.get(session)
                            .forEach(Connection::close);
                } finally {
                    if (session.isConnected()) {
                        session.disconnect();
                    }
                }
            }
        }
    }

    private void checkShutdown() {
        if (shutdown.get()) {
            throw new SftpException(String.format("Sftp '%s:%d' is already closed.", host, port));
        }
    }

    private class Conn implements Connection {

        private final Session session;
        private final SimpleSftpClientWrapper sftp;
        private final Path ancestor;
        private final AtomicBoolean shutdown = new AtomicBoolean();
        private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        private final ReentrantReadWriteLock.ReadLock rl = lock.readLock();
        private final ReentrantReadWriteLock.WriteLock wl = lock.writeLock();

        public Conn(Session session) {
            Path ancestor;
            SimpleSftpClientWrapper client = new SimpleSftpClientWrapper(session, charset);
            try {
                client.cd(working);
                ancestor = client.pwd();
            } catch (Throwable t) {
                try {
                    client.close();
                } catch (Throwable t1) {
                    t.addSuppressed(t1);
                }
                throw t;
            }
            this.sftp = client;
            this.session = session;
            this.ancestor = ancestor;
        }

        @Override
        public String actualPath(String path) {
            Sftp.this.checkShutdown();
            Path pwd = pwd();
            return pwd.resolve(path)
                    .normalize()
                    .toString();
        }

        @Override
        public String actualPath(Path path) {
            Sftp.this.checkShutdown();
            Path pwd = pwd();
            return pwd.resolve(path)
                    .normalize()
                    .toString();
        }

        @Override
        public Path pwd() {
            Sftp.this.checkShutdown();
            return lockedPwd();
        }

        private Path lockedPwd() {
            return readLocked(sftp::pwd);
        }

        @Override
        public void cd(String path) {
            Sftp.this.checkShutdown();
            Path w = Paths.get(path);
            lockedCheckPath(w);
            readLocked(() -> {
                sftp.cd(w);
                return null;
            });
        }

        @Override
        public void cd(Path path) {
            Sftp.this.checkShutdown();
            lockedCheckPath(path);
            readLocked(() -> {
                sftp.cd(path);
                return null;
            });
        }

        @Override
        public void store(String remote, File local) {
            Sftp.this.checkShutdown();
            ensurePath(remote);
            readLocked(() -> {
                sftp.store(remote, local);
                return null;
            });
        }

        @Override
        public void store(String remote, InputStream is) {
            Sftp.this.checkShutdown();
            ensurePath(remote);
            readLocked(() -> {
                sftp.store(remote, is);
                return null;
            });
        }

        private void ensurePath(String remote) {
            Path path = Paths.get(remote);
            Path[] ps = lockedCheckPath(path);
            lockedMkdirs(ps[0], ps[1].getParent());
        }

        @Override
        public void mkdir(Path path) {
            Sftp.this.checkShutdown();
            lockedCheckPath(path);
            readLocked(() -> {
                sftp.mkdir(path);
                return null;
            });
        }

        @Override
        public void mkdirs(Path path) {
            Sftp.this.checkShutdown();
            Path[] ps = lockedCheckPath(path);
            lockedMkdirs(ps[0], ps[1]);
        }

        private void lockedMkdirs(Path pwd, Path normalizedAbsolutePath) {
            readLocked(() -> {
                sftp.mkdirs(pwd, normalizedAbsolutePath);
                return null;
            });
        }

        @Override
        public List<CFile> listFiles() {
            Sftp.this.checkShutdown();
            Path pwd = lockedPwd();
            List<ChannelSftp.LsEntry> entries = readLocked(sftp::listFiles);
            return entries.stream()
                    .map(f -> new SftpFileAdapter(pwd, f))
                    .collect(Collectors.toList());
        }

        @Override
        public List<CFile> listFiles(LocalDateTime day, boolean isDirectory) {
            Sftp.this.checkShutdown();
            LocalDateTime d = day.truncatedTo(ChronoUnit.DAYS);
            Path pwd = lockedPwd();
            List<ChannelSftp.LsEntry> entries = readLocked(sftp::listFiles);
            return entries.stream()
                    .filter(Objects::nonNull)
                    .map(f -> new SftpFileAdapter(pwd, f))
                    .filter(f -> f.modified().truncatedTo(ChronoUnit.DAYS).equals(d))
                    .filter(f -> isDirectory == (f.type() == FileType.DIRECTORY))
                    .collect(Collectors.toList());
        }

        @Override
        public List<CFile> listFiles(Predicate<CFile> predicate) {
            Sftp.this.checkShutdown();
            Path pwd = lockedPwd();
            List<ChannelSftp.LsEntry> entries = readLocked(sftp::listFiles);
            return entries.stream()
                    .filter(Objects::nonNull)
                    .map(f -> new SftpFileAdapter(pwd, f))
                    .filter(predicate)
                    .collect(Collectors.toList());
        }

        @Override
        public void download(String remote, File local) {
            Sftp.this.checkShutdown();
            Path remotePath = Paths.get(remote);
            lockedCheckPath(remotePath);
            readLocked(() -> {
                sftp.download(remote, local);
                return null;
            });
        }

        @Override
        public void download(String remote, OutputStream os) {
            Sftp.this.checkShutdown();
            Path remotePath = Paths.get(remote);
            lockedCheckPath(remotePath);
            readLocked(() -> {
                sftp.download(remote, os);
                return null;
            });
        }

        private Path[] lockedCheckPath(Path path) {
            Path pwd = lockedPwd();
            Path p = checkPath(pwd, path);
            return new Path[]{pwd, p};
        }

        private Path checkPath(Path pwd, Path path) {
            Path p = pwd.resolve(path).normalize();
            if (!p.startsWith(ancestor)) {
                throw new SftpException(String.format("The ancestor of path '%s' is not '%s'.", p, ancestor));
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

        @Override
        public void close() {
            if (shutdown.compareAndSet(false, true)) {
                wl.lock();
                try {
                    sftp.close();
                } finally {
                    boolean needToCloseSession = false;
                    synchronized (connections) {
                        Map<Session, Set<Connection>> connections = Sftp.this.connections;
                        if (!connections.isEmpty()) {
                            Set<Connection> set = connections.get(session);
                            set.remove(this);
                            if (set.isEmpty()) {
                                connections.remove(session);
                                needToCloseSession = true;
                            }
                        }
                    }

                    if (needToCloseSession) {
                        if (session.isConnected()) {
                            session.disconnect();
                        }
                    }
                    wl.unlock();
                }
            }
        }

        private void checkShutdown() {
            if (shutdown.get()) {
                throw new SftpException(String.format(
                        "Sftp connection '%s:%d' is already closed.", host, port));
            }
        }
    }

    private static class SimpleSftpClientWrapper implements AutoCloseable {
        private final ChannelSftp sftp;

        public SimpleSftpClientWrapper(Session session, Charset charset) {
            Channel channel = null;
            try {
                channel = session.openChannel("sftp");
                channel.connect();
                if (charset != null) {
                    try {
                        ((ChannelSftp) channel).setFilenameEncoding(charset.name());
                    } catch (com.jcraft.jsch.SftpException e) {
                        log.warn("Use default encoding.");
                    }
                }
            } catch (JSchException e) {
                if (channel != null && channel.isConnected()) {
                    channel.disconnect();
                }
                throw new SftpException("Failed to open sftp channel.", e);
            }
            this.sftp = (ChannelSftp) channel;
        }

        public Path pwd() {
            String string;
            try {
                string = sftp.pwd();
            } catch (com.jcraft.jsch.SftpException e) {
                throw new SftpException("Couldn't pwd.", e);
            }
            return Paths.get(string).normalize();
        }

        public void cd(Path path) {
            try {
                sftp.cd(path.toString());
            } catch (com.jcraft.jsch.SftpException e) {
                throw new SftpException("Failed to change working path.", e);
            }
        }

        public void store(String remote, File local) {
            try (InputStream is = Files.newInputStream(local.toPath())) {
                sftp.put(is, remote);
            } catch (IOException e) {
                throw new SftpException("Local file not found: " + local.getAbsolutePath(), e);
            } catch (com.jcraft.jsch.SftpException e) {
                throw new SftpException(String.format(
                        "Failed to store local file: %s to remote: %s", local.getAbsolutePath(), remote), e);
            }
        }

        public void store(String remote, InputStream is) {
            try {
                sftp.put(is, remote);
            } catch (com.jcraft.jsch.SftpException e) {
                throw new SftpException(String.format(
                        "Failed to store file to remote: %s", remote), e);
            }
        }

        public void mkdirs(Path path) {
            Path pwd = pwd();
            Path an = pwd.resolve(path).normalize();
            mkdirs(pwd, an);
        }

        public void mkdirs(Path pwd, Path absolutePath) {
            Path created = Paths.get(Path.C_ROOT_DIR);
            Path toBeCreated = created.relativize(absolutePath);
            if (absolutePath.startsWith(pwd)) {
                created = pwd;
                toBeCreated = pwd.relativize(absolutePath);
            }

            boolean success = false;
            for (Path path : toBeCreated) {
                created = created.resolve(path);
                success = pMkdir(created);
            }

            if (!success) {
                try {
                    sftp.stat(absolutePath.toString());
                } catch (com.jcraft.jsch.SftpException e) {
                    throw new SftpException(String.format("Failed to create '%s'", absolutePath), e);
                }
            }
        }

        public void mkdir(Path dir) {
            if (!pMkdir(dir)) {
                try {
                    sftp.stat(dir.toString());
                } catch (com.jcraft.jsch.SftpException e) {
                    throw new SftpException(String.format("Failed to create '%s'", dir), e);
                }
            }
        }

        private boolean pMkdir(Path dir) {
            try {
                sftp.mkdir(dir.toString());
                return true;
            } catch (com.jcraft.jsch.SftpException e) {
                if (e.id != SSH_FX_FAILURE) {
                    throw new SftpException(String.format("Failed to create '%s'", dir), e);
                }
            }
            return false;
        }

        public List<ChannelSftp.LsEntry> listFiles() {
            try {
                @SuppressWarnings("unchecked")
                Vector<ChannelSftp.LsEntry> vector = (Vector<ChannelSftp.LsEntry>) sftp.ls(".");
                return vector.stream()
                        .filter(e -> {
                            String name = e.getFilename();
                            return !name.equals(Path.C_CURRENT_DIR) && !name.equals(Path.C_PARENT_DIR);
                        })
                        .collect(Collectors.toList());
            } catch (com.jcraft.jsch.SftpException e) {
                throw new SftpException("Failed to list files.", e);
            }
        }

        public void download(String remote, File local) {
            try (OutputStream os = new BufferedOutputStream(Files.newOutputStream(local.toPath()))) {
                sftp.get(remote, os);
            } catch (IOException e) {
                throw new SftpException("File not found: " + local.getAbsolutePath(), e);
            } catch (com.jcraft.jsch.SftpException e) {
                throw new SftpException(String.format(
                        "Failed to download file: %s to local: %s", remote, local.getPath()), e);
            }
        }

        public void download(String remote, OutputStream os) {
            try {
                sftp.get(remote, os);
            } catch (com.jcraft.jsch.SftpException e) {
                throw new SftpException(String.format(
                        "Failed to download file: %s.", remote), e);
            }
        }

        @Override
        public void close() {
            if (sftp.isConnected()) {
                sftp.disconnect();
            }
        }

    }

}
