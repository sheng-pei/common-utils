package ppl.common.utils.filesystem.sftp;

import ppl.common.utils.filesystem.core.FileSystemException;

public class SftpException extends FileSystemException {
    public SftpException(String message) {
        super(message);
    }

    public SftpException(String message, Throwable cause) {
        super(message, cause);
    }
}
