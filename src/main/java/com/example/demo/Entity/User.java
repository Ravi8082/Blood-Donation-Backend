package com.example.demo.Entity;

import com.example.demo.Enum.Role;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Email(regexp = "^[a-zA-Z0-9._-]+@(gmail\\.com|yahoo\\.com|outlook\\.com)$", message = "Please use a valid Gmail, Yahoo, or Outlook email")
    private String email;

    @Column(nullable = false)
   @NotBlank(message = "Password is required")
   @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String name;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number")
    private String mobile;

    @NotNull(message = "Age is required")
    @Pattern(regexp = "^(1[89]|[2-9][0-9])$", message = "Age must be 18 or above")
    private String age;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(Male|Female|Other)$", message = "Gender must be Male, Female, or Other")
    private String gender;

    @NotBlank(message = "Address is required")
    private String address;

    @Column(name = "bloodgroup")
    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Invalid blood group format")
    private String bloodgroup;

    private boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @PrePersist
    public void prePersist() {
        this.isActive = true; // Default user to active on creation
        if (this.role == null) {
            this.role = Role.USER; // Default role set to USER
        }
    }

    public User(String name, String email, String password, String address, String mobile, String age, String gender, String bloodgroup) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.mobile = mobile;
        this.gender = gender;
        this.age = age;
        this.bloodgroup = bloodgroup;
    }
}
