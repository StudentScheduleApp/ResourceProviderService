package com.studentscheduleapp.resourceproviderservice.repos;

import com.studentscheduleapp.resourceproviderservice.models.User;
import com.studentscheduleapp.resourceproviderservice.properties.services.ImageServiceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Repository
public class ImageRepository {



    @Autowired
    private ImageServiceProperties imageServiceProperties;

    @Autowired
    private RestTemplate restTemplate;

    public String upload(MultipartFile file) throws Exception {
        ResponseEntity<String> r = restTemplate.postForEntity(imageServiceProperties.getUri() + imageServiceProperties.getGetUploadPath(), file, String.class);
        if(r.getStatusCode().is2xxSuccessful())
            return r.getBody();
        if (r.getStatusCode().equals(HttpStatus.BAD_REQUEST))
            return null;
        throw new Exception("request to " + imageServiceProperties.getUri() + " return code " + r.getStatusCode());
    }
    public boolean delete(String id) throws Exception {
        ResponseEntity<Void> r = restTemplate.exchange(imageServiceProperties.getUri() + imageServiceProperties.getGetDeletePath() + "/" + id, HttpMethod.DELETE, null, Void.class);
        if(r.getStatusCode().is2xxSuccessful())
            return true;
        throw new Exception("request to " + imageServiceProperties.getUri() + " return code " + r.getStatusCode());
    }
}
