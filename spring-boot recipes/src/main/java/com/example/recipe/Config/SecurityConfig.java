package com.example.recipe.Config;

import com.example.recipe.security.JwtAuthenticationFilter;
import com.example.recipe.service.MyUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final MyUserDetailsService myUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

//    create bean for interface of AuthenticationManager
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }


//    configure the authentication
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService)
                .passwordEncoder(passwordEncoder());

    }

//    configure the authorization
    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        disable csrf() just for convenience, it has to be enable for browser base server for security purpose!
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/auth/accountVerification/**",
                        "/api/auth/refresh/token",
                        "/api/auth/logout",
                        "/api/auth/signup",
                        "/api/auth/login",
                        "/actuator/shutdown").permitAll()
                .antMatchers("/api/recipe/all").permitAll()
                .antMatchers(
                        "/v2/api-docs",
                        "/configuration/ui",
                        "**/swagger-resource/**",
                        "/configuration/security",
                        "/swagger-ui.html",
                        "/webjars/**").permitAll()
//                .antMatchers(HttpMethod.GET).permitAll()
//                .antMatchers("/api/recipe/new","/api/recipe/{id}","/api/recipe/search/").hasAnyRole("USER","ADMIN")
                .anyRequest()
                .authenticated();
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
// form base auth is a more powerful auth compare to base auth, but it requires section id cookies to login in postman

    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}

