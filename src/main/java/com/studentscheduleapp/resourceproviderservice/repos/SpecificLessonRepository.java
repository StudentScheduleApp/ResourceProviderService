package com.studentscheduleapp.resourceproviderservice.repos;

import com.studentscheduleapp.resourceproviderservice.models.SpecificLesson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Repository
public class SpecificLessonRepository {



    @Value("${ip.databaseservice}")
    private String databaseService;

    @Autowired
    private RestTemplate restTemplate;

    public SpecificLesson getById(long id) throws Exception {
        ResponseEntity<SpecificLesson> r = restTemplate.getForEntity(databaseService + "/api/specificLesson/id/" + id, SpecificLesson.class);
        if(r.getStatusCode().is2xxSuccessful())
            return r.getBody();
        if(r.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return null;
        throw new Exception("request to " + databaseService + " return code " + r.getStatusCode());
    }
    public List<SpecificLesson> getByGroupId(long id) throws Exception {
        ResponseEntity<List> r = restTemplate.getForEntity(databaseService + "/api/specificLesson/outlineMedia/" + id, List.class);
        if(r.getStatusCode().is2xxSuccessful())
            return r.getBody();
        if(r.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return null;
        throw new Exception("request to " + databaseService + " return code " + r.getStatusCode());
    }
    public SpecificLesson save(SpecificLesson customLesson) throws Exception {
        ResponseEntity<SpecificLesson> r = restTemplate.postForEntity(databaseService + "/api/specificLesson/save", customLesson, SpecificLesson.class);
        if(r.getStatusCode().is2xxSuccessful())
            return r.getBody();
        if (r.getStatusCode().equals(HttpStatus.CONFLICT))
            return null;
        throw new Exception("request to " + databaseService + " return code " + r.getStatusCode());
    }
    public boolean delete(long id) throws Exception {
        ResponseEntity<Void> r = restTemplate.getForEntity(databaseService + "/api/specificLesson/delete/" + id, Void.class);
        if(r.getStatusCode().is2xxSuccessful())
            return true;
        throw new Exception("request to " + databaseService + " return code " + r.getStatusCode());
    }
}
