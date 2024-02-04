package ppl.common.utils.hdfs.data;

import com.fasterxml.jackson.annotation.JsonAlias;

public class FileStatusesBody {
    @JsonAlias("FileStatuses")
    private FileStatuses fileStatuses;

    public FileStatuses getFileStatuses() {
        return fileStatuses;
    }

    public void setFileStatuses(FileStatuses fileStatuses) {
        this.fileStatuses = fileStatuses;
    }
}
