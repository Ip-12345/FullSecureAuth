package in.indupriya.authify.controller;

import in.indupriya.authify.io.ProfileRequest;
import in.indupriya.authify.io.ProfileResponse;
import in.indupriya.authify.service.EmailService;
import in.indupriya.authify.service.ProfileService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;


//IMPORTANT: Controller handles HTTP requests and responses->like reception desk
//It doesn't handle the core business logic

@RestController
//@RequestMapping("/api/v1.0")
//This means that all endpoints in this controller will start with /api/v1.0.
//so if we call /register it becomes /api/v1.0/register
@RequiredArgsConstructor //generates constructor with one parameter
public class ProfileController {

    private final ProfileService profileService;
    private final EmailService emailService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse register(@Valid @RequestBody ProfileRequest request){
        //@RequestBody tells Spring to take the JSON body from the HTTP request and convert it to a ProfileRequest object.
        ProfileResponse response=profileService.createProfile(request);
        emailService.sendWelcomeEmail(response.getEmail(), response.getName());
        return response;
    }

//    @GetMapping("/test")
//    public String test(){
//        return "Auth is working induu";
//    }

    @GetMapping("/profile")
    public ProfileResponse getProfile(@CurrentSecurityContext(expression = "authentication?.name") String email){
        return profileService.getProfile(email);
    }
}