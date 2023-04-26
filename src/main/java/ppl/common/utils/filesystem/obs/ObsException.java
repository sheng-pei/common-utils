package ppl.common.utils.filesystem.obs;

import ppl.common.utils.filesystem.FileSystemException;

public class ObsException extends FileSystemException {
    public ObsException(String message) {
        super(message);
    }

    public ObsException(String message, Throwable cause) {
        super(message, cause);
    }
}
