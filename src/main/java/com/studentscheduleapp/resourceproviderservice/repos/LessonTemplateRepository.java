package com.studentscheduleapp.resourceproviderservice.repos;

import com.studentscheduleapp.resourceproviderservice.models.LessonTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Repository
public class LessonTemplateRepository {



    @Value("${ip.databaseservice}")
    private String databaseService;

    @Autowired
    private RestTemplate restTemplate;

    public LessonTemplate getById(long id) throws Exception {
        ResponseEntity<LessonTemplate> r = restTemplate.getForEntity(databaseService + "/api/lessonTemplates/id/" + id, LessonTemplate.class);
        if(r.getStatusCode().is2xxSuccessful())
            return r.getBody();
        if(r.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return null;
        throw new Exception("request to " + databaseService + " return code " + r.getStatusCode());
    }
    public List<LessonTemplate> getByScheduleTemplateId(long id) throws Exception {
        ResponseEntity<List> r = restTemplate.getForEntity(databaseService + "/api/lessonTemplates/scheduleTemplate/" + id, List.class);
        if(r.getStatusCode().is2xxSuccessful())
            return r.getBody();
        if(r.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return null;
        throw new Exception("request to " + databaseService + " return code " + r.getStatusCode());
    }
    public LessonTemplate save(LessonTemplate lessonTemplate) throws Exception {
        ResponseEntity<LessonTemplate> r = restTemplate.postForEntity(databaseService + "/api/lessonTemplates/save", lessonTemplate, LessonTemplate.class);
        if(r.getStatusCode().is2xxSuccessful())
            return r.getBody();
        if (r.getStatusCode().equals(HttpStatus.CONFLICT))
            return null;
        throw new Exception("request to " + databaseService + " return code " + r.getStatusCode());
    }
    public boolean delete(long id) throws Exception {
        ResponseEntity<Void> r = restTemplate.getForEntity(databaseService + "/api/lessonTemplates/delete/" + id, Void.class);
        if(r.getStatusCode().is2xxSuccessful())
            return true;
        throw new Exception("request to " + databaseService + " return code " + r.getStatusCode());
    }
}
