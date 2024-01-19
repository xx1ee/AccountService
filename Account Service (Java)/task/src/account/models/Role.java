package account.models;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
@Entity
@Table(name = "role")
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long role;
    private String authority;

    public Role(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }

    public Role() {
        super();
    }

    public Role(Long roleId, String authority) {
        super();
        this.role = roleId;
        this.authority = authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public void setId(Long id) {
        this.role = id;
    }

    public Long getId() {
        return role;
    }
}
