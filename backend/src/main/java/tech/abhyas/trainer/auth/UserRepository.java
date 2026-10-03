package tech.abhyas.trainer.auth;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    // login_id uses a case-insensitive collation (V1), so both lookups ignore letter case
    Optional<AppUser> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);
}
