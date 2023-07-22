package ppl.common.utils.filesystem.ftp;

import ppl.common.utils.filesystem.FileSystemProperties;

public class FtpProperties implements FileSystemProperties {
    private String server = "127.0.0.1";
    private int port = 21;
    private String username;
    private String password;
    private Boolean active = false;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        if (server != null) {
            this.server = server;
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        if (active != null) {
            this.active = active;
        }
    }

    @Override
    public String toString() {
        return "FtpProperties{" +
                "server='" + server + '\'' +
                ", port=" + port +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", active=" + active +
                '}';
    }
}
