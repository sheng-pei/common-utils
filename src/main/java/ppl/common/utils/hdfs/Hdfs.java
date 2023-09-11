package ppl.common.utils.hdfs;

import ppl.common.utils.filesystem.Path;
import ppl.common.utils.hdfs.agent.Agent;
import ppl.common.utils.hdfs.data.FileStatuses;
import ppl.common.utils.hdfs.selector.Selector;
import ppl.common.utils.http.Client;

import java.io.InputStream;

public class Hdfs {
    private final Agent agent;

    public Hdfs(Client client, Cluster cluster) {
        Selector selector = cluster.getSelector();
        this.agent = new Agent(client, selector, cluster.getUser(), selector.maxAttempts(-1));
    }

    public void copyToRemote(Path local, Path remote) {
        agent.copyToRemote(local, remote);
    }

    public void copyToRemote(InputStream is, Path remote) {
        agent.copyToRemote(is, remote);
    }

    public void copyToLocal(Path remote, Path local) {
        agent.copyToLocal(remote, local);
    }

    public FileStatuses listDir(Path remote) {
        return agent.listDir(remote);
    }

    public boolean mkdirs(Path remote) {
        return agent.mkdirs(remote);
    }

    public boolean delete(Path remote) {
        return agent.delete(remote);
    }
}
