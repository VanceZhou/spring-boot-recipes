package com.example.recipe.DataTransferObj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

@Data
@AllArgsConstructor
@NoArgsConstructor

//this class is used to hold the info that pass from user registration and used to create account
public class SignUpRequest {
    private String username;
    private String password;
    private String email;

}
