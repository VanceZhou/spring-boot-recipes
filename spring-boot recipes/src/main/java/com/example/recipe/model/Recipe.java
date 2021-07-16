package com.example.recipe.model;


import com.example.recipe.DataTransferObj.RecipeDto;
import com.example.recipe.service.AuthService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Recipe implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String username;
    private int numberOfLikes;
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ArrayList<Comment> comments;


    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotBlank
    private String category;

    private Instant date;
    @NotNull
    @Size(min = 1)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ArrayList<String> ingredients;
    @NotNull
    @Size(min = 1)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ArrayList<String> directions;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private MyUser myUser;
    public Recipe(RecipeDto recipeDto) {

        this.name = recipeDto.getName();
        this.description = recipeDto.getDescription();
        this.category = recipeDto.getCategory();
        this.ingredients = recipeDto.getIngredients();
        this.directions = recipeDto.getDirections();
    }
//been called when update or created to record the time.
    public void setDate() {
        this.date = Instant.now();
    }
}