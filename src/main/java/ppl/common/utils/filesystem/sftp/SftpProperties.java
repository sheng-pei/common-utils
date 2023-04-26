//package ppl.common.utils.filesystem.sftp;
//
//import ppl.common.utils.filesystem.FileSystemProperties;
//
//public class SftpProperties implements FileSystemProperties {
//    private String server = "127.0.0.1";
//    private int port = 22;
//    private String username;
//    private String password;
//
//    public String getServer() {
//        return server;
//    }
//
//    public void setServer(String server) {
//        if (server != null) {
//            this.server = server;
//        }
//    }
//
//    public Integer getPort() {
//        return port;
//    }
//
//    public void setPort(Integer port) {
//        if (port != null) {
//            this.port = port;
//        }
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    @Override
//    public String toString() {
//        return "SftpProperties{" +
//                "server='" + server + '\'' +
//                ", port=" + port +
//                ", username='" + username + '\'' +
//                ", password='" + password + '\'' +
//                '}';
//    }
//
//}
