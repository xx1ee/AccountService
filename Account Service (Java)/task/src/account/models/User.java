package account.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "userXLY")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;
    @NotBlank
    String name;
    @NotBlank
    String lastname;
    @NotBlank
    @Pattern(regexp = "[A-Za-z0-9._%+-]+@acme.com")
    String email;
    @NotNull
    @Size(min = 12, message = "Password length must be 12 chars minimum!")
    String password;
    @Value(value = "0")
    Integer failed_attempt = 0;
    @Value(value = "true")
    Boolean account_non_locked = true;
    LocalDateTime lock_time;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "userXLY_role",
            joinColumns = {@JoinColumn(name="user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    public Set<Role> authorities;

    public User(@NotBlank String name, @NotBlank String lastname, @NotBlank @Pattern(regexp = "[A-Za-z0-9._%+-]+@acme.com") String email, @NotNull String password, Set<Role> authorities) {
        super();
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public User() {
        super();
        this.authorities = new HashSet<>();
    }

    public Integer getFailed_attempt() {
        return failed_attempt;
    }

    public void setFailed_attempt(Integer failed_attempt) {
        this.failed_attempt = failed_attempt;
    }

    public Boolean getAccount_non_locked() {
        return account_non_locked;
    }

    public void setAccount_non_locked(Boolean account_non_locked) {
        this.account_non_locked = account_non_locked;
    }

    public LocalDateTime getLock_time() {
        return lock_time;
    }

    public void setLock_time(LocalDateTime lock_time) {
        this.lock_time = lock_time;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public @NotBlank String getName() {
        return this.name;
    }

    public @NotBlank String getLastname() {
        return this.lastname;
    }

    public @NotBlank @Pattern(regexp = "[A-Za-z0-9._%+-]+@acme.com") String getEmail() {
        return this.email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    public @NotNull String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return account_non_locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setName(@NotBlank String name) {
        this.name = name;
    }

    public void setLastname(@NotBlank String lastname) {
        this.lastname = lastname;
    }

    public void setEmail(@NotBlank @Pattern(regexp = "[A-Za-z0-9._%+-]+@acme.com") String email) {
        this.email = email;
    }

    public void setPassword(@NotNull String password) {
        this.password = password;
    }

    public void setAuthorities(Set<Role> authorities) {
        this.authorities = authorities;
    }
}