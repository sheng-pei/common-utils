package ppl.common.utils.attire.proxy.server.param;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class FormDataPojo {
    private final Charset charset;

    public FormDataPojo(FormData formData) {
        String charset = formData.value();
        if (charset.isEmpty()) {
            this.charset = StandardCharsets.UTF_8;
        } else {
            this.charset = Charset.forName(charset);
        }
    }

    public Charset charset() {
        return charset;
    }
}
