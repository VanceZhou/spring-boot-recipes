package com.example.recipe.Controller;

import com.example.recipe.DataTransferObj.RecipeDto;
import com.example.recipe.model.Recipe;
import com.example.recipe.service.AuthService;
import com.example.recipe.service.RecipeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/recipe")
@AllArgsConstructor
public class RecipeController {
    private final RecipeService recipeService;
    private final AuthService authService;


    @PostMapping("/new")
    public ResponseEntity<RecipeDto> postRecipe(@Valid @RequestBody RecipeDto recipeDto) {
       return new ResponseEntity<>(recipeService.postRecipe(recipeDto), HttpStatus.CREATED);
    }
    @GetMapping("/all")
    public List<Recipe> getAllRecipe(){
        return recipeService.getAllRecipe();
    }
    @GetMapping("/{id}")
    public Recipe searchRecipeById(@PathVariable Long id){
        return recipeService.findById(id);
    }

    @GetMapping("/search/")
    public ArrayList<Recipe> searchByCategory(@RequestParam(required = false) String category, @RequestParam(required = false) String name) {
        return recipeService.searchByCategory(category, name);
    }
    @GetMapping("/profile/{username}")
    public List<Recipe> searchByUsername(@PathVariable String username) {
        return recipeService.findRecipesByUsername(username);
    }
    @PutMapping("/{id}")
    public ResponseEntity<String> updateRecipe(@PathVariable Long id, @Valid @RequestBody RecipeDto recipeDto) {
        return recipeService.updateRecipe(id, recipeDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRecipe(@PathVariable Long id) {
        return recipeService.deleteRecipe(id);
    }


}
