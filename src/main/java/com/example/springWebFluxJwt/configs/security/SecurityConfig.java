package com.example.springWebFluxJwt.configs.security;


import com.example.springWebFluxJwt.services.CustomReactiveUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final CustomReactiveUserDetailsService userDetailsService;
    private final AuthConverter jwtAuthConverter;
    private final AuthManager jwtAuthManager;

    public SecurityConfig(CustomReactiveUserDetailsService userDetailsService, AuthConverter jwtAuthConverter, AuthManager jwtAuthManager) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthConverter = jwtAuthConverter;
        this.jwtAuthManager = jwtAuthManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

//    // Fake list of users, in real project you want to fetch the users from your database
//    @Bean
//    public MapReactiveUserDetailsService userDetailsService(PasswordEncoder encoder){
//        //Spring is going to take care of supplying us the argument through dependency injection
//        UserDetails user = User.builder()
//                .username("kuku")
//                .password(encoder.encode("kuku"))
//                .roles("USER")
//                .build();
//        return new MapReactiveUserDetailsService(user);
//    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, AuthConverter jwtAuthConverter, AuthManager jwtAuthManager){
        AuthenticationWebFilter jwtFilter = new AuthenticationWebFilter(jwtAuthManager);
        jwtFilter.setServerAuthenticationConverter(jwtAuthConverter);
        http
                .authorizeExchange(auth-> {
//                    auth.anyExchange().permitAll(); //evry path
                    auth.pathMatchers("/login").permitAll();
                    auth.anyExchange().authenticated();
                })
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .httpBasic(auth-> auth.disable())
                .formLogin(auth-> auth.disable())
                .csrf(auth-> auth.disable());

        return http.build();
    }

}
