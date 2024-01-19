package account.repos;

import account.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmailIgnoreCase(String username);
    List<User> findAllByOrderByIdAsc();
    void deleteUserByEmailIgnoreCase(String email);
}
