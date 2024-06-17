package com.example.springWebFluxJwt.controller;

import com.example.springWebFluxJwt.models.reqrespBodies.ReqLogin;
import com.example.springWebFluxJwt.models.reqrespModel.ReqRespModel;
import com.example.springWebFluxJwt.services.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Collection;

@RestController
public class AuthController {

    final ReactiveUserDetailsService users;
    final JwtService jwtService;
    final PasswordEncoder passwordEncoder;

    public AuthController(ReactiveUserDetailsService users, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/auth")
    public Mono<ResponseEntity<ReqRespModel<String>>> auth(){
//        return Mono.just("Hi from auth");
        return Mono.just(
                ResponseEntity.ok(
                        new ReqRespModel<>("Welcome to private auth","")
                )
        );

    }

    @PostMapping("/login")
    public Mono<ResponseEntity<ReqRespModel<String>>> login(@RequestBody ReqLogin user){
//        return Mono.just(
//                ResponseEntity.ok(
//                        new ReqRespModel<>("Hi from login"+ user.getEmail(),"")
//                )
//        );
        Mono<UserDetails> foundUser = users.findByUsername(user.getEmail()).defaultIfEmpty(new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public String getPassword() {
                return null;
            }

            @Override
            public String getUsername() {
                return null;
            }
        });

        return foundUser.map(
                u -> {
                    // check if user found or not
                    if (u.getUsername() == null) {
                        return ResponseEntity.status(404).body(new ReqRespModel<>("", "User not found."));
                    }
                    if (passwordEncoder.matches(user.getPassword(), u.getPassword())){
                        return ResponseEntity.ok(
                                new ReqRespModel<>(jwtService.generate(u.getUsername()), "Success"));
                    }
                    return ResponseEntity.badRequest().body(new ReqRespModel<>("","Invalid Credentials"));
                }
                );
    }
}
