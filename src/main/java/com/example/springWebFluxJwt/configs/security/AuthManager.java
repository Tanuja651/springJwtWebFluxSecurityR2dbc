package com.example.springWebFluxJwt.configs.security;

import com.example.springWebFluxJwt.services.CustomReactiveUserDetailsService;
import com.example.springWebFluxJwt.services.JwtService;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Component
public class AuthManager implements ReactiveAuthenticationManager {
    final JwtService jwtService; // To validate and get Username
    private final CustomReactiveUserDetailsService customReactiveUserDetailsService;

    public AuthManager(JwtService jwtService, CustomReactiveUserDetailsService customReactiveUserDetailsService) {
        this.jwtService = jwtService;
        this.customReactiveUserDetailsService = customReactiveUserDetailsService;
    }


    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(
                authentication
        )
                .cast(BearerToken.class)
                .flatMap(auth -> {
                    String userName = jwtService.getUserName(auth.getCredentials());
                    Mono<UserDetails> foundUser = customReactiveUserDetailsService.findByUsername(userName).defaultIfEmpty(new UserDetails() {
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
                    Mono<Authentication> authenticatedUser = foundUser.flatMap(u->{
                        if(u.getUsername()==null){
                            Mono.error(new IllegalArgumentException("User not found in auth manager"));
                        }
                        if(jwtService.validate(u,auth.getCredentials())){
                            return Mono.justOrEmpty(new UsernamePasswordAuthenticationToken(u.getUsername(),u.getPassword(),u.getAuthorities()));
                        }
                        Mono.error(new IllegalArgumentException("Invalid/ Expired Token"));
//                        throw new IllegalArgumentException("Invalid Token");
                        return Mono.justOrEmpty(new UsernamePasswordAuthenticationToken(u.getUsername(),u.getPassword(),u.getAuthorities()));
                    });
                    return authenticatedUser;
                });
    }
}
