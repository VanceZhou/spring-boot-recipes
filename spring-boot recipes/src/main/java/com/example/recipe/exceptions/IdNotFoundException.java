package com.example.recipe.exceptions;

public class IdNotFoundException extends RuntimeException{
    public IdNotFoundException(String exMessage){
        super(exMessage);
    }
}
