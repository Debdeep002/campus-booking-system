package jar.repositories;

import jar.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    // Custom method to find a user for logging in
    Optional<User> findByCollegeEmail(String collegeEmail);
}