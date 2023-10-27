package com.studentscheduleapp.resourceproviderservice.services;

import com.studentscheduleapp.resourceproviderservice.repos.ServiceAuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorizeServiceService {
    @Autowired
    private ServiceAuthRepository serviceAuthRepository;

    public boolean authorize(String token) throws Exception {
        return serviceAuthRepository.authorize(token);
    }

}
