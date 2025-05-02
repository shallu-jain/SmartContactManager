package com.example.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "contact")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Integer id;

    @NotBlank(message = "Name field is Required")
    @Size(min = 3, max = 20, message = "Value must be between 3 to 20 characters")
    private String name;

    //@Column(unique = true)
    @NotBlank(message = "Email can't be empty")
    @Email(message = "Enter a valid email address.e.g:test@gmail.com")
    private String email;

    @NotBlank(message = "Password field is Required")
    @Size(min = 8, message = "Value must be at least 8 characters")
    private String password;
    private String role;
    private boolean enabled;
    private String imageUrl;
    @Column(length = 700)
    private String about;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Contact> contact = new ArrayList<>();

    /*@Override
    public String toString() {
        return
                "id=" + id +
                        ", name='" + name + '\'' +
                        ", email='" + email + '\'' +
                        ", password='" + password + '\'' +
                        ", role='" + role + '\'' +
                        ", enabled=" + enabled +
                        ", imageUrl='" + imageUrl + '\'' +
                        ", about='" + about + '\'' +
                        ", contact=" + contact;
    }*/
}
