package com.studentscheduleapp.resourceproviderservice.api;

import com.studentscheduleapp.resourceproviderservice.repos.CustomLessonRepository;
import com.studentscheduleapp.resourceproviderservice.models.CustomLesson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/customLessons")
public class CustomLessonController {

    @Autowired
    private CustomLessonRepository customLessonRepository;

    @GetMapping("id/{id}")
    public ResponseEntity<CustomLesson> getById(@PathVariable("id") long id){
        return ResponseEntity.ok(customLessonRepository.findById(id).orElse(null));
    }
    @GetMapping("group/{id}")
    public ResponseEntity<List<CustomLesson>> getByGroupId(@PathVariable("id") long id){
        return ResponseEntity.ok(customLessonRepository.findByGroupId(id));
    }
    @PostMapping("save")
    public ResponseEntity<CustomLesson> save(@RequestBody CustomLesson data){
        return ResponseEntity.ok(customLessonRepository.save(data));
    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<List<CustomLesson>> deleteById(@PathVariable("id") long id){
        customLessonRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
