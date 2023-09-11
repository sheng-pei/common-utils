package ppl.common.utils.hdfs.ns;

import sun.net.spi.nameservice.NameService;
import sun.net.spi.nameservice.NameServiceDescriptor;

public class HdfsNameServiceDescriptor implements NameServiceDescriptor {
    @Override
    public NameService createNameService() throws Exception {
        return new HdfsNameService();
    }

    @Override
    public String getProviderName() {
        return "b";
    }

    @Override
    public String getType() {
        return "a";
    }
}
