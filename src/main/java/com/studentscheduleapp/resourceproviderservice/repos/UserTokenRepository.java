package com.studentscheduleapp.resourceproviderservice.repos;

import com.studentscheduleapp.resourceproviderservice.models.api.AuthorizeServiceRequest;
import com.studentscheduleapp.resourceproviderservice.models.api.AuthorizeUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
public class UserTokenRepository {

    @Value("${ip.identityservice}")
    private String identityService;

    @Autowired
    private RestTemplate restTemplate;

    public boolean authorize(AuthorizeUserRequest request) throws Exception{
        ResponseEntity<Void> r = restTemplate.postForEntity(identityService + "/api/user/authorize", request, Void.class);
        if(r.getStatusCode().is2xxSuccessful())
            return true;
        if(r.getStatusCode().equals(HttpStatus.UNAUTHORIZED))
            return false;
        throw new Exception("request to " + identityService + " return code " + r.getStatusCode());
    }
}
