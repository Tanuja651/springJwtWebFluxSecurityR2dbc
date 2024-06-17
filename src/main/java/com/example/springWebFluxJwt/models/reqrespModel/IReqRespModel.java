package com.example.springWebFluxJwt.models.reqrespModel;

public interface IReqRespModel<T> {
    T getData(); //Return the data of the response
    String getMessage(); //Return a message(String) if there has been any error eg:on 404 error
}
