package tqs.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tqs.project.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE UPPER(u.email) = UPPER(:email)")
    List<User> findByEmailIgnoreCase(@Param("email") String email);
    
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE UPPER(u.email) = UPPER(:email)")
    boolean existsByEmailIgnoreCase(@Param("email") String email);
}