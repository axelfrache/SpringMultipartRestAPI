package io.github.axelfrache.savesyncserver.service;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Response {
    private String message;

    public Response(String message) {
        this.message = message;
    }
}