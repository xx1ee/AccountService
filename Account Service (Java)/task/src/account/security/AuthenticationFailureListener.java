package account.security;

import account.models.*;
import account.repos.SecurityEventRepository;
import account.repos.UserRepository;
import account.services.SecurityEventService;
import account.services.UserService;
import account.util.UserExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
    private final UserService userService;
    private final UserRepository userRepository;
    private final SecurityEventRepository eventRepository;
    private final SecurityEventService eventService;

    @Autowired
    public AuthenticationFailureListener(
            UserService userService,
            UserRepository userRepository,
            SecurityEventRepository eventRepository, SecurityEventService eventService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.eventService = eventService;
    }

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String pathInfo = attributes.getRequest().getRequestURI();
        String userEmail = ((String) event.getAuthentication().getPrincipal()).toLowerCase();
        System.out.println(userEmail);
        System.out.println("в секурити евенте");
        User user = userService.findByEmail(userEmail);
        if (user == null) {
            eventService.save(new SecurityEvent(String.valueOf(System.currentTimeMillis()), SecurityAction.LOGIN_FAILED.toString(), userEmail, pathInfo, pathInfo));
        }
        if (user != null) {
            if (user.isAccountNonLocked()) {
                eventService.save(new SecurityEvent(String.valueOf(System.currentTimeMillis()), SecurityAction.LOGIN_FAILED.toString(), userEmail, pathInfo, pathInfo));
            }
            if (user.isEnabled() && user.isAccountNonLocked()) {
                if (user.getFailed_attempt() < UserService.MAX_FAILED_ATTEMPTS) {
                    userService.increaseFailedAttempts(user);
                } else {
                    for (Role r : user.authorities) {
                        if (r.getAuthority().equals("ROLE_ADMINISTRATOR")) {
                            throw new UserExistException(HttpStatus.BAD_REQUEST, "Can't lock the ADMINISTRATOR!", "Bad Request", pathInfo);
                        }
                    }
                    eventRepository.save(new SecurityEvent(String.valueOf(System.currentTimeMillis()),String.valueOf(SecurityAction.BRUTE_FORCE), userEmail, pathInfo, pathInfo));
                    userService.lock(user, pathInfo);
                }
            } else if (!user.isAccountNonLocked()) {
                throw new LockedException("User account is locked");
            }
        }
    }
}
