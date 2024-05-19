//package ppl.common.utils.hdfs.ns;
//
//import sun.net.spi.nameservice.NameService;
//
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//
//public class HdfsNameService implements NameService {
//    @Override
//    public InetAddress[] lookupAllHostAddr(String s) throws UnknownHostException {
//        if ("hello.world".equals(s)) {
//            return new InetAddress[] {InetAddress.getByAddress(s, new byte[] {1, 1, 1, 1})};
//        }
//        throw new UnknownHostException();
//    }
//
//    @Override
//    public String getHostByAddr(byte[] bytes) throws UnknownHostException {
//        return null;
//    }
//}
