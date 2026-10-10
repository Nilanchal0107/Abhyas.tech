package tech.abhyas.trainer.common;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/** Every error leaves as RFC 7807 Problem Details (api-contracts 1.1). Never echoes or logs a request body. */
@RestControllerAdvice
public class ProblemHandler {
    private static final Logger log = LoggerFactory.getLogger(ProblemHandler.class);

    @ExceptionHandler(ApiException.class)
    ResponseEntity<ProblemDetail> api(ApiException e) {
        ResponseEntity<ProblemDetail> r = problem(e.status(), e.type(), e.getMessage());
        e.extras().forEach(r.getBody()::setProperty);
        return r;
    }

    @ExceptionHandler({NoResourceFoundException.class, NoHandlerFoundException.class})
    ResponseEntity<ProblemDetail> noRoute() {
        return problem(HttpStatus.NOT_FOUND, "not-found", "We could not find that.");
    }

    /** Spring's own client errors (wrong method, bad JSON, missing parameter) keep their 4xx status. */
    private static ResponseEntity<ProblemDetail> spring(ErrorResponse e) {
        HttpStatus status = HttpStatus.valueOf(e.getStatusCode().value());
        // ponytail: only 400 and 404 have a contract code; other statuses use server-error until a ticket needs more
        String type = status == HttpStatus.BAD_REQUEST ? "validation" : status == HttpStatus.NOT_FOUND ? "not-found" : "server-error";
        return problem(status, type, status == HttpStatus.BAD_REQUEST ? "Some fields need attention." : status.getReasonPhrase());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ProblemDetail> other(Exception e, HttpServletRequest req) {
        // not a separate @ExceptionHandler(ErrorResponse): Spring ranks handlers by class depth, so this one would win
        if (e instanceof ErrorResponse er) return spring(er);
        // class name and SQL error number only: an exception message can carry request content
        Throwable c = e;
        while (c != null && !(c instanceof java.sql.SQLException)) c = c.getCause();
        String sql = c instanceof java.sql.SQLException se ? " sqlState=" + se.getSQLState() + " errno=" + se.getErrorCode() : "";
        log.error("Unhandled {} on {} {}{}", e.getClass().getName(), req.getMethod(), req.getRequestURI(), sql);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "server-error", "Something went wrong. Please try again.");
    }

    private static ResponseEntity<ProblemDetail> problem(HttpStatus status, String type, String detail) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(status, detail);
        p.setType(URI.create(type));
        p.setTitle(status.getReasonPhrase());
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_PROBLEM_JSON).body(p);
    }
}
