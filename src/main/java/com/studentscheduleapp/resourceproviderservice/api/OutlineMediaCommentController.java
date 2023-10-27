package com.studentscheduleapp.resourceproviderservice.api;

import com.studentscheduleapp.resourceproviderservice.repos.OutlineMediaCommentRepository;
import com.studentscheduleapp.resourceproviderservice.models.CustomLesson;
import com.studentscheduleapp.resourceproviderservice.models.OutlineMediaComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/outlineMediaComments")
public class OutlineMediaCommentController {

    @Autowired
    private OutlineMediaCommentRepository outlineMediaCommentRepository;

    @GetMapping("id/{id}")
    public ResponseEntity<OutlineMediaComment> getById(@PathVariable("id") long id){
        return ResponseEntity.ok(outlineMediaCommentRepository.findById(id).orElse(null));
    }
    @GetMapping("outlineMedia/{id}")
    public ResponseEntity<List<OutlineMediaComment>> getByOutlineMediaId(@PathVariable("id") long id){
        return ResponseEntity.ok(outlineMediaCommentRepository.findByMediaId(id));
    }
    @PostMapping("save")
    public ResponseEntity<OutlineMediaComment> save(@RequestBody OutlineMediaComment data){
        return ResponseEntity.ok(outlineMediaCommentRepository.save(data));
    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<List<CustomLesson>> deleteById(@PathVariable("id") long id){
        outlineMediaCommentRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
