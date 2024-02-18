package ppl.common.utils.filesystem.sftp;

import ppl.common.utils.filesystem.core.FileSystemProperties;

import java.nio.charset.Charset;

public class SftpProperties implements FileSystemProperties {
    private String server = "127.0.0.1";
    private int port = 22;
    private String username;
    private String password;
    private String working = ".";
    private boolean autoCreateWorking = true;
    private Charset charset;
    private boolean channelFirst = true;
    private int coreSession = 1;
    private int maxChannel = 5;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        if (server != null) {
            this.server = server;
        }
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        if (port != null) {
            this.port = port;
        }
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

    public String getWorking() {
        return working;
    }

    public void setWorking(String working) {
        this.working = working;
    }

    public boolean isAutoCreateWorking() {
        return autoCreateWorking;
    }

    public void setAutoCreateWorking(boolean autoCreateWorking) {
        this.autoCreateWorking = autoCreateWorking;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public int getCoreSession() {
        return coreSession;
    }

    public void setCoreSession(int coreSession) {
        if (coreSession <= 0) {
            throw new IllegalArgumentException("The amount of core sessions must be positive.");
        }
        this.coreSession = coreSession;
    }

    public boolean isChannelFirst() {
        return channelFirst;
    }

    public void setChannelFirst(boolean channelFirst) {
        this.channelFirst = channelFirst;
    }

    public int getMaxChannel() {
        return maxChannel;
    }

    public void setMaxChannel(int maxChannel) {
        if (maxChannel < 1) {
            throw new IllegalArgumentException("The max amount of channels per session must be greater than one.");
        }
        this.maxChannel = maxChannel;
    }
}
