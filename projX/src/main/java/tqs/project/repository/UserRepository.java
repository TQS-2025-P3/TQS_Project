package tqs.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tqs.project.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
