package com.studentscheduleapp.resourceproviderservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Boolean banned;
    private String avaUrl;
    private List<Role> roles;

}
