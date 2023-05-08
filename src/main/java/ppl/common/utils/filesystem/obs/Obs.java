package ppl.common.utils.filesystem.obs;

import com.obs.services.ObsClient;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import ppl.common.utils.filesystem.*;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Obs implements FileSystem {

    private static final Protocol protocol = Protocol.OBS;
    private final String bucket;
    private final ObsClient obsClient;
    private final Path.Creator pathCreator;
    private final AtomicBoolean closed = new AtomicBoolean();

    private final ObjectPool<PoolableConnection<ObsConnection>> pool;

    private Obs(ObsProperties obsProperties, Path.Creator pathCreator) {
        this.bucket = obsProperties.getBucket();
        this.obsClient = new ObsClient(obsProperties.getAk(), obsProperties.getSk(), obsProperties.getEndpoint());
        this.pathCreator = pathCreator;
        PoolableConnectionFactory<ObsConnection> factory = createPooledObjectFactory();
        ObjectPool<PoolableConnection<ObsConnection>> pool = createObjectPool(factory);
        factory.setPool(pool);
        this.pool = pool;
    }

    private PoolableConnectionFactory<ObsConnection> createPooledObjectFactory() {
        return new PoolableConnectionFactory<>(ObsConnection::new);
    }

    private ObjectPool<PoolableConnection<ObsConnection>> createObjectPool(PooledObjectFactory<PoolableConnection<ObsConnection>> factory) {
        return new GenericObjectPool<>(factory);
    }

    @Override
    public Connection getConnection() throws Exception {
        ensureOpen();
        return pool.borrowObject();
    }

    @Override
    public void close() throws Exception {
        if (this.closed.compareAndSet(false, true)) {
            this.obsClient.close();
            this.pool.close();
        }
    }

    private void ensureOpen() throws Exception {
        if (this.closed.get()) {
            throw new IOException("Filesystem is already closed.");
        }
    }

//    @Override
//    public void store(String remote, File local) {
//        remote = remote.trim();
//        Path remotePath = Paths.get(remote);
//        if (remotePath.isAbsolute()) {
//            log.warn("Absolute remote path is not allowed for storing. Change to relative path, automatically.");
//            remotePath = remotePath.subpath(0, remotePath.getNameCount());
//        }
//        Path p = workingPath.resolve(remotePath);
//
//        PutObjectRequest request = new PutObjectRequest();
//        request.setBucketName(bucket);
//        request.setObjectKey(ROOT.relativize(p).toString());
//        request.setFile(local);
//        try {
//            this.client.putObject(request);
//        } catch (com.obs.services.exception.ObsException e) {
//            throw new ObsException("Failed to store file " + p, e);
//        }
//    }
//
//    @Override
//    public List<String> listFiles(Instant day, boolean isDirectory) {
//        throw new UnsupportedOperationException("Cannot list files modified on some day.");
//    }
//
//    @Override
//    public void download(String remote, File local) {
//        Path p = workingPath.resolve(remote);
//        ObsObject obsObject = openForRead(p);
//        readIn(obsObject, local);
//    }
//
//    private ObsObject openForRead(Path p) {
//        try {
//            return this.client.getObject(bucket, ROOT.relativize(p).toString());
//        } catch (com.obs.services.exception.ObsException e) {
//            throw new ObsException("Failed to open " + p, e);
//        }
//    }
//
//    private void readIn(ObsObject obsObject, File localFile) {
//        try (InputStream content = obsObject.getObjectContent()) {
//            if (content != null) {
//                byte[] buf = new byte[1024];
//                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(localFile))) {
//                    int size = content.read(buf);
//                    while (size != -1) {
//                        bos.write(buf, 0, size);
//                        size = content.read(buf);
//                    }
//                }
//            }
//        } catch (IOException e) {
//            throw new ObsException("Failed to download file from obs", e);
//        }
//    }

    public static Obs create(FileSystemProperties fileSystemProperties,
                             Path.Creator morePathCreator) {
        if (!(fileSystemProperties instanceof ObsProperties)) {
            throw new IllegalArgumentException("Invalid configuration for protocol: " + protocol.getName());
        }

        return new Obs((ObsProperties) fileSystemProperties, morePathCreator);
    }

    private class ObsConnection implements Connection, Session, Validator {
        private volatile Path working;
        private final AtomicBoolean closed = new AtomicBoolean();

        public ObsConnection() {
            resetSession();
        }

        @Override
        public void resetSession() {
            this.working = pathCreator.create(FileSystem.C_ROOT_DIR);
        }

        @Override
        public Path pwd() throws IOException {
            ensureOpen();
            return this.working;
        }

        @Override
        public void cd(String pwd) throws IOException {
            ensureOpen();
            Path working = this.working;

            Path to = pathCreator.create(pwd);
            if (!to.isAbsolute()) {
                to = working.resolve(to);
            }
            this.working = to.normalize();
        }

        @Override
        public void store(String remote, File local) throws IOException {
            ensureOpen();
        }

        @Override
        public List<String> listFiles(Instant day, boolean isDirectory) throws IOException {
            ensureOpen();
            System.out.println("listFiles");
            return null;
        }

        @Override
        public void download(String remote, File local) throws IOException {
            ensureOpen();
        }

        @Override
        public void close() throws Exception {
            this.closed.compareAndSet(false, true);
        }

        @Override
        public void validate() throws Exception {
            ensureOpen();
        }

        private void ensureOpen() throws IOException {
            if (Obs.this.closed.get() || this.closed.get()) {
                throw new IOException("Already closed.");
            }
        }
    }

}
