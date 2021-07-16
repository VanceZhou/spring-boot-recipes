package com.example.recipe.service;


import com.example.recipe.DataTransferObj.RecipeDto;
import com.example.recipe.exceptions.IdNotFoundException;
import com.example.recipe.model.MyUser;
import com.example.recipe.model.Recipe;
import com.example.recipe.repository.RecipeRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;

@Service
@AllArgsConstructor
public class RecipeService {
    private final AuthService authService;
    private final RecipeRepository recipeRepository;

    public RecipeDto postRecipe(RecipeDto recipeDto){
        MyUser currentUser = authService.getCurrentUser();
        Recipe recipeToPost = new Recipe(recipeDto);
        recipeToPost.setMyUser(currentUser);
        recipeToPost.setDate(Instant.now());
        recipeToPost.setUsername(currentUser.getUsername());
        recipeToPost.setComments(null);
        recipeToPost.setNumberOfLikes(0);
        recipeRepository.save(recipeToPost);
        return recipeDto;
    }

    public Recipe findById(Long id){
        Recipe recipe = recipeRepository.findById(id)
            .orElseThrow(() -> new IdNotFoundException("ID " + id + "not found!"));
        return recipe;
    }

    public ArrayList<Recipe> searchByCategory(String category, String name) {
        ArrayList<Recipe> foundRecipe = new ArrayList<>();
        Iterable<Recipe> allRecipes = recipeRepository.findAll();
        if (category != null && name == null) {
            for (Recipe recipe : allRecipes) {
                //Since category is certain type, so is must be exactly matches the input category
                if (category.matches(recipe.getCategory().toLowerCase())) {
                    foundRecipe.add(recipe);
                }
            }
        } else if (category == null && name != null) {
            for (Recipe recipe : allRecipes) {
                //As long as the name contains the input name, it counts as same name
                if (recipe.getName().toLowerCase().contains(name)) {
                    foundRecipe.add(recipe);
                }
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "wrong number of RequestParam");
        }

        Collections.sort(foundRecipe, Collections.reverseOrder(Comparator.comparing(Recipe::getDate)));
        return foundRecipe;
    }

    public ResponseEntity<String> updateRecipe(Long id, RecipeDto recipeDto) {
        String currentUsername = authService.getCurrentUser().getUsername();
        Optional<Recipe> recipeOptional = recipeRepository.findById(id);
        if (recipeOptional.isPresent()) {
            if (recipeOptional.get().getMyUser().getUsername().equals(currentUsername)) {
                Recipe recipe = new Recipe(recipeDto);
                // Since the id is automatically generated, then the recipe.ID will be different from the founded recipe
                // Therefore we need to update the id to be the same as the old recipe id.
                recipe.setId(recipeOptional.get().getId());
                // Update date since it is determined by the add time or last update time.
                recipe.setMyUser(authService.getCurrentUser());
                recipe.setDate();
                recipeRepository.save(recipe);

                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Recipe was updated");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Recipe does not belongs to this owner");
            }
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipe not found");

    }
    public ResponseEntity<String> deleteRecipe(Long id) {
        MyUser currentUser = authService.getCurrentUser();
        String username = currentUser.getUsername();

        Recipe recipeToDelete = recipeRepository.findById(id)
                .orElse(null);
        System.out.println("found?");
        if (recipeToDelete != null && recipeToDelete.getMyUser().getUsername().equals(currentUser.getUsername())) {
            recipeRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body("Recipe with id: " + id + " deleted!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipe with id: " + id + " not found!");
        }

    }
    public List<Recipe> findRecipesByUsername(String username){
//        String username = authService.getCurrentUser().getUsername();
        List<Recipe> recipes = recipeRepository.findByUsername(username)
                .orElse(null);
        if (recipes != null){
            return recipes;
        }
        return Collections.EMPTY_LIST;
    }

    public List<Recipe> getAllRecipe() {
        List<Recipe> recipeList = recipeRepository.findAll();
        return recipeList;
    }
}
