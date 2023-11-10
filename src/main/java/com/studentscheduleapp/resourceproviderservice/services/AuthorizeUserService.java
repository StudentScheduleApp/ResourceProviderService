package com.studentscheduleapp.resourceproviderservice.services;

import com.studentscheduleapp.resourceproviderservice.models.api.AuthorizeUserRequest;
import com.studentscheduleapp.resourceproviderservice.repos.UserAuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorizeUserService {
    @Autowired
    private UserAuthRepository userAuthRepository;

    public boolean authorize(AuthorizeUserRequest request) throws Exception {
        return userAuthRepository.authorize(request);
    }

}
