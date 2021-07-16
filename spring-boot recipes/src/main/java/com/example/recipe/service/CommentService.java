package com.example.recipe.service;

import com.example.recipe.DataTransferObj.CommentDto;
import com.example.recipe.model.Comment;
import com.example.recipe.repository.CommentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;


@Service
@AllArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final AuthService authService;

    public boolean save(CommentDto commentDto){
//       convert CommentDto to Comment and save it.
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setCreatedDate(Instant.now());
        if (authService.getCurrentUser() == null){
            return false;
        }
        comment.setMyUser(authService.getCurrentUser());
        commentRepository.save(comment);
        return true;
    }
}
