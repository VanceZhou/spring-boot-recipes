package com.example.recipe.service;

import com.example.recipe.exceptions.recipeEmailSendingException;
import com.example.recipe.model.MyUser;
import com.example.recipe.model.MyUserDetails;
import com.example.recipe.repository.MyUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    private final MyUserRepository myUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<MyUser> myUser = myUserRepository.findByUsername(username);
        myUser.orElseThrow(()->new recipeEmailSendingException("user with " + username + "not found!"));
//        build userdetail using info inside myUser
        return new MyUserDetails(myUser.get());
    }
}
