package com.ahsanb.auth.tenant.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "Class representing an Error")
public class ErrorMessage {
    @JsonProperty
    @NotNull
    private String message;

    public ErrorMessage(@NotNull String message) {
        super();
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}

