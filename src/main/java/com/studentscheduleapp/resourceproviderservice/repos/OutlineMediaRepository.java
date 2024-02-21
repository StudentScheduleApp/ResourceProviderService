package com.studentscheduleapp.resourceproviderservice.repos;

import com.studentscheduleapp.resourceproviderservice.models.OutlineMedia;
import com.studentscheduleapp.resourceproviderservice.properties.services.DatabaseServiceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class OutlineMediaRepository {



    @Autowired
    private DatabaseServiceProperties databaseServiceProperties;

    @Autowired
    private RestTemplate restTemplate;

    public OutlineMedia getById(long id) throws Exception {
        ResponseEntity<OutlineMedia> r = restTemplate.getForEntity(databaseServiceProperties.getUri() + databaseServiceProperties.getGetOutlineMediaByIdPath() + "/" + id, OutlineMedia.class);
        if(r.getStatusCode().is2xxSuccessful())
            return r.getBody();
        if(r.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return null;
        throw new Exception("request to " + databaseServiceProperties.getUri() + " return code " + r.getStatusCode());
    }
    public List<OutlineMedia> getByOutlineId(long id) throws Exception {
        ResponseEntity<OutlineMedia[]> r = restTemplate.getForEntity(databaseServiceProperties.getUri() + databaseServiceProperties.getGetOutlineMediaByOutlineIdPath() + "/" + id, OutlineMedia[].class);
        if(r.getStatusCode().is2xxSuccessful())
            return new ArrayList<>(Arrays.asList(r.getBody()));
        if(r.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return null;
        throw new Exception("request to " + databaseServiceProperties.getUri() + " return code " + r.getStatusCode());
    }
    public OutlineMedia save(OutlineMedia customLesson) throws Exception {
        ResponseEntity<OutlineMedia> r = restTemplate.postForEntity(databaseServiceProperties.getUri() + databaseServiceProperties.getSaveOutlineMediaPath(), customLesson, OutlineMedia.class);
        if(r.getStatusCode().is2xxSuccessful())
            return r.getBody();
        if (r.getStatusCode().equals(HttpStatus.CONFLICT))
            return null;
        throw new Exception("request to " + databaseServiceProperties.getUri() + " return code " + r.getStatusCode());
    }
    public boolean delete(long id) throws Exception {
        ResponseEntity<Void> r = restTemplate.exchange(databaseServiceProperties.getUri() + databaseServiceProperties.getDeleteOutlineMediaPath() + "/" + id, HttpMethod.DELETE, null, Void.class);
        if(r.getStatusCode().is2xxSuccessful())
            return true;
        throw new Exception("request to " + databaseServiceProperties.getUri() + " return code " + r.getStatusCode());
    }
}
