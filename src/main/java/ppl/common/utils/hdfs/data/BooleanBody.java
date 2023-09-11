package ppl.common.utils.hdfs.data;

import com.fasterxml.jackson.annotation.JsonAlias;

public class BooleanBody {
    @JsonAlias("boolean")
    private boolean bool;

    public boolean isBool() {
        return bool;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }
}
