package com.example.recipe.Controller;

import com.example.recipe.DataTransferObj.AuthenticationResponse;
import com.example.recipe.DataTransferObj.LoginRequest;
import com.example.recipe.DataTransferObj.RefreshTokenRequest;
import com.example.recipe.DataTransferObj.SignUpRequest;
import com.example.recipe.model.MyUser;
import com.example.recipe.service.AuthService;
import com.example.recipe.service.RefreshTokenService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
// given SignUpRequest to sign up account
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody SignUpRequest signUpRequest) throws ExecutionException, InterruptedException {
        if (authService.signup(signUpRequest).get()){
            return new ResponseEntity<>("User successful registered!", HttpStatus.OK);
        }
        return new ResponseEntity<>("The account with the same username already exist!", HttpStatus.BAD_REQUEST);
    }
//    base on the token that send to email to active account
    @GetMapping("/accountVerification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token){
        authService.verifyAccount(token);
        return new ResponseEntity<>("Account has been activated successfully", HttpStatus.OK);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody LoginRequest loginRequest) throws ExecutionException, InterruptedException {
        return authService.login(loginRequest).get();
    }

    @PostMapping("refresh/token")
    public AuthenticationResponse refreshToken (@Valid @RequestBody RefreshTokenRequest refreshTokenRequest){
        return authService.refreshToken(refreshTokenRequest);

    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(RefreshTokenRequest refreshTokenRequest){
        refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK).body("Logout successfully!");
    }
    @GetMapping("/currentUser")
    public ResponseEntity<MyUser> getCurrentUserInfo(){
        return  ResponseEntity.status(HttpStatus.FOUND).body(authService.getCurrentUser());
    }
}
