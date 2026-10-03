package tech.abhyas.trainer.common;

import java.util.Map;
import org.springframework.http.HttpStatus;

/** An error with a stable api-contracts 1.1 {@code type} code and optional extra body fields. */
public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final String type;
    private final Map<String, Object> extras;

    public ApiException(HttpStatus status, String type, String detail) {
        this(status, type, detail, Map.of());
    }

    public ApiException(HttpStatus status, String type, String detail, Map<String, Object> extras) {
        super(detail);
        this.status = status;
        this.type = type;
        this.extras = extras;
    }

    public HttpStatus status() { return status; }
    public String type() { return type; }
    public Map<String, Object> extras() { return extras; }
}
