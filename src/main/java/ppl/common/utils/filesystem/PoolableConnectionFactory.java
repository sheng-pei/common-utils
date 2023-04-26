package ppl.common.utils.filesystem;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import ppl.common.utils.SetOnce;

import java.util.function.Supplier;

public class PoolableConnectionFactory<C extends Connection> implements PooledObjectFactory<PoolableConnection<C>> {

    private final SetOnce<ObjectPool<PoolableConnection<C>>> pool;
    private final Supplier<C> supplier;

    public PoolableConnectionFactory(Supplier<C> supplier) {
        this.supplier = supplier;
        this.pool = new SetOnce<>();
    }

    public synchronized void setPool(ObjectPool<PoolableConnection<C>> pool) {
        this.pool.set(pool);
    }

    @Override
    public void activateObject(PooledObject<PoolableConnection<C>> pooledObject) throws Exception {

    }

    @Override
    public void destroyObject(PooledObject<PoolableConnection<C>> pooledObject) throws Exception {

    }

    @Override
    public PooledObject<PoolableConnection<C>> makeObject() throws Exception {
        return new DefaultPooledObject<>(new PoolableConnection<>(pool.get(), supplier.get()));
    }

    @Override
    public void passivateObject(PooledObject<PoolableConnection<C>> pooledObject) throws Exception {

    }

    @Override
    public boolean validateObject(PooledObject<PoolableConnection<C>> pooledObject) {
        return false;
    }
}
