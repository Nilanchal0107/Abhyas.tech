package tech.abhyas.trainer.common;

import org.springframework.http.HttpStatus;

/** An error with a stable api-contracts 1.1 {@code type} code. */
public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final String type;

    public ApiException(HttpStatus status, String type, String detail) {
        super(detail);
        this.status = status;
        this.type = type;
    }

    public HttpStatus status() { return status; }
    public String type() { return type; }
}
