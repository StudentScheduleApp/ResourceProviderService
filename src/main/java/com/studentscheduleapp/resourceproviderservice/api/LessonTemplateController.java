package com.studentscheduleapp.resourceproviderservice.api;

import com.studentscheduleapp.resourceproviderservice.repos.LessonTemplateRepository;
import com.studentscheduleapp.resourceproviderservice.models.CustomLesson;
import com.studentscheduleapp.resourceproviderservice.models.LessonTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/lessonTemplates")
public class LessonTemplateController {

    @Autowired
    private LessonTemplateRepository lessonTemplateRepository;

    @GetMapping("id/{id}")
    public ResponseEntity<LessonTemplate> getById(@PathVariable("id") long id){
        return ResponseEntity.ok(lessonTemplateRepository.findById(id).orElse(null));
    }
    @GetMapping("scheduleTemplate/{id}")
    public ResponseEntity<List<LessonTemplate>> getByScheduleTemplateId(@PathVariable("id") long id){
        return ResponseEntity.ok(lessonTemplateRepository.findByScheduleTemplateId(id));
    }
    @PostMapping("save")
    public ResponseEntity<LessonTemplate> save(@RequestBody LessonTemplate data){
        return ResponseEntity.ok(lessonTemplateRepository.save(data));
    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<List<CustomLesson>> deleteById(@PathVariable("id") long id){
        lessonTemplateRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
