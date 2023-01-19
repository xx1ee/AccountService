package account.services;

import account.repos.UserRepository;
import account.user.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (!userRepository.existsUserByEmail(email.toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not found");
        }
        return new UserDetailsImpl(userRepository.findUserByEmail(email.toLowerCase()));
    }
}
