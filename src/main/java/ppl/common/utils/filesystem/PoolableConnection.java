package ppl.common.utils.filesystem;

import org.apache.commons.pool2.ObjectPool;

import java.io.File;
import java.time.Instant;
import java.util.List;

public class PoolableConnection<C extends Connection> implements Connection {

    private final ObjectPool<PoolableConnection<C>> pool;
    private final C connection;

    public PoolableConnection(ObjectPool<PoolableConnection<C>> pool, C connection) {
        this.pool = pool;
        this.connection = connection;
    }

    @Override
    public Path pwd() {
        return this.connection.pwd();
    }

    @Override
    public void cd(String working) {
        this.connection.cd(working);
    }

    @Override
    public void store(String remote, File local) {
        this.connection.store(remote, local);
    }

    @Override
    public List<String> listFiles(Instant day, boolean isDirectory) {
        return this.connection.listFiles(day, isDirectory);
    }

    @Override
    public void download(String remote, File local) {
        this.connection.download(remote, local);
    }

    @Override
    public void close() throws Exception {
        this.pool.returnObject(this);
    }
}
