package com.example.recipe.model;

import com.example.recipe.DataTransferObj.SignUpRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MyUser {
//can not autowired a object of class inside entity

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long userId;
    @NotBlank
    private String username;
    @NotBlank
    @Size(min = 3)
    private String password;
    @Pattern(regexp = ".+@.+\\..+", message = "Please provide a valid email address")
    @NotNull
    private String email;
    private boolean enable;

    public MyUser(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;

    }
    public MyUser(SignUpRequest signUpRequest) {
        this.username = signUpRequest.getUsername();
        this.email = signUpRequest.getEmail();
        this.password = signUpRequest.getPassword();
    }

}