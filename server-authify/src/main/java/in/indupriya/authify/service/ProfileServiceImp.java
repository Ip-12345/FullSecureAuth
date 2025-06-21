package in.indupriya.authify.service;

import in.indupriya.authify.entity.UserEntity;
import in.indupriya.authify.io.ProfileRequest;
import in.indupriya.authify.io.ProfileResponse;
import in.indupriya.authify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;


//IMPORTANT: ontains the actual business logic.
//Implements the interface and writes the real code — like calling the repository to save data, validating input, etc.


@Service
@RequiredArgsConstructor
public class ProfileServiceImp implements ProfileService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public ProfileResponse createProfile(ProfileRequest request){
        UserEntity newProfile=convertToUserEntity(request);
        if(!userRepository.existsByEmail(request.getEmail())) {
            newProfile=userRepository.save(newProfile);
            return convertToProfileResponse(newProfile);
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
    }

    private ProfileResponse convertToProfileResponse(UserEntity newProfile){
        return ProfileResponse.builder()
                .name(newProfile.getName())
                .email(newProfile.getEmail())
                .userId(newProfile.getUserId())
                .isAccountVerified(newProfile.getIsAccountVerified())
                .build();
    }

    private UserEntity convertToUserEntity(ProfileRequest request){
        return UserEntity.builder()
                .email(request.getEmail())
                .userId(UUID.randomUUID().toString())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .isAccountVerified(false)
                .resetOtpExpiredAt(0L)
                .verifyOtp(null)
                .verifyOtpExpiredAt(0L)
                .resetOtp(null)
                .build();
    }

    @Override
    public ProfileResponse getProfile(String email){
        UserEntity existingUser=userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("User not found: "+email));
        return convertToProfileResponse(existingUser);
    }

    @Override
    public void sendResetOtp(String email){
        UserEntity existingEntity=userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("User not found: "+email));

        String otp=String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        long expiryTime=System.currentTimeMillis()+(15*60*1000);
        existingEntity.setResetOtp(otp);
        existingEntity.setResetOtpExpiredAt(expiryTime);
        userRepository.save(existingEntity);

        try{
            emailService.sendResetOtpEmail(existingEntity.getEmail(),otp);
        }
        catch (Exception ex){
            throw new RuntimeException("Unable to send email");
        }
    }

    @Override
    public void resetPassword(String email, String otp, String newPassword){
        UserEntity existingUser=userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("User not found: "+email));
        if(existingUser.getResetOtp()==null || !existingUser.getResetOtp().equals(otp)){
            throw new RuntimeException("Invalid OTP");
        }

        if(existingUser.getResetOtpExpiredAt()<System.currentTimeMillis()){
            throw new RuntimeException("OTP Expired");
        }

        existingUser.setPassword(passwordEncoder.encode(newPassword));
        existingUser.setResetOtp(null);
        existingUser.setResetOtpExpiredAt(0L);
        userRepository.save(existingUser);
    }

    @Override
    public void sendOtp(String email){
        UserEntity existingUser=userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("User not found: "+email));
        if(existingUser.getIsAccountVerified()!=null && existingUser.getIsAccountVerified()) return;

        String otp=String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        long expiryTime=System.currentTimeMillis()+(24*60*60*1000);
        existingUser.setVerifyOtp(otp);
        existingUser.setVerifyOtpExpiredAt(expiryTime);
        userRepository.save(existingUser);
        try{
            emailService.sendOtpEmail(existingUser.getEmail(), otp);
        }
        catch (Exception e){
            throw new RuntimeException("Unable to send Email");
        }
    }

    @Override
    public void verifyOtp(String email, String otp){
        UserEntity existingUser=userRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("User not found: "+email));
        if(existingUser.getVerifyOtp()==null || !existingUser.getVerifyOtp().equals(otp)){
            throw new RuntimeException("Invalid OTP");
        }

        if(existingUser.getVerifyOtpExpiredAt()<System.currentTimeMillis()){
            throw new RuntimeException("OTP Expired");
        }

        existingUser.setIsAccountVerified(true);
        existingUser.setVerifyOtp(null);
        existingUser.setVerifyOtpExpiredAt(0L);
        userRepository.save(existingUser);
    }
}
