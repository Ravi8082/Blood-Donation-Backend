package com.example.demo.Entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.*;


@Entity
@Getter
@Setter
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Blood group is required")
    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Invalid blood group format")
    private String bloodgroup;

    @Min(value = 1, message = "Minimum 1 unit required")
    @Max(value = 5, message = "Cannot request more than 5 units")
    private int units;

    @NotBlank(message = "Disease field is required")
    private String disease;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number format")
    private String mobile;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(Male|Female|Other)$", message = "Gender must be Male, Female, or Other")
    private String gender;

    @Min(value = 18, message = "Age must be 18 or above")
    @Max(value = 100, message = "Age must be below 100")
    private int age;

    @NotBlank(message = "Status is required")
    @Pattern(regexp = "^(Pending|Approved|Rejected)$", message = "Status must be Pending, Approved, or Rejected")
    private String status;

    public Request() {
        super();
    }
}
