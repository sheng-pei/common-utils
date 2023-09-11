package ppl.common.utils.http.response;

import java.util.Arrays;

public enum ResponseCode {
    CONTINUE(100, ""),
    OK(200, ""),
    CREATED(201, ""),
    NON_AUTHORITATIVE_INFORMATION(203, ""),
    NO_CONTENT(204, ""),
    TEMPORARY_REDIRECT(307, ""),
    BAD_REQUEST(400, ""),
    UNAUTHORIZED(401, ""),
    FORBIDDEN(403, ""),
    NOT_FOUND(404, ""),
    METHOD_NOT_ALLOWED(405, ""),
    REQUEST_TIMEOUT(408, ""),
    GONE(410, ""),
    UNSUPPORTED_MEDIA_TYPE(415, ""),
    INTERNAL_SERVER_ERROR(500, ""),
    BAD_GATEWAY(502, ""),
    SERVICE_UNAVAILABLE(503, ""),
    GATEWAY_TIMEOUT(504, "");

    private final int code;
    private final String description;

    ResponseCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public boolean isError() {
        return code >= 400;
    }

    public String getDescription() {
        return description;
    }

    public static ResponseCode enumOf(Integer code) {
        if (code == null) {
            return null;
        }

        return Arrays.stream(ResponseCode.values())
                .filter(c -> code.equals(c.getCode()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "No enum constant: '%d' of enum class: '%s'",
                        code, ResponseCode.class.getCanonicalName())));
    }
}
