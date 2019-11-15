package pl.coderstrust.database.hibernate;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    void deleteUserByEmail(String email);

    Optional<User> findUserByEmail(String email);
}
