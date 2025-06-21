package in.indupriya.authify.io;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
public class ProfileRequest {
    @NotBlank (message = "Name should not be empty")
    private String name;
    @Email (message = "Enter valid email address")
    @NotNull (message = "Email should not be empty")
    private String email;
    @Size(min=6, message="Password should be atleast 6 characters")
    private String password;
}
