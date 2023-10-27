package com.studentscheduleapp.resourceproviderservice.api;

import com.studentscheduleapp.resourceproviderservice.repos.OutlineRepository;
import com.studentscheduleapp.resourceproviderservice.models.CustomLesson;
import com.studentscheduleapp.resourceproviderservice.models.Outline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/outlines")
public class OutlineController {

    @Autowired
    private OutlineRepository outlineRepository;

    @GetMapping("id/{id}")
    public ResponseEntity<Outline> getById(@PathVariable("id") long id){
        return ResponseEntity.ok(outlineRepository.findById(id).orElse(null));
    }
    @GetMapping("specificLesson/{id}")
    public ResponseEntity<List<Outline>> getBySpecificLessonId(@PathVariable("id") long id){
        return ResponseEntity.ok(outlineRepository.findBySpecificLessonId(id));
    }
    @GetMapping("user/{id}")
    public ResponseEntity<List<Outline>> getByUserId(@PathVariable("id") long id){
        return ResponseEntity.ok(outlineRepository.findByUserId(id));
    }
    @PostMapping("save")
    public ResponseEntity<Outline> save(@RequestBody Outline data){
        return ResponseEntity.ok(outlineRepository.save(data));
    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<List<CustomLesson>> deleteById(@PathVariable("id") long id){
        outlineRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
