package ppl.common.utils.filesystem;

import ppl.common.utils.filesystem.obs.ObsProperties;

import java.time.Instant;

public class Main {
    public static void main(String[] args) throws Exception {
        FileSystem system = Protocol.OBS.open(new ObsProperties.Builder().withAk("aa").withBucket("aa").withEndpoint("aa").withSk("aa").build());
        Connection connection = system.getConnection();
        System.out.println(connection.pwd());
        connection.cd("a/b");
        System.out.println(connection.pwd());
        connection.close();
        connection.listFiles(Instant.now(), false);
        connection = system.getConnection();
        System.out.println(connection.pwd());
    }
}
