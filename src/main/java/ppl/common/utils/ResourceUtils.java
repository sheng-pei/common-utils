package ppl.common.utils;

import ppl.common.utils.exception.ResourceException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ResourceUtils {

    private ResourceUtils() { }

    private static final Pattern JAR_URL_PATTERN = Pattern.compile("^[jJ][aA][rR]:.*!/.*$");
    private static final String FILE = "file";

    /*
     * file:///absolute/path/file or file:/absolute/path/file
     */
    public static boolean isLocalFile(URL url) {
        String protocol = url.getProtocol().toLowerCase();
        String authority = url.getAuthority();      //[(user info)root:pass]@[(host)localhost]
        return protocol.equals(FILE) &&
                (authority == null || "".equals(authority)) &&
                url.getPort() < 0 &&
                url.getQuery() == null &&
                url.getRef() == null;
    }

    /*
     * The given resource must be (jar:) protocol
     *
     * jar:<url>!/{entry}
     */
    public static URL extractUrlFromJarUrl(String resource) {

        Matcher matcher = JAR_URL_PATTERN.matcher(resource);
        if (!matcher.matches()) {
            throw new ResourceException("Invalid jar url: " + resource);
        }

        int jarProtocolLen = 4;     //jar:
        int index = resource.indexOf("!/");
        String result = resource.substring(jarProtocolLen, index);

        return newURL(result);
    }

    private static URL newURL(String url) {
        URL jarUrl;
        try {
            jarUrl = new URL(url);
        } catch (MalformedURLException e) {
            throw new ResourceException("Invalid url: " + url, e);
        }
        return jarUrl;
    }

}
