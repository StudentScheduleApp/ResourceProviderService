package com.studentscheduleapp.resourceproviderservice.repos;

import com.studentscheduleapp.resourceproviderservice.models.OutlineMediaComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Repository
public class OutlineMediaCommentRepository {



    @Value("${ip.databaseservice}")
    private String databaseService;

    @Autowired
    private RestTemplate restTemplate;

    public OutlineMediaComment getById(long id) throws Exception {
        ResponseEntity<OutlineMediaComment> r = restTemplate.getForEntity(databaseService + "/api/outlineMediaComment/id/" + id, OutlineMediaComment.class);
        if(r.getStatusCode().is2xxSuccessful())
            return r.getBody();
        if(r.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return null;
        throw new Exception("request to " + databaseService + " return code " + r.getStatusCode());
    }
    public List<OutlineMediaComment> getByOutlineMediaId(long id) throws Exception {
        ResponseEntity<List> r = restTemplate.getForEntity(databaseService + "/api/outlineMediaComment/outlineMedia/" + id, List.class);
        if(r.getStatusCode().is2xxSuccessful())
            return r.getBody();
        if(r.getStatusCode().equals(HttpStatus.NOT_FOUND))
            return null;
        throw new Exception("request to " + databaseService + " return code " + r.getStatusCode());
    }
    public OutlineMediaComment save(OutlineMediaComment customLesson) throws Exception {
        ResponseEntity<OutlineMediaComment> r = restTemplate.postForEntity(databaseService + "/api/outlineMediaComment/save", customLesson, OutlineMediaComment.class);
        if(r.getStatusCode().is2xxSuccessful())
            return r.getBody();
        if (r.getStatusCode().equals(HttpStatus.CONFLICT))
            return null;
        throw new Exception("request to " + databaseService + " return code " + r.getStatusCode());
    }
    public boolean delete(long id) throws Exception {
        ResponseEntity<Void> r = restTemplate.getForEntity(databaseService + "/api/outlineMediaComment/delete/" + id, Void.class);
        if(r.getStatusCode().is2xxSuccessful())
            return true;
        throw new Exception("request to " + databaseService + " return code " + r.getStatusCode());
    }
}
