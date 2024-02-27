package com.studentscheduleapp.resourceproviderservice.repos;

import com.studentscheduleapp.resourceproviderservice.models.Outline;
import com.studentscheduleapp.resourceproviderservice.properties.services.DatabaseServiceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Repository
public class OutlineRepository {


    @Autowired
    private DatabaseServiceProperties databaseServiceProperties;

    @Autowired
    private RestTemplate restTemplate;

    public Outline getById(long id) throws Exception {
        ResponseEntity<Outline> r = restTemplate.getForEntity(databaseServiceProperties.getUri() + databaseServiceProperties.getGetOutlineByIdPath() + "/" + id, Outline.class);
        if (r.getStatusCode().is2xxSuccessful())
            return r.getBody();
        throw new Exception("request to " + databaseServiceProperties.getUri() + " return code " + r.getStatusCode());
    }

    public List<Outline> getBySpecificLessonId(long id) throws Exception {
        ResponseEntity<Outline[]> r = restTemplate.getForEntity(databaseServiceProperties.getUri() + databaseServiceProperties.getGetOutlineBySpecificLessonIdPath() + "/" + id, Outline[].class);
        if (r.getStatusCode().is2xxSuccessful())
            return r.getBody() == null ? null : Arrays.asList(r.getBody());
        throw new Exception("request to " + databaseServiceProperties.getUri() + " return code " + r.getStatusCode());
    }

    public List<Outline> getByUserId(long id) throws Exception {
        ResponseEntity<Outline[]> r = restTemplate.getForEntity(databaseServiceProperties.getUri() + databaseServiceProperties.getGetOutlineByUserIdPath() + "/" + id, Outline[].class);
        if (r.getStatusCode().is2xxSuccessful())
            return r.getBody() == null ? null : Arrays.asList(r.getBody());
        throw new Exception("request to " + databaseServiceProperties.getUri() + " return code " + r.getStatusCode());
    }

    public Outline save(Outline member) throws Exception {
        ResponseEntity<Outline> r = restTemplate.postForEntity(databaseServiceProperties.getUri() + databaseServiceProperties.getSaveOutlinePath(), member, Outline.class);
        if (r.getStatusCode().is2xxSuccessful())
            return r.getBody();
        throw new Exception("request to " + databaseServiceProperties.getUri() + " return code " + r.getStatusCode());
    }

    public void delete(long id) throws Exception {
        ResponseEntity<Void> r = restTemplate.exchange(databaseServiceProperties.getUri() + databaseServiceProperties.getDeleteOutlinePath() + "/" + id, HttpMethod.DELETE, null, Void.class);
        if (!r.getStatusCode().is2xxSuccessful())
            throw new Exception("request to " + databaseServiceProperties.getUri() + " return code " + r.getStatusCode());
    }
}
