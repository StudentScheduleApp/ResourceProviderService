package com.studentscheduleapp.resourceproviderservice.api;

import com.studentscheduleapp.resourceproviderservice.repos.ScheduleTemplateRepository;
import com.studentscheduleapp.resourceproviderservice.models.CustomLesson;
import com.studentscheduleapp.resourceproviderservice.models.ScheduleTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/scheduleTemplates")
public class ScheduleTemplateController {

    @Autowired
    private ScheduleTemplateRepository scheduleTemplateRepository;

    @GetMapping("id/{id}")
    public ResponseEntity<ScheduleTemplate> getById(@PathVariable("id") long id){
        return ResponseEntity.ok(scheduleTemplateRepository.findById(id).orElse(null));
    }
    @GetMapping("group/{id}")
    public ResponseEntity<List<ScheduleTemplate>> getByGroupId(@PathVariable("id") long id){
        return ResponseEntity.ok(scheduleTemplateRepository.findByGroupId(id));
    }
    @PostMapping("save")
    public ResponseEntity<ScheduleTemplate> save(@RequestBody ScheduleTemplate data){
        return ResponseEntity.ok(scheduleTemplateRepository.save(data));
    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<List<CustomLesson>> deleteById(@PathVariable("id") long id){
        scheduleTemplateRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
