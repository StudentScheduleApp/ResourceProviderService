package com.studentscheduleapp.resourceproviderservice.repos;

import com.studentscheduleapp.resourceproviderservice.models.api.AuthorizeUserRequest;
import com.studentscheduleapp.resourceproviderservice.properties.services.IdentityServiceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
public class UserAuthRepository {

    @Autowired
    private IdentityServiceProperties identityServiceProperties;

    @Autowired
    private RestTemplate restTemplate;

    public boolean authorize(AuthorizeUserRequest request) throws Exception{
        ResponseEntity<Boolean> r = restTemplate.postForEntity(identityServiceProperties.getUri() + identityServiceProperties.getGetAuthorizePath(), request, Boolean.class);
        if(r.getStatusCode().is2xxSuccessful())
            return r.getBody() != null && r.getBody();
        throw new Exception("request to " + identityServiceProperties.getUri() + " return code " + r.getStatusCode());
    }
    public long getUserIdByToken(String token) throws Exception{
        ResponseEntity<Long> r = restTemplate.postForEntity(identityServiceProperties.getUri() + identityServiceProperties.getGetUserIdByTokenPath(), token, Long.class);
        if(r.getStatusCode().is2xxSuccessful())
            return r.getBody() == null ? 0L : r.getBody();
        throw new Exception("request to " + identityServiceProperties.getUri() + " return code " + r.getStatusCode());
    }
}
