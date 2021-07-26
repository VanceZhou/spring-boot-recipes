package com.example.recipe.service;

import com.example.recipe.DataTransferObj.AuthenticationResponse;
import com.example.recipe.DataTransferObj.LoginRequest;
import com.example.recipe.DataTransferObj.RefreshTokenRequest;
import com.example.recipe.DataTransferObj.SignUpRequest;
import com.example.recipe.exceptions.recipeEmailSendingException;
import com.example.recipe.model.MyUser;
import com.example.recipe.model.MyUserDetails;
import com.example.recipe.model.NotificationEmail;
import com.example.recipe.model.VerificationToken;
import com.example.recipe.repository.MyUserRepository;
import com.example.recipe.repository.VerificationTokenRepository;
import com.example.recipe.security.JwtProvider;
import lombok.AllArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service

@AllArgsConstructor
public class AuthService {
    //    better practice to use constructor than autowired
    private final PasswordEncoder passwordEncoder;
    private final MyUserRepository myUserRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    //    by default, the SecurityContextHolder is bounded to local thread, if we wanna use it in multi-thread, we need to create following bean
    @Bean
    public DelegatingSecurityContextAsyncTaskExecutor taskExecutor(ThreadPoolTaskExecutor delegate) {
        return new DelegatingSecurityContextAsyncTaskExecutor(delegate);
    }

    @Transactional
    @Async
    public CompletableFuture<Boolean> signup(SignUpRequest signUpRequest){
//       Check if username already exist before sign up.
        MyUser signupUser = myUserRepository.findByUsername(signUpRequest.getUsername())
                .orElse(null);
        if (signupUser != null){
            return CompletableFuture.completedFuture(false);
        }
//        Using the password encoder to encode the password before save it into database.
        signUpRequest.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        MyUser myUSer = new MyUser(signUpRequest);
        myUSer.setEnable(false);
//        Save the user into database
        myUserRepository.save(myUSer);
        String token = generateVerificationToken(myUSer);
        mailService.sendMail(new NotificationEmail("please activate your account!", myUSer.getEmail(),
                "please click on the link to activate your account: " + "http://localhost:8080/api/auth/accountVerification/" + token));
        return CompletableFuture.completedFuture(true);
    }
    // verification token is used to send to user email and verify the sign up info. not JWT
    private String generateVerificationToken(MyUser myUSer) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setMyUser(myUSer);
        verificationTokenRepository.save(verificationToken);
        return token;
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow(()-> new recipeEmailSendingException("can not find the token!"));
//        if the token is valid, find the account that has that token, and set it as enable.
        Long ID = verificationToken.get().getMyUser().getUserId();
//        better to find by id because find by username might find multiple users with the same name.
        Optional<MyUser> userToActivate = myUserRepository.findById(ID);
        userToActivate.orElseThrow(()-> new recipeEmailSendingException("can not find the user!"));
        userToActivate.get().setEnable(true);
        myUserRepository.save(userToActivate.get());
    }

    @Async
    public CompletableFuture<AuthenticationResponse> login(LoginRequest loginRequest){
//        use username and password tp generate authentication.
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtToken = jwtProvider.generateToken(authentication);
        Instant expirationDate = Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis());
//        generate an AuthenticationResponse with jwtToken and expiration date.
        String refreshToken = refreshTokenService.generateRefreshToken().getToken();
        return CompletableFuture.completedFuture(new AuthenticationResponse(jwtToken, loginRequest.getUsername(),refreshToken , expirationDate));
    }

    @Transactional
    public MyUser getCurrentUser() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MyUser currentUser = myUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + username));
        return currentUser;
    }

    //get a RefreshTokenRequest with refresh token and username
    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
//        check if valid, if not, exception will comes up
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
//        generate a new jwt token with username
        String token = jwtProvider.generateTokenWithUsername(refreshTokenRequest.getUsername());
//        get request token from RefreshTokenRequest
        String refreshToken = refreshTokenRequest.getRefreshToken();
        Instant expirationDate = Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis());
//        send the AuthenticationResponse back to front end
        return new AuthenticationResponse(token, refreshTokenRequest.getUsername(),refreshToken, expirationDate);

    }
}
