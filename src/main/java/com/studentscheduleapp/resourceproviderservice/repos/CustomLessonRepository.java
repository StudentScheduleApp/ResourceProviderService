package com.studentscheduleapp.resourceproviderservice.repos;

import com.studentscheduleapp.resourceproviderservice.models.CustomLesson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Repository
public class CustomLessonRepository {



    @Value("${ip.databaseservice}")
    private String databaseService;

    @Autowired
    private RestTemplate restTemplate;

    public CustomLesson getById(long id) throws Exception {
        ResponseEntity<CustomLesson> r = restTemplate.getForEntity(databaseService + "/api/customLessons/id/" + id, CustomLesson.class);
        if(r.getStatusCode().is2xxSuccessful())
            return r.getBody();
        if(r.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return null;
        throw new Exception("request to " + databaseService + " return code " + r.getStatusCode());
    }
    public List<CustomLesson> getByGroupId(long id) throws Exception {
        ResponseEntity<List> r = restTemplate.getForEntity(databaseService + "/api/customLessons/group/" + id, List.class);
        if(r.getStatusCode().is2xxSuccessful())
            return r.getBody();
        if(r.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return null;
        throw new Exception("request to " + databaseService + " return code " + r.getStatusCode());
    }
    public CustomLesson save(CustomLesson customLesson) throws Exception {
        ResponseEntity<CustomLesson> r = restTemplate.postForEntity(databaseService + "/api/customLessons/save", customLesson, CustomLesson.class);
        if(r.getStatusCode().is2xxSuccessful())
            return r.getBody();
        if (r.getStatusCode().equals(HttpStatus.CONFLICT))
            return null;
        throw new Exception("request to " + databaseService + " return code " + r.getStatusCode());
    }
    public boolean delete(long id) throws Exception {
        ResponseEntity<Void> r = restTemplate.getForEntity(databaseService + "/api/customLessons/delete/" + id, Void.class);
        if(r.getStatusCode().is2xxSuccessful())
            return true;
        throw new Exception("request to " + databaseService + " return code " + r.getStatusCode());
    }
}
