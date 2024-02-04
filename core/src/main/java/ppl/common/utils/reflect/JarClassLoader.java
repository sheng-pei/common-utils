package ppl.common.utils.reflect;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.security.SecureClassLoader;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class JarClassLoader extends SecureClassLoader {
    private final URL jar;

    public JarClassLoader(URL jar, ClassLoader parent) {
        super(parent);
        this.jar = jar;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String path = name.replace('.', '/').concat(".class");
        try {
            ZipEntry e = new ZipEntry(path);
            JarURLConnection connection = (JarURLConnection) jar.openConnection();
            JarFile jarFile = connection.getJarFile();
            try (InputStream is = jarFile.getInputStream(e)) {
                try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                    byte[] buf = new byte[4096];
                    int cnt = is.read(buf);
                    while (cnt >= 0) {
                        os.write(buf, 0, cnt);
                        cnt = is.read(buf);
                    }
                    byte[] res = os.toByteArray();
                    return defineClass(name, res, 0, res.length);
                }
            }
        } catch (Exception e) {
            throw new ClassNotFoundException(name, e);
        }
    }
}
