package tech.abhyas.trainer.auth;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Signs and checks the HS256 session token held in the {@code rdt_session} cookie. */
@Component
public class TokenService {
    public static final Duration LIFETIME = Duration.ofHours(8);

    private final byte[] secret;
    private final Clock clock;

    public TokenService(@Value("${app.jwt.secret:}") String secret, Clock clock) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        if (this.secret.length < 32) {
            throw new IllegalStateException("JWT_SECRET must be at least 32 bytes (see .env.example)");
        }
        this.clock = clock;
    }

    public String issue(long userId) {
        try {
            Date now = Date.from(clock.instant());
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(Long.toString(userId))
                    .issueTime(now)
                    .expirationTime(Date.from(clock.instant().plus(LIFETIME)))
                    .build();
            SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            jwt.sign(new MACSigner(secret));
            return jwt.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("Could not sign the session token", e);
        }
    }

    /** The user ID in a valid, unexpired token, or empty for anything else. */
    public Optional<Long> verify(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            if (!JWSAlgorithm.HS256.equals(jwt.getHeader().getAlgorithm()) || !jwt.verify(new MACVerifier(secret))) {
                return Optional.empty();
            }
            Date exp = jwt.getJWTClaimsSet().getExpirationTime();
            if (exp == null || !exp.toInstant().isAfter(clock.instant())) return Optional.empty();
            return Optional.of(Long.parseLong(jwt.getJWTClaimsSet().getSubject()));
        } catch (Exception e) { // bad format, bad signature, bad subject: all just "not signed in"
            return Optional.empty();
        }
    }
}
