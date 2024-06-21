package dev.mochahaulier.bankingtest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientRequest {
    @NotNull(message = "First name is required.")
    private String firstName;
    @NotNull(message = "Last name is required.")
    private String lastName;

    @NotNull(message = "E-mail is required.")
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String email;
    @NotNull(message = "Phone is required.")
    private String phone;
}
