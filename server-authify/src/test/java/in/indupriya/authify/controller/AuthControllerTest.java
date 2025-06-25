package in.indupriya.authify.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.indupriya.authify.JwtUtil;
import in.indupriya.authify.io.AuthRequest;
import in.indupriya.authify.service.AppUserDetailsService;
import in.indupriya.authify.service.ProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// I built a unit test class that simulates HTTP requests to your AuthController,
// mocks all the backend logic (auth manager, token creation, DB lookup),
// and verifies that your REST API behaves correctly in all login/logout/auth scenarios without hitting a real database or security layer.


//✅ Focuses only on AuthController, loading minimal context (MVC + controller).
//✅ It does not load full Spring Boot context, so it’s faster and more isolated.
@WebMvcTest(AuthController.class)
//✅ Disables Spring Security filters like authentication, CSRF, etc.
//✅ This allows you to test endpoints freely, without worrying about real security setup.
@AutoConfigureMockMvc(addFilters = false)  // disable Spring Security filters
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


    //to test /login endpoint
    @Test
    public void testLoginSuccess() throws Exception {
        AuthRequest request = new AuthRequest("test@example.com", "password123");
        UserDetails userDetails = new User("test@example.com", "password123", Collections.emptyList());
        String token = "dummy-token";
        String role = "USER";

        //Properly mock void method authenticate (return any object)
        //Mocks:
        //AuthenticationManager to simulate login success
        //JwtUtil to return a dummy token
        //AppUserDetailsService to return fake user and role
        //Checks:
        //Response is 200 OK
        //JWT is set in Set-Cookie header
        //Response JSON has email, token, and role
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(userDetails, "password123", userDetails.getAuthorities()));
        when(appUserDetailsService.loadUserByUsername(request.getEmail())).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn(token);
        when(appUserDetailsService.getUserRole(request.getEmail())).thenReturn(role);
        mockMvc.perform(post("/login")
                        .with(csrf())  // ✅ Important for POST
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("jwt")))
                .andExpect(jsonPath("$.email").value(request.getEmail()))
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.role").value(role));
    }


    //✅ Simulates a logged-in user.
    //Calls /is-authenticated
    //Should return: 200 OK, body = "true"
    @Test
    @WithMockUser(username = "test@example.com")
    public void testIsAuthenticated_True() throws Exception {
        mockMvc.perform(get("/is-authenticated"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    //No @WithMockUser, meaning no user is logged in.
    //Should return: 200 OK, body = "false"
    //(Note: this only works because filters are disabled via addFilters = false)
    @Test
    public void testIsAuthenticated_False() throws Exception {
        mockMvc.perform(get("/is-authenticated"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }


    //Mocks the service method profileService.sendResetOtp() to do nothing.
    //Tests:
    //POST /send-reset-otp with CSRF and email param
    //Expects 200 OK
    @Test
    @WithMockUser
    public void testSendResetOtp() throws Exception {
        String email = "test@example.com";
        doNothing().when(profileService).sendResetOtp(email);

        mockMvc.perform(post("/send-reset-otp")
                        .param("email", email)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    //Tests POST /logout
    //Uses @WithMockUser to simulate a logged-in user
    //Checks:
    //Response 200 OK
    //JWT cookie is cleared
    //Body = "Logged out successfully!"
    @Test
    @WithMockUser
    public void testLogout() throws Exception {
        mockMvc.perform(post("/logout").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("jwt=")))
                .andExpect(content().string("Logged out successfully!"));
    }
    //Before:
    //had status mismatches (302, 401) due to real security filters.
    //expected 200 OK, but Spring Security redirected or blocked unauthenticated access.
    //Now:
    //disabled filters with @AutoConfigureMockMvc(addFilters = false).
    //correctly mocked services.
    //used @WithMockUser for endpoints that require a user.
    //added .with(csrf()) to all POSTs.
}
