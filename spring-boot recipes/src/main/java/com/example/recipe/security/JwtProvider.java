package com.example.recipe.security;

import com.example.recipe.exceptions.recipeEmailSendingException;
import com.example.recipe.model.MyUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.sql.Date;
import java.time.Instant;

import static io.jsonwebtoken.Jwts.parser;
import static java.util.Date.from;


@Service
public class JwtProvider {
    private KeyStore keyStore;

//expire in 15 minutes
    private Long jwtExpirationInMillis = 9000000L;



    @PostConstruct
//    initialize the keystore with the jks file.
    public void init() {
        try {
            keyStore = KeyStore.getInstance("JKS");
            InputStream resourceAsStream = getClass().getResourceAsStream("/springblog.jks");
            keyStore.load(resourceAsStream, "letmein".toCharArray());
        } catch (KeyStoreException | CertificateException |NoSuchAlgorithmException | IOException e){
            throw  new recipeEmailSendingException("error happens when creating jwt key!");
        }
    }
// generate the jwt token with a private key
    public String generateToken(Authentication authentication){
        MyUserDetails principal = (MyUserDetails) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .setIssuedAt(from(Instant.now()))
                .signWith(getPrivateKey())
                .setExpiration(from(Instant.now().plusMillis(jwtExpirationInMillis)))
                .compact();
    }
//    generate token with username, in case the authentication is expire and we can not use the above method
    public String generateTokenWithUsername(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(from(Instant.now()))
                .signWith(getPrivateKey())
                .setExpiration(from(Instant.now().plusMillis(jwtExpirationInMillis)))
                .compact();
    }


//pass in the alias name and password to get private key from key store.
    private PrivateKey getPrivateKey() {
        try{
            PrivateKey privateKey =  (PrivateKey) keyStore.getKey("springblog", "letmein".toCharArray());
            return privateKey;
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e){
            throw  new recipeEmailSendingException("error happens when creating jwt key!");
        }
    }
//    validate the jwt token
    public boolean validateToken(String jwt){
        parser().setSigningKey(getPublicKey()).parseClaimsJws(jwt);
        return true;
    }
    private PublicKey getPublicKey(){
        try {
            return keyStore.getCertificate("springblog").getPublicKey();
        } catch ( KeyStoreException e){
            throw new recipeEmailSendingException("error when getting public key!");
        }
    }
    public String getUsernameFromJwt(String token){
        Claims claims = parser()
                .setSigningKey(getPublicKey())
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
    public Long getJwtExpirationInMillis(){
        return jwtExpirationInMillis;
    }


}
