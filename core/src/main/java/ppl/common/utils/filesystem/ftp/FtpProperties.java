package ppl.common.utils.filesystem.ftp;

import ppl.common.utils.filesystem.core.FileSystemProperties;

import java.nio.charset.Charset;

public class FtpProperties implements FileSystemProperties {
    private String server = "127.0.0.1";
    private int port = 21;
    private String username;
    private String password;
    private boolean active = false;
    private String working = ".";
    private boolean autoCreateWorking = true;
    private Charset charset;

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

    public String getWorking() {
        return working;
    }

    public void setWorking(String working) {
        if (working != null) {
            this.working = working;
        }
    }

    public Boolean isAutoCreateWorking() {
        return autoCreateWorking;
    }

    public void setAutoCreateWorking(Boolean autoCreateWorking) {
        if (autoCreateWorking != null) {
            this.autoCreateWorking = autoCreateWorking;
        }
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }
}
