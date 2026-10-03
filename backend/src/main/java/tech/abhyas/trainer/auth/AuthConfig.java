package tech.abhyas.trainer.auth;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AuthConfig implements WebMvcConfigurer {
    private final TokenService tokens;

    AuthConfig(TokenService tokens) { this.tokens = tokens; }

    // static: TokenService needs the Clock, and this class needs TokenService, so these beans must not need this instance
    @Bean
    static Clock clock() { return Clock.systemUTC(); }

    @Bean
    static PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor(tokens)).addPathPatterns("/api/**").excludePathPatterns(AuthInterceptor.PUBLIC);
    }
}
