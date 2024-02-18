package ppl.common.utils.hdfs.selector;

import ppl.common.utils.filesystem.path.Path;
import ppl.common.utils.hdfs.retrier.RetryStage;
import ppl.common.utils.http.url.URL;

public interface Selector extends RetryStage<Path, URL> {
    int maxAttempts(int maxAttempts);
    URL init(Path path);
    URL next(Path path);
    void finish();
}
