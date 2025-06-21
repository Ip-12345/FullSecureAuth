package in.indupriya.authify.service;

import in.indupriya.authify.io.ProfileRequest;
import in.indupriya.authify.io.ProfileResponse;


//IMPORTANT: This Class Defines a contract (just method names) for business logic


public interface ProfileService {
    ProfileResponse createProfile(ProfileRequest request);

    ProfileResponse getProfile(String email);

    void sendResetOtp(String email);

    void resetPassword(String email, String otp, String newPassword);

    void sendOtp(String email);
    void verifyOtp(String email, String otp);
}
