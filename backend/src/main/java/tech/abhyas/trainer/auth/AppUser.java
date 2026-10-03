package tech.abhyas.trainer.auth;

import jakarta.persistence.*;
import java.time.Instant;

/** Maps {@code app_user}. Lets ddl-auto=validate check the schema; login arrives with ticket 02. */
@Entity
@Table(name = "app_user")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id", nullable = false, length = 50)
    private String loginId;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    protected AppUser() {}

    public Long getId() { return id; }
    public String getLoginId() { return loginId; }
}
