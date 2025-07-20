package com.skillhub.backend.demo.auth;

//import com.skillhub.backend.model.Role;
import lombok.Data;

import com.skillhub.backend.demo.model.Role;


@Data
public class SignupRequest {
    private String name;
    private String email;
    private String password;
    private  Role role;
    private String whatsapp;
    private String instagram;
    private String profilePic;
    private String gender;


}


