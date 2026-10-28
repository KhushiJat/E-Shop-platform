package com.ecommerce.project.security;

import com.ecommerce.project.model.AppRole;
import com.ecommerce.project.model.Role;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repositories.RoleRepository;
import com.ecommerce.project.repositories.UserRepository;
import com.ecommerce.project.security.jwt.AuthEntryPointJwt;
import com.ecommerce.project.security.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    // Injecting environment variables for default users with fallback defaults
    @Value("${DEFAULT_USER_USERNAME:user1}")
    private String userUsername;

    @Value("${DEFAULT_USER_EMAIL:user1@example.com}")
    private String userEmail;

    @Value("${DEFAULT_USER_PASSWORD:password1}")
    private String userPassword;

    @Value("${DEFAULT_SELLER_USERNAME:seller1}")
    private String sellerUsername;

    @Value("${DEFAULT_SELLER_EMAIL:seller1@example.com}")
    private String sellerEmail;

    @Value("${DEFAULT_SELLER_PASSWORD:password2}")
    private String sellerPassword;

    @Value("${DEFAULT_ADMIN_USERNAME:admin}")
    private String adminUsername;

    @Value("${DEFAULT_ADMIN_EMAIL:admin@example.com}")
    private String adminEmail;

    @Value("${DEFAULT_ADMIN_PASSWORD:adminPass}")
    private String adminPassword;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/", "/error").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/seller/**").hasAnyRole("ADMIN", "SELLER")
                        .requestMatchers("/api/test/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        http.headers(headers -> headers.frameOptions(
                frameOptions -> frameOptions.sameOrigin()));
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring().requestMatchers(
                "/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**"));
    }

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, javax.sql.DataSource dataSource) {
        return args -> {
            // Ensure roles exist in the database first
            roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_USER)));
            roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_SELLER)));
            roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_ADMIN)));

            org.springframework.jdbc.core.JdbcTemplate jdbc = new org.springframework.jdbc.core.JdbcTemplate(dataSource);

            // Seed users cleanly using direct SQL to avoid any Hibernate detached entity bugs
            seedUserViaSql(jdbc, passwordEncoder, userUsername, userEmail, passwordEncoder.encode(userPassword), 1L); // 1 = ROLE_USER
            seedUserViaSql(jdbc, passwordEncoder, sellerUsername, sellerEmail, passwordEncoder.encode(sellerPassword), 2L); // 2 = ROLE_SELLER

            // Seed admin with all roles (1, 2, 3)
            seedAdminViaSql(jdbc, passwordEncoder, adminUsername, adminEmail, passwordEncoder.encode(adminPassword));
        };
    }

    private void seedUserViaSql(org.springframework.jdbc.core.JdbcTemplate jdbc, PasswordEncoder encoder, String username, String email, String encodedPass, Long roleId) {
        try {
            Integer count = jdbc.queryForObject("SELECT count(*) FROM users WHERE username = ?", Integer.class, username);
            if (count == null || count == 0) {
                jdbc.update("INSERT INTO users (username, email, password, enabled) VALUES (?, ?, ?, true)", username, email, encodedPass);
                Long userId = jdbc.queryForObject("SELECT user_id FROM users WHERE username = ?", Long.class, username);
                jdbc.update("INSERT INTO user_role (user_id, role_id) VALUES (?, ?)", userId, roleId);
            }
        } catch (Exception e) {
            System.err.println("Error seeding user " + username + ": " + e.getMessage());
        }
    }

    private void seedAdminViaSql(org.springframework.jdbc.core.JdbcTemplate jdbc, PasswordEncoder encoder, String username, String email, String encodedPass) {
        try {
            Integer count = jdbc.queryForObject("SELECT count(*) FROM users WHERE username = ?", Integer.class, username);
            if (count == null || count == 0) {
                jdbc.update("INSERT INTO users (username, email, password, enabled) VALUES (?, ?, ?, true)", username, email, encodedPass);
                Long userId = jdbc.queryForObject("SELECT user_id FROM users WHERE username = ?", Long.class, username);
                jdbc.update("INSERT INTO user_role (user_id, role_id) VALUES (?, ?)", userId, 1L);
                jdbc.update("INSERT INTO user_role (user_id, role_id) VALUES (?, ?)", userId, 2L);
                jdbc.update("INSERT INTO user_role (user_id, role_id) VALUES (?, ?)", userId, 3L);
            }
        } catch (Exception e) {
            System.err.println("Error seeding admin " + username + ": " + e.getMessage());
        }
    }
}