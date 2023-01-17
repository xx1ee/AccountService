package account.controllers;
import account.models.User;
import account.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.security.Principal;

@RestController
@RequestMapping("api/")
public class AccountController {
    @Autowired
    private UserRepository userRepository;
    @PostMapping("auth/signup")
    public ResponseEntity<?> registerUser(@RequestBody @Valid @NotNull User user) {
        if (userRepository.existsUserByEmailIgnoreCase(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User exist!");
        }
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }
    @GetMapping("empl/payment")
    public User paymentUser(@AuthenticationPrincipal User user) {
        String email = user.getUsername();
        return userRepository.findByEmailIgnoreCase(email).get();
    }
}
