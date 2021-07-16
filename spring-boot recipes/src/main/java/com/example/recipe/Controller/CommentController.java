package com.example.recipe.Controller;

import com.example.recipe.DataTransferObj.CommentDto;
import com.example.recipe.model.Comment;
import com.example.recipe.service.CommentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comment")
@AllArgsConstructor
@Slf4j
public class CommentController {
    private final CommentService commentService;
    @PostMapping
    public ResponseEntity<String> createComment(@RequestBody CommentDto commentDto){
        if (commentService.save(commentDto)){
            return ResponseEntity.status(HttpStatus.CREATED).body("posted comment!");
        }
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("you need to sign in before posting comment!");
    }
}
