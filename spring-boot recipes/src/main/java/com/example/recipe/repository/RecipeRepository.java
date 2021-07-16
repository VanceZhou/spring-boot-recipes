package com.example.recipe.repository;

import com.example.recipe.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Optional<Recipe> findById(Long id);
//    Optional<Recipe> findByUsername(String username);

    Optional<Recipe> findByName(String name);

    Optional<List<Recipe>> findByUsername(String username);
}
