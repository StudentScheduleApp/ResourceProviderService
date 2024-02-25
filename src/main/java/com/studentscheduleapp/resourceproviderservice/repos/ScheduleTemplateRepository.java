package com.studentscheduleapp.resourceproviderservice.repos;

import com.studentscheduleapp.resourceproviderservice.models.Outline;
import com.studentscheduleapp.resourceproviderservice.models.ScheduleTemplate;
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
public class ScheduleTemplateRepository {

    @Autowired
    private DatabaseServiceProperties databaseServiceProperties;

    @Autowired
    private RestTemplate restTemplate;

    public ScheduleTemplate getById(long id) throws Exception {
        ResponseEntity<ScheduleTemplate> r = restTemplate.getForEntity(databaseServiceProperties.getUri() + databaseServiceProperties.getGetScheduleTemplateByIdPath() + "/" + id, ScheduleTemplate.class);
        if(r.getStatusCode().is2xxSuccessful())
            return r.getBody();
        throw new Exception("request to " + databaseServiceProperties.getUri() + " return code " + r.getStatusCode());
    }
    public List<ScheduleTemplate> getByGroupId(long id) throws Exception {
        ResponseEntity<ScheduleTemplate[]> r = restTemplate.getForEntity(databaseServiceProperties.getUri() + databaseServiceProperties.getGetScheduleTemplateByGroupIdPath() + "/" + id, ScheduleTemplate[].class);
        if(r.getStatusCode().is2xxSuccessful())
            return new ArrayList<>(Arrays.asList(r.getBody()));
        throw new Exception("request to " + databaseServiceProperties.getUri() + " return code " + r.getStatusCode());
    }
    public ScheduleTemplate save(ScheduleTemplate customLesson) throws Exception {
        ResponseEntity<ScheduleTemplate> r = restTemplate.postForEntity(databaseServiceProperties.getUri() + databaseServiceProperties.getSaveScheduleTemplatePath(), customLesson, ScheduleTemplate.class);
        if(r.getStatusCode().is2xxSuccessful())
            return r.getBody();
        throw new Exception("request to " + databaseServiceProperties.getUri() + " return code " + r.getStatusCode());
    }
    public boolean delete(long id) throws Exception {
        ResponseEntity<Void> r = restTemplate.exchange(databaseServiceProperties.getUri() + databaseServiceProperties.getDeleteScheduleTemplatePath() + "/" + id, HttpMethod.DELETE, null, Void.class);
        if(r.getStatusCode().is2xxSuccessful())
            return true;
        throw new Exception("request to " + databaseServiceProperties.getUri() + " return code " + r.getStatusCode());
    }
}
