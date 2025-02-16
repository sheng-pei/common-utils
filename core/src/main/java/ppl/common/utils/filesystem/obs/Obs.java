package ppl.common.utils.filesystem.obs;

import com.obs.services.ObsClient;
import com.obs.services.model.ObsObject;
import com.obs.services.model.PutObjectRequest;
import com.obs.services.model.fs.NewFolderRequest;
import ppl.common.utils.IOs;
import ppl.common.utils.filesystem.core.*;
import ppl.common.utils.filesystem.path.Path;
import ppl.common.utils.filesystem.path.Paths;
import ppl.common.utils.filesystem.sftp.SftpException;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class Obs implements FileSystem {

    private static final Protocol protocol = Protocol.OBS;

    public static Obs create(FileSystemProperties fileSystemProperties) {
        if (!(fileSystemProperties instanceof ObsProperties)) {
            throw new IllegalArgumentException("Invalid configuration for protocol: " + protocol.getName());
        }

        ObsProperties obsProperties = (ObsProperties) fileSystemProperties;
        Obs obs = new Obs(obsProperties);
        if (obsProperties.isAutoCreateWorking()) {
            try (SimpleObsClientWrapper client = obs.new SimpleObsClientWrapper()) {
                client.mkdirs(obs.working);
            }
        }
        return obs;
    }

    private final String endpoint;
    private final String bucket;
    private final Path working;
    private final ObsClient obsClient;
    private final AtomicBoolean shutdown = new AtomicBoolean();

    private Obs(ObsProperties obsProperties) {
        this.endpoint = obsProperties.getEndpoint();
        this.bucket = obsProperties.getBucket();
        this.working = Paths.get(Path.C_ROOT_DIR)
                .resolve(Paths.get(obsProperties.getWorking()))
                .normalize();
        this.obsClient = new ObsClient(obsProperties.getAk(), obsProperties.getSk(), obsProperties.getEndpoint());
    }

    @Override
    public Connection getConnection() {
        checkShutdown();
        return new ObsConnection();
    }

    @Override
    public void close() {
        if (this.shutdown.compareAndSet(false, true)) {
            try {
                this.obsClient.close();
            } catch (IOException e) {
                throw new ObsException("Failed to close obs.", e);
            }
        }
    }

    private void checkShutdown() {
        if (shutdown.get()) {
            throw new SftpException(String.format("Obs '%s/%s' is already closed.", endpoint, bucket));
        }
    }

    private class ObsConnection implements Connection {

        private volatile Path working;
        private final SimpleObsClientWrapper obs;
        private final Path ancestor;
        private final AtomicBoolean shutdown = new AtomicBoolean();

        public ObsConnection() {
            this.obs = new SimpleObsClientWrapper();
            this.ancestor = Obs.this.working;
        }

        @Override
        public String actualPath(String path) {
            Obs.this.checkShutdown();
            checkShutdown();
            return this.working.resolve(path)
                    .normalize().toString();
        }

        @Override
        public String actualPath(Path path) {
            Obs.this.checkShutdown();
            checkShutdown();
            return this.working.resolve(path)
                    .normalize().toString();
        }

        @Override
        public Path pwd() {
            Obs.this.checkShutdown();
            checkShutdown();
            return this.working;
        }

        @Override
        public void cd(String path) {
            Obs.this.checkShutdown();
            checkShutdown();
            synchronized (this) {
                Path working = this.working;
                checkPath(working, Paths.get(path));
                this.working = working.resolve(path).normalize();
            }
        }

        @Override
        public void cd(Path path) {
            Obs.this.checkShutdown();
            checkShutdown();
            synchronized (this) {
                Path working = this.working;
                checkPath(working, path);
                this.working = working.resolve(path).normalize();
            }
        }

        @Override
        public void store(String remote, File local) {
            Obs.this.checkShutdown();
            checkShutdown();
            Path path = Paths.get(remote);
            Path t = checkPath(this.working, path);
            obs.store(t, local);
        }

        @Override
        public void store(String remote, InputStream is) {
            Obs.this.checkShutdown();
            checkShutdown();
            Path path = Paths.get(remote);
            Path t = checkPath(this.working, path);
            obs.store(t, is);
        }

        @Override
        public void download(String remote, File local) {
            Obs.this.checkShutdown();
            checkShutdown();
            Path path = Paths.get(remote);
            Path t = checkPath(this.working, path);
            obs.download(t, local);
        }

        @Override
        public void download(String remote, OutputStream os) {
            Obs.this.checkShutdown();
            checkShutdown();
            Path path = Paths.get(remote);
            Path t = checkPath(this.working, path);
            obs.download(t, os);
        }

        @Override
        public void mkdir(Path path) {
            Obs.this.checkShutdown();
            checkShutdown();
            Path working = this.working;
            Path t = checkPath(working, path);
            if (!working.equals(t.getParent())) {
                throw new ObsException("Mkdir error. File not found.");
            }
            obs.mkdirs(path);
        }

        @Override
        public void mkdirs(Path path) {
            Obs.this.checkShutdown();
            checkShutdown();
            Path working = this.working;
            checkPath(working, path);
            obs.mkdirs(path);
        }

        @Override
        public List<CFile> listFiles() {
            //TODO, 目前没有资源，待有资源后尝试实现
            return null;
        }

        @Override
        public List<CFile> listFiles(LocalDateTime day, boolean isDirectory) {
            //TODO, 目前没有资源，待有资源后尝试实现
            throw new UnsupportedOperationException("Cannot list files modified on some day.");
        }

        @Override
        public List<CFile> listFiles(Predicate<CFile> predicate) {
            //TODO, 目前没有资源，待有资源后尝试实现
            return null;
        }

        @Override
        public void close() {
            this.shutdown.compareAndSet(false, true);
        }

        private void checkShutdown() {
            if (shutdown.get()) {
                throw new ObsException(String.format(
                        "Obs connection '%s/%s' is already closed.", endpoint, bucket));
            }
        }

        private Path checkPath(Path pwd, Path path) {
            Path p = pwd.resolve(path).normalize();
            if (!p.startsWith(ancestor)) {
                throw new ObsException(String.format("The ancestor of path '%s' is not '%s'.", p, ancestor));
            }
            return p;
        }
    }

    private class SimpleObsClientWrapper implements AutoCloseable {

        public void mkdirs(Path path) {
            //TODO, 重复Key是否会有问题。
            NewFolderRequest request = new NewFolderRequest();
            request.setBucketName(bucket);
            request.setObjectKey(Paths.get(Path.C_ROOT_DIR).relativize(path).toString());
            try {
                obsClient.newFolder(request);
            } catch (com.obs.services.exception.ObsException e) {
                throw new ObsException("Mkdirs error.", e);
            }
        }

        public void store(Path path, File local) {
            PutObjectRequest request = new PutObjectRequest();
            request.setBucketName(bucket);
            request.setObjectKey(Paths.get(Path.C_ROOT_DIR).relativize(path).toString());
            request.setFile(local);
            try {
                obsClient.putObject(request);
            } catch (com.obs.services.exception.ObsException e) {
                throw new ObsException("Failed to store file: " + path, e);
            }
        }

        public void store(Path path, InputStream is) {
            PutObjectRequest request = new PutObjectRequest();
            request.setBucketName(bucket);
            request.setObjectKey(Paths.get(Path.C_ROOT_DIR).relativize(path).toString());
            request.setInput(is);
            try {
                obsClient.putObject(request);
            } catch (com.obs.services.exception.ObsException e) {
                throw new ObsException("Failed to store file: " + path, e);
            }
        }

        public void download(Path path, File local) {
            ObsObject obsObject = openForRead(path);
            readIn(obsObject, local);
        }

        public void download(Path path, OutputStream os) {
            ObsObject obsObject = openForRead(path);
            readIn(obsObject, os);
        }

        private ObsObject openForRead(Path p) {
            try {
                return obsClient.getObject(bucket, Paths.get(Path.C_ROOT_DIR).relativize(p).toString());
            } catch (com.obs.services.exception.ObsException e) {
                throw new ObsException("Failed to open " + p, e);
            }
        }

        private void readIn(ObsObject obsObject, File localFile) {
            try (InputStream content = obsObject.getObjectContent();
                 BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(localFile.toPath()))) {
                if (content != null) {
                    IOs.copy(content, bos);
                }
            } catch (IOException e) {
                throw new ObsException("Failed to download file from obs", e);
            }
        }

        private void readIn(ObsObject obsObject, OutputStream os) {
            try (InputStream content = obsObject.getObjectContent()) {
                if (content != null) {
                    IOs.copy(content, os);
                }
            } catch (IOException e) {
                throw new ObsException("Failed to download file from obs", e);
            }
        }

        @Override
        public void close() {
        }

    }

}
