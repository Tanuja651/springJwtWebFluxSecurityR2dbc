package com.example.springWebFluxJwt.models.reqrespBodies;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReqLogin {
    private String email;
    private String password;
}
