package account.controllers;

import account.user.Answer;
import account.user.Password;
import account.user.User;
import account.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("api")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/auth/signup")
    public User registerUser(@Valid @RequestBody User user){
        return userService.save(user);
    }
    @PostMapping("/auth/changepass")
    public Answer changepassUser(@AuthenticationPrincipal UserDetails user, @RequestBody @NotEmpty Password password){
        return userService.changePassword(user, password);
    }

    @GetMapping("/empl/payment")
    public User paymentUser(@AuthenticationPrincipal UserDetails user) {
        return userService.findByEmail(user.getUsername());
    }
}
