package account.security;

import account.errors.CustomAccessDeniedHandler;
import account.services.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import jakarta.servlet.DispatcherType;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@EnableWebSecurity
@Configuration
public class WebSecurity {

    UserDetailsServiceImpl userDetailsService;
    RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    public WebSecurity(UserDetailsServiceImpl userDetailsService, RestAuthenticationEntryPoint restAuthenticationEntryPoint) {
        this.userDetailsService = userDetailsService;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Configure Basic Auth + custom entry point
                .httpBasic(httpBasic ->
                        httpBasic.authenticationEntryPoint(restAuthenticationEntryPoint)
                )

                // 2. Disable CSRF and frameOptions (for H2 console / Postman, if needed)
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))

                // 3. Force HTTPS on all requests
                .requiresChannel(channel ->
                        channel.anyRequest().requiresSecure()
                )

                // 4. Authorize requests
                .authorizeHttpRequests(authz -> authz
                                .dispatcherTypeMatchers(DispatcherType.ASYNC, DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                                .requestMatchers("/api/auth/signup").permitAll()
                                .requestMatchers("/api/auth/changepass").authenticated()
                                .requestMatchers("/api/empl/payment").hasAnyRole("USER", "ACCOUNTANT")
                                .requestMatchers("/api/acct/payments").hasRole("ACCOUNTANT")
                                .requestMatchers("/api/admin/**").hasRole("ADMINISTRATOR")
                                .requestMatchers(HttpMethod.GET, "/api/security/events").hasRole("AUDITOR")
                        // You can add more matchers if needed
                )

                // 5. Disable session creation (STATELESS)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 6. Exception handling & custom AccessDeniedHandler
                .exceptionHandling(exceptions ->
                        exceptions.accessDeniedHandler(new CustomAccessDeniedHandler())
                );

        // Build and return the configured SecurityFilterChain
        return http.build();
    }
}
//TODO: 1. ERROR: For some reason the handle method of CustomAccessDeniedHandler and the commence method of
// RestAuthenticationEntryPoint are called twice. Why?