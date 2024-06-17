package com.example.springWebFluxJwt.models;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.*;


@Table(name = "users")
@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private Long id;
    private String username;
    private String password;
//    private String roles; // You might want to have a separate entity and relationship for roles

    // getters and setters
}