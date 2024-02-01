package ppl.common.utils.os;

public final class OsInfo {
    private static final String OS_NAME = System.getProperty("os.name").toLowerCase();
    public static final OsInfo instance = new OsInfo();

    private Platform platform;

    private OsInfo() {}

    public Platform getOS() {
        if (platform != null) {
            return platform;
        }

        if (isLinux()) {
            platform = Platform.LINUX;
        } else if (isWindows()) {
            platform = Platform.WINDOWS;
        } else if (isMacOS()) {
            platform = Platform.MAC_OS;
        } else if (isMacOSX()) {
            platform = Platform.MAC_OS_X;
        } else {
            platform = Platform.OTHERS;
        }
        return platform;
    }

    private static boolean isLinux() {
        return OS_NAME.startsWith("linux");
    }

    private static boolean isWindows() {
        return OS_NAME.startsWith("windows");
    }

    private static boolean isMacOS() {
        String[] fragments = OS_NAME.split("\\s+");
        for (int i = 0; i < fragments.length; i++) {
            if (fragments[i].equals("mac")) {
                if (i+1 < fragments.length && fragments[i+1].equals("os") &&
                        (i+2 >= fragments.length || !fragments[i+2].equals("x"))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isMacOSX() {
        String[] fragments = OS_NAME.split("\\s+");
        for (int i = 0; i < fragments.length; i++) {
            if (fragments[i].equals("mac")) {
                if (i+2 < fragments.length &&
                        fragments[i+1].equals("os") &&
                        fragments[i+2].equals("x")) {
                    return true;
                }
            }
        }
        return false;
    }
}
