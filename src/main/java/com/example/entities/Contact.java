package com.example.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString(exclude = "user")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "contact_id")
    private Integer id;

    @NotBlank(message = "Name is required.")
    @Size(min = 3, message = "Name must be greater than 3 characters.")
    private String name;
    private String nickName;
    private String work;

    @Size(min = 10, max = 10, message = "Phone Number must be 10 digits long.")
    private String phone;

    @Column(unique = true)
    @Email(message = "Enter a valid email address.e.g:test@gmail.com")
    private String email;
//    private String image;

    @Column(length = 1000)
    private String description;

    @ManyToOne()
    @JsonIgnore
    private User user;

}
