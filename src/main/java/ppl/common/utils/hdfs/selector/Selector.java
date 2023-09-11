package ppl.common.utils.hdfs.selector;

import ppl.common.utils.filesystem.Path;
import ppl.common.utils.hdfs.agent.RetryStage;
import ppl.common.utils.http.url.URL;

public interface Selector extends RetryStage<Path, URL> {
    int maxAttempts(int maxAttempts);
    URL init(Path path);
    URL next(Path path);
    void finish();
}
