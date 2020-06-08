package com.ahsanb.auth.tn.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(description = "Response for listing users")
public class ListUserResponse {
    @JsonProperty("users")
    private List<UserInfo> users;
    
    public ListUserResponse(List<UserInfo> users) {
        this.users = users;
    }
}

