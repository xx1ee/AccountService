package account.services;


import account.repos.UserRepository;
import account.user.Answer;
import account.user.Password;
import account.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Component
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final List<String> breachedPasswords = List.of("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User findByEmail(String email) {
        try{
            return userRepository.findUserByEmail(email);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User exist!");
        }
    }
    public Answer changePassword(UserDetails user, Password password) {
        //System.out.println(user.getUsername().toLowerCase());
        if (user != null) {
            if (password.getNew_password().length() < 12) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password length must be 12 chars minimum!");
            }
            if (breachedPasswords.contains(password.getNew_password())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password is in the hacker's database!");
            }
            //System.out.println(user.getUsername().toLowerCase());
            User user1 = userRepository.findUserByEmail(user.getUsername().toLowerCase());
            if (!passwordEncoder.matches(password.getNew_password(), user1.getPassword())) {
                //System.out.println(password + "    " + user1.getPassword());
                user1.setPassword(passwordEncoder.encode(password.getNew_password()));
                userRepository.delete(user1);
                userRepository.save(user1);
                return new Answer(user1.getEmail());
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The passwords must be different!");
            }
        } else throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    public User save(User user) {
        if (user.getPassword().length() < 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password length must be at least 12 chars!");
        }
        if (breachedPasswords.contains(user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password is in the hacker's database!");
        }
        user.setEmail(user.getEmail().toLowerCase());
        if (userRepository.existsUserByEmail(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User exist!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        userRepository.save(user);
        return user;
    }
}
