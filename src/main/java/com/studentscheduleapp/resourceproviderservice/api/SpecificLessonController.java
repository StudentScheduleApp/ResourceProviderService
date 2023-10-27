package com.studentscheduleapp.resourceproviderservice.api;

import com.studentscheduleapp.resourceproviderservice.repos.SpecificLessonRepository;
import com.studentscheduleapp.resourceproviderservice.models.CustomLesson;
import com.studentscheduleapp.resourceproviderservice.models.SpecificLesson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/specificLessons")
public class SpecificLessonController {

    @Autowired
    private SpecificLessonRepository specificLessonRepository;

    @GetMapping("id/{id}")
    public ResponseEntity<SpecificLesson> getById(@PathVariable("id") long id){
        return ResponseEntity.ok(specificLessonRepository.findById(id).orElse(null));
    }
    @GetMapping("group/{id}")
    public ResponseEntity<List<SpecificLesson>> getByGroupId(@PathVariable("id") long id){
        return ResponseEntity.ok(specificLessonRepository.findByGroupId(id));
    }
    @PostMapping("save")
    public ResponseEntity<SpecificLesson> save(@RequestBody SpecificLesson data){
        return ResponseEntity.ok(specificLessonRepository.save(data));
    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<List<CustomLesson>> deleteById(@PathVariable("id") long id){
        specificLessonRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
