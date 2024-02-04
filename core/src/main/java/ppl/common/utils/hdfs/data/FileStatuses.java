package ppl.common.utils.hdfs.data;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

public class FileStatuses {
    @JsonAlias("FileStatus")
    private List<FileStatus> fileStatus;

    public List<FileStatus> getFileStatus() {
        return fileStatus;
    }

    public void setFileStatus(List<FileStatus> fileStatus) {
        this.fileStatus = fileStatus;
    }
}
