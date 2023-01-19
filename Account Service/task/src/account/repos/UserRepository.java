package account.repos;

import account.user.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findUserByEmail(String email);
    void delete(User user);

    boolean existsUserByEmail(String email);

}