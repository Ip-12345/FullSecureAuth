package in.indupriya.authify.config;


import in.indupriya.authify.filter.JwtRequestFilter;
import in.indupriya.authify.service.AppUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor //used for constructor based injection: to inject AppUserDetailsService below
public class SecurityConfig {

    private final AppUserDetailsService appUserDetailsService;
    private final JwtRequestFilter jwtRequestFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    //SecurityFilterChain is a bean that defines your app’s security configuration (what URLs need login, what roles can access them, etc.).
    @Bean
    //This tells Spring: “Hey! I’m creating a bean (i.e., a component you’ll manage in the application context).”
    //In this case, it's a security configuration bean that defines how security filters should behave.
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->auth
                        .requestMatchers("/login", "/register", "/send-reset-otp", "/reset-password", "/logout")
                        .permitAll().anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .logout(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex->ex.authenticationEntryPoint(customAuthenticationEntryPoint));
        return http.build();
    }

    @Bean
    //PasswordEncoder is an interface in Spring Security used to hash and verify passwords.
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
        //BCryptPasswordEncoder is an implementation of PasswordEncoder that uses BCrypt, a strong hashing algorithm designed for passwords.
        //This will:
        //Encrypt passwords during registration.
        //Verify passwords during login by comparing encrypted hashes
    }

    //This method returns a CorsFilter, which Spring Security can use to allow/deny cross-origin HTTP requests.
    //It takes a CorsConfigurationSource, which defines what CORS rules to follow.
    //You’re calling another method corsConfigurationSource() to define that.
    @Bean
    public CorsFilter corsFilter(){
        return new CorsFilter(corsConfigurationSource());
    }

    private CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration config=new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("POST", "PUT", "DELETE", "OPTIONS", "PATCH", "GET"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    //Declares this method as a Spring Bean — so Spring will manage this AuthenticationManager and allow it to be injected elsewhere.
    public AuthenticationManager authenticationManager(){

        //Creates an authentication provider that fetches user details from your DB.
        DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider();

        //You’re injecting your custom implementation of UserDetailsService.
        //This will fetch user info (like email, password) from the database.
        authenticationProvider.setUserDetailsService(appUserDetailsService);

        //Injects a password encoder (e.g., BCrypt) so that passwords can be safely compared.
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        //Wraps your DaoAuthenticationProvider inside a ProviderManager, which is a type of AuthenticationManager.
        //AuthenticationManager is like a boss.
        //ProviderManager is the manager who delegates the work to one or more AuthenticationProviders.
        //DaoAuthenticationProvider is an employee that knows how to authenticate a user using a database.
        return new ProviderManager(authenticationProvider);
    }
}
