package in.indupriya.authify.service;

import in.indupriya.authify.entity.UserEntity;
import in.indupriya.authify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//@Service: This tells Spring to treat this class as a service and manage it as a bean (so it can be injected wherever needed).
@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {
    //injecting the user repository
    private final UserRepository userRepository;

    @Override
    ////loadUserByUsername: This method is called automatically by Spring Security when someone tries to log in.
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        UserEntity existingUser =userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("User not found for the email provided: "+email));
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + existingUser.getRole()));
        return new User(existingUser.getEmail(), existingUser.getPassword(), authorities);
        //A new User object is returned. This is Spring Security’s built-in implementation of UserDetails.
        //It contains:
        //The user’s email (used as the username)
        //The password (hashed)
        //An empty list of authorities/roles (you can add roles later if needed)
    }

    public String getUserRole(String email) {
        return userRepository.findByEmail(email)
                .map(UserEntity::getRole)
                .orElse("USER");
    }
}
