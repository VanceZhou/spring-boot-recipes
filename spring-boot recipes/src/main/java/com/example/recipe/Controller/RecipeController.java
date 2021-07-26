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
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/recipe")
@AllArgsConstructor
public class RecipeController {
    private final RecipeService recipeService;
    private final AuthService authService;


    @PostMapping("/new")
    public ResponseEntity<RecipeDto> postRecipe(@Valid @RequestBody RecipeDto recipeDto) throws ExecutionException, InterruptedException {
        return new ResponseEntity<RecipeDto>(recipeService.postRecipe(recipeDto).get(), HttpStatus.CREATED);
    }
    @GetMapping("/all")
    public List<Recipe> getAllRecipe() throws ExecutionException, InterruptedException {
        return recipeService.getAllRecipe().get();
    }
    @GetMapping("/{id}")
    public Recipe searchRecipeById(@PathVariable Long id) throws ExecutionException, InterruptedException {
        return recipeService.findById(id).get();
    }

    @GetMapping("/search/")
    public ArrayList<Recipe> searchByCategory(@RequestParam(required = false) String category, @RequestParam(required = false) String name) throws ExecutionException, InterruptedException {
        return recipeService.searchByCategory(category, name).get();
    }
    @GetMapping("/profile/{username}")
    public List<Recipe> searchByUsername(@PathVariable String username) throws ExecutionException, InterruptedException {
        return recipeService.findRecipesByUsername(username).get();
    }
    @PutMapping("/{id}")
    public ResponseEntity<String> updateRecipe(@PathVariable Long id, @Valid @RequestBody RecipeDto recipeDto) throws ExecutionException, InterruptedException {
        return recipeService.updateRecipe(id, recipeDto).get();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRecipe(@PathVariable Long id) throws ExecutionException, InterruptedException {
        return recipeService.deleteRecipe(id).get();
    }


}
