package tech.abhyas.trainer.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.abhyas.trainer.common.ApiException;

@RestController
@RequestMapping("/api")
public class MeController {
    /** Nobody can sign in yet (ticket 02), so this always answers "not signed in". */
    @GetMapping("/me")
    void me() {
        throw new ApiException(HttpStatus.UNAUTHORIZED, "unauthorized", "Please log in.");
    }
}
