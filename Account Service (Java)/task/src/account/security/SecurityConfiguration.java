package account.security;

import account.util.CustomAccessDeniedHandler;
import account.util.CustomAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
public class SecurityConfiguration {

    @Bean
    public CustomAuthenticationEntryPoint getRestAuthenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(13);
    }
    @Bean
    public AuthenticationManager authManager(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return new ProviderManager(daoAuthenticationProvider);
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .httpBasic((httpBasic) -> httpBasic
                        .authenticationEntryPoint(getRestAuthenticationEntryPoint())
                )
                .csrf(csrf -> csrf.disable())
                .headers(headers-> headers.disable())
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/security/events/").hasAuthority("ROLE_AUDITOR")
                        .requestMatchers(HttpMethod.GET, "/api/empl/payment").hasAnyAuthority("ROLE_USER", "ROLE_ACCOUNTANT")
                        .requestMatchers(HttpMethod.POST, "/api/acct/payments").hasAuthority("ROLE_ACCOUNTANT")
                        .requestMatchers(HttpMethod.PUT, "/api/acct/payments").hasAnyAuthority("ROLE_ACCOUNTANT")
                        .requestMatchers(HttpMethod.GET, "/api/admin/user/**").hasAuthority("ROLE_ADMINISTRATOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/admin/user/**").hasAuthority("ROLE_ADMINISTRATOR")
                        .requestMatchers(HttpMethod.PUT, "/api/admin/user/role").hasAnyAuthority("ROLE_ADMINISTRATOR")
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(
                        (exceptionHandling) -> exceptionHandling
                                .accessDeniedHandler(accessDeniedHandler())

                )
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return new CustomAccessDeniedHandler();
    }
}
