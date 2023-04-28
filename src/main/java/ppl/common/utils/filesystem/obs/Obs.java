package ppl.common.utils.filesystem.obs;

import com.obs.services.ObsClient;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppl.common.utils.filesystem.*;

import java.io.File;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

public class Obs implements FileSystem {

    private static final Protocol protocol = Protocol.OBS;

    private final String endpoint;
    private final String ak;
    private final String sk;
    private final String bucket;

    private final Path.Creator pathCreator;

    private final Path.MoreCreator morePathCreator;

    private final ObjectPool<PoolableConnection<ObsConnection>> pool;

    private Obs(ObsProperties obsProperties, Path.Creator pathCreator, Path.MoreCreator morePathCreator) {
        this.endpoint = obsProperties.getEndpoint();
        this.ak = obsProperties.getAk();
        this.sk = obsProperties.getSk();
        this.bucket = obsProperties.getBucket();
        this.pathCreator = pathCreator;
        this.morePathCreator = morePathCreator;
        PoolableConnectionFactory<ObsConnection> factory = createPooledObjectFactory(endpoint, ak, sk, bucket);
        ObjectPool<PoolableConnection<ObsConnection>> pool = createObjectPool(factory);
        factory.setPool(pool);
        this.pool = pool;
    }

    private PoolableConnectionFactory<ObsConnection> createPooledObjectFactory(String endpoint, String ak, String sk, String bucket) {
        return new PoolableConnectionFactory<>(() -> new ObsConnection(endpoint, ak, sk, bucket));
    }

    private ObjectPool<PoolableConnection<ObsConnection>> createObjectPool(PooledObjectFactory<PoolableConnection<ObsConnection>> factory) {
        return new GenericObjectPool<>(factory);
    }

    @Override
    public Connection getConnection() {
//        return pool.borrowObject();
        return null;
    }

    @Override
    public void close() {
//        try {
//            this.client.close();
//        } catch (IOException e) {
//            throw new ObsException("Failed to close obs client.", e);
//        }
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
                             Path.Creator pathCreator,
                             Path.MoreCreator morePathCreator) {
        if (!(fileSystemProperties instanceof ObsProperties)) {
            throw new IllegalArgumentException("Invalid configuration for protocol: " + protocol.getName());
        }

        return new Obs((ObsProperties) fileSystemProperties, pathCreator, morePathCreator);
    }

    private class ObsConnection implements Connection {
        private final ObsClient client;
        private final String bucket;
        private final AtomicReference<Path> working;

        public ObsConnection(String endpoint, String ak, String sk, String bucket) {
            this.client = new ObsClient(ak, sk, endpoint);
            this.bucket = bucket;
            this.working = new AtomicReference<>(pathCreator.create(FileSystem.C_ROOT_DIR));
        }

        @Override
        public Path pwd() {
            return this.working.get();
        }

        @Override
        public void cd(String pwd) {
            if (pwd.startsWith(FileSystem.C_ROOT_DIR)) {
                this.working.set(pathCreator.create(pwd));
            } else {
                Path p = this.working.get();
                while (!this.working.compareAndSet(p, p.resolve(pwd))) ;
            }
        }

        @Override
        public void store(String remote, File local) {

        }

        @Override
        public List<String> listFiles(Instant day, boolean isDirectory) {
            return null;
        }

        @Override
        public void download(String remote, File local) {

        }

        @Override
        public void close() throws Exception {

        }
    }

}
