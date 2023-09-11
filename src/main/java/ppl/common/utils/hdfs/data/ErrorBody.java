package ppl.common.utils.hdfs.data;

import com.fasterxml.jackson.annotation.JsonAlias;

public class ErrorBody {
    @JsonAlias("RemoteException")
    private RemoteError remoteError;

    public RemoteError getRemoteError() {
        return remoteError;
    }

    public void setRemoteError(RemoteError remoteError) {
        this.remoteError = remoteError;
    }
}
