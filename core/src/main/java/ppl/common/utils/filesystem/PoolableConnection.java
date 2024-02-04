package ppl.common.utils.filesystem;

import org.apache.commons.pool2.ObjectPool;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.List;

public class PoolableConnection<C extends Connection> implements Connection {

    private final ObjectPool<PoolableConnection<C>> pool;
    private final C connection;
    private volatile boolean closed;

    public PoolableConnection(ObjectPool<PoolableConnection<C>> pool, C connection) {
        this.pool = pool;
        this.connection = connection;
    }

    @Override
    public Path pwd() throws IOException {
        checkOpen();
        return this.connection.pwd();
    }

    @Override
    public void cd(String working) throws IOException {
        checkOpen();
        this.connection.cd(working);
    }

    @Override
    public void store(String remote, File local) throws IOException {
        checkOpen();
        this.connection.store(remote, local);
    }

    @Override
    public List<String> listFiles(Instant day, boolean isDirectory) throws IOException {
        checkOpen();
        return this.connection.listFiles(day, isDirectory);
    }

    @Override
    public void download(String remote, File local) throws IOException {
        checkOpen();
        this.connection.download(remote, local);
    }

    private void checkOpen() throws IOException {
        if (closed) {
            throw new IOException("Connection is already closed.");
        }
    }

    @Override
    public synchronized void close() throws Exception {//synchronized. Make sure this instance is not passed to
                                                       //returnObject method more than one time at once.
        if (closed) {
            return;
        }

        try {
            this.pool.returnObject(this);
        } catch (IllegalStateException e) {//This connection has invalid pool state.
            reallyClose();
        }
    }

    public void activate() throws Exception {
        if (this.connection instanceof Session) {
            Session session = (Session) this.connection;
            session.resetSession();
        }
        this.closed = false;
    }

    public void passivate() throws Exception {
        this.closed = true;
    }

    public void validate() throws Exception {
        if (this.connection instanceof Validator) {
            Validator validator = (Validator) this.connection;
            validator.validate();
        }
    }

    public void reallyClose() throws Exception {
        try {
            passivate();
        } finally {
            this.connection.close();
        }
    }
}
