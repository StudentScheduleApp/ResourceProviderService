package com.studentscheduleapp.resourceproviderservice.repos;

import com.studentscheduleapp.resourceproviderservice.models.Outline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Repository
public class OutlineRepository {



    @Value("${ip.databaseservice}")
    private String databaseService;

    @Autowired
    private RestTemplate restTemplate;

    public Outline getById(long id) throws Exception {
        ResponseEntity<Outline> r = restTemplate.getForEntity(databaseService + "/api/outlines/id/" + id, Outline.class);
        if(r.getStatusCode().is2xxSuccessful())
            return r.getBody();
        if(r.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return null;
        throw new Exception("request to " + databaseService + " return code " + r.getStatusCode());
    }
    public List<Outline> getBySpecificLessonId(long id) throws Exception {
        ResponseEntity<List> r = restTemplate.getForEntity(databaseService + "/api/outlines/specificLesson/" + id, List.class);
        if(r.getStatusCode().is2xxSuccessful())
            return r.getBody();
        if(r.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return null;
        throw new Exception("request to " + databaseService + " return code " + r.getStatusCode());
    }
    public List<Outline> getByUserId(long id) throws Exception {
        ResponseEntity<List> r = restTemplate.getForEntity(databaseService + "/api/outlines/user/" + id, List.class);
        if(r.getStatusCode().is2xxSuccessful())
            return r.getBody();
        if(r.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return null;
        throw new Exception("request to " + databaseService + " return code " + r.getStatusCode());
    }
    public Outline save(Outline member) throws Exception {
        ResponseEntity<Outline> r = restTemplate.postForEntity(databaseService + "/api/outlines/save", member, Outline.class);
        if(r.getStatusCode().is2xxSuccessful())
            return r.getBody();
        if (r.getStatusCode().equals(HttpStatus.CONFLICT))
            return null;
        throw new Exception("request to " + databaseService + " return code " + r.getStatusCode());
    }
    public boolean delete(long id) throws Exception {
        ResponseEntity<Void> r = restTemplate.getForEntity(databaseService + "/api/outlines/delete/" + id, Void.class);
        if(r.getStatusCode().is2xxSuccessful())
            return true;
        throw new Exception("request to " + databaseService + " return code " + r.getStatusCode());
    }
}
