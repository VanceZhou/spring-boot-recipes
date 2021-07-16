package com.example.recipe.exceptions;

public class recipeEmailSendingException extends RuntimeException{
    public recipeEmailSendingException(String exMessage){
        super(exMessage);
    }

}
