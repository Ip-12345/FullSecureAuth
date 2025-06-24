package in.indupriya.authify;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.indupriya.authify.controller.AuthController;
import in.indupriya.authify.io.AuthRequest;
import in.indupriya.authify.service.AppUserDetailsService;
import in.indupriya.authify.service.ProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private AppUserDetailsService appUserDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private ProfileService profileService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testLoginSuccess() throws Exception {
        AuthRequest request = new AuthRequest("test@example.com", "password123");
        UserDetails userDetails = new User("test@example.com", "password123", Collections.emptyList());
        String token = "dummy-token";
        String role = "USER";

        // Mock dependencies
        doNothing().when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        when(appUserDetailsService.loadUserByUsername(request.getEmail())).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn(token);
        when(appUserDetailsService.getUserRole(request.getEmail())).thenReturn(role);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("jwt")))
                .andExpect(jsonPath("$.email").value(request.getEmail()))
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.role").value(role));
    }

    @Test
    public void testIsAuthenticated_True() throws Exception {
        mockMvc.perform(get("/is-authenticated").principal(() -> "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void testIsAuthenticated_False() throws Exception {
        mockMvc.perform(get("/is-authenticated").principal(() -> null))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    public void testSendResetOtp() throws Exception {
        String email = "test@example.com";
        doNothing().when(profileService).sendResetOtp(email);

        mockMvc.perform(post("/send-reset-otp").param("email", email))
                .andExpect(status().isOk());
    }

    @Test
    public void testLogout() throws Exception {
        mockMvc.perform(post("/logout"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("jwt=")))
                .andExpect(content().string("Logged out successfully!"));
    }
}
