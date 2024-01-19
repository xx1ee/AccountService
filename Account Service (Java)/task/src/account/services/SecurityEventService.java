package account.services;

import account.models.SecurityEvent;
import account.repos.SecurityEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecurityEventService {
    private final SecurityEventRepository securityEventRepository;
    @Autowired
    public SecurityEventService(SecurityEventRepository securityEventRepository) {
        this.securityEventRepository = securityEventRepository;
    }
    public void save(SecurityEvent securityEvent) {
        System.out.println(securityEvent.getAction());
        securityEventRepository.save(securityEvent);
    }

    public List<SecurityEvent> findAll() {
        return securityEventRepository.findAll();
    }
}
