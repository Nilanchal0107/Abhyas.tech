package tech.abhyas.trainer.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import tech.abhyas.trainer.common.ApiException;

/** Every /api request needs a valid session cookie. The user ID goes into the {@code userId} request attribute. */
public class AuthInterceptor implements HandlerInterceptor {
    public static final String USER_ID = "userId";
    // logout is public so it also works with an expired cookie ("repeating it is fine")
    public static final String[] PUBLIC = {"/api/auth/signup", "/api/auth/login", "/api/auth/logout"};

    private final TokenService tokens;

    public AuthInterceptor(TokenService tokens) { this.tokens = tokens; }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (!AuthController.COOKIE.equals(c.getName())) continue;
                Optional<Long> id = tokens.verify(c.getValue());
                if (id.isPresent()) {
                    req.setAttribute(USER_ID, id.get());
                    return true;
                }
            }
        }
        throw new ApiException(HttpStatus.UNAUTHORIZED, "unauthorized", "Please log in.");
    }
}
