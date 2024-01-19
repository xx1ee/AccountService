package account.services;

import account.models.*;
import account.repos.RoleRepository;
import account.repos.UserRepository;
import account.util.NewException;
import account.util.UserExistException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final SecurityEventService securityEventService;

    public static final int MAX_FAILED_ATTEMPTS = 5;

    private static final long LOCK_TIME_DURATION = 24 * 60 * 60 * 1000; // 24 hours
    @Autowired
    private HttpServletRequest request;
    private final List<String> breachedPasswords = Arrays.asList("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, SecurityEventService securityEventService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.securityEventService = securityEventService;
    }
    public UserResponse register(User user) {
        if (breachedPasswords.contains(user.getPassword())) {
            throw new UserExistException(HttpStatus.BAD_REQUEST,"The password is in the hacker's database!", "Bad Request", "/api/auth/changepass" );
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (userRepository.findByEmailIgnoreCase(user.getEmail()) == null) {
            Set<Role> roleSet = new HashSet<>();
            if (userRepository.findAll().isEmpty()) {
                roleSet.add(roleRepository.findFirstByAuthority("ROLE_ADMINISTRATOR"));
            } else {
                roleSet.add(roleRepository.findFirstByAuthority("ROLE_USER"));
            }
            user.setAuthorities(roleSet);
            userRepository.save(user);
            var userFind = userRepository.findByEmailIgnoreCase(user.getEmail());
            Set<String> rolesSorted = new TreeSet<>();
            for (GrantedAuthority r : userFind.getAuthorities()) {
                rolesSorted.add(r.getAuthority());
            }
            return new UserResponse(userFind.getId(), userFind.getName(), userFind.getLastname(), userFind.getEmail(), rolesSorted);
        } else {
            throw new UserExistException(HttpStatus.BAD_REQUEST, "User exist!", "Bad Request", "/api/auth/signup");
        }
    }
    public UserUpdatedPasswordResponse changePass(User user, String new_pass) {
        if (breachedPasswords.contains(new_pass)) {
            throw new UserExistException(HttpStatus.BAD_REQUEST,"The password is in the hacker's database!", "Bad Request", "/api/auth/changepass" );
        }
        if (passwordEncoder.matches(new_pass, userRepository.findByEmailIgnoreCase(user.getEmail()).getPassword())) {
            throw new UserExistException(HttpStatus.BAD_REQUEST,"The passwords must be different!", "Bad Request", "/api/auth/changepass" );
        }
        user.setPassword(passwordEncoder.encode(new_pass));
        userRepository.save(user);
        return new UserUpdatedPasswordResponse(user.getEmail().toLowerCase(), "The password has been updated successfully");

    }
    public User findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }
    public List<UserResponse> findAllSortedById() {
        List<UserResponse> userResponseList = new ArrayList<>();
        if (userRepository.findAllByOrderByIdAsc().isEmpty()) {
            return userResponseList;
        }
        for (User user : userRepository.findAllByOrderByIdAsc()) {
            Set<String> setRoles = new TreeSet<>();
            for (GrantedAuthority r : user.getAuthorities()) {
                setRoles.add(r.getAuthority());
            }
            userResponseList.add(new UserResponse(user.getId(), user.getName(), user.getLastname(), user.getEmail().toLowerCase(), setRoles));
        }
        return userResponseList;
    }
    @Transactional
    public void deleteUser(String email) {
        userRepository.deleteUserByEmailIgnoreCase(email);
    }
    public void put(User user) {
        userRepository.save(user);
    }
    public void increaseFailedAttempts(User user) {
        int newFailAttempts = user.getFailed_attempt() + 1;
        user.setFailed_attempt(newFailAttempts);
        userRepository.save(user);
    }

    public void resetFailedAttempts(String email) {
        var u = userRepository.findByEmailIgnoreCase(email);
        u.setFailed_attempt(0);
        userRepository.save(u);
    }

    public void lock(User user, String pathInfo) {
        user.setAccount_non_locked(false);
        user.setLock_time(LocalDateTime.now());
        securityEventService.save(new SecurityEvent(String.valueOf(System.currentTimeMillis()),String.valueOf(SecurityAction.LOCK_USER), user.getEmail().toLowerCase(), "Lock user " + user.getEmail(), pathInfo ));
        userRepository.save(user);
    }
    public void unlock(User user, User admin) {
        user.setLock_time(null);
        user.setFailed_attempt(0);
        user.setAccount_non_locked(true);
        securityEventService.save(new SecurityEvent(String.valueOf(System.currentTimeMillis()),String.valueOf(SecurityAction.UNLOCK_USER), admin.getEmail().toLowerCase(), "Unlock user " + user.getEmail(), "/api/admin/user/access"));
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("in the user details service");
        var user = userRepository.findByEmailIgnoreCase(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found!");
        }
        if (user.isAccountNonLocked() == false) {
            throw new UsernameNotFoundException("User account is locked");
        }
        return user;
    }

}
