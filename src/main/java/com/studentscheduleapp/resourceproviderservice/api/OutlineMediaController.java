package com.studentscheduleapp.resourceproviderservice.api;

import com.studentscheduleapp.resourceproviderservice.repos.OutlineMediaRepository;
import com.studentscheduleapp.resourceproviderservice.models.CustomLesson;
import com.studentscheduleapp.resourceproviderservice.models.OutlineMedia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/outlineMedias")
public class OutlineMediaController {

    @Autowired
    private OutlineMediaRepository outlineMediaRepository;

    @GetMapping("id/{id}")
    public ResponseEntity<OutlineMedia> getById(@PathVariable("id") long id){
        return ResponseEntity.ok(outlineMediaRepository.findById(id).orElse(null));
    }
    @GetMapping("outline/{id}")
    public ResponseEntity<List<OutlineMedia>> getByOutlineId(@PathVariable("id") long id){
        return ResponseEntity.ok(outlineMediaRepository.findByOutlineId(id));
    }
    @PostMapping("save")
    public ResponseEntity<OutlineMedia> save(@RequestBody OutlineMedia data){
        return ResponseEntity.ok(outlineMediaRepository.save(data));
    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<List<CustomLesson>> deleteById(@PathVariable("id") long id){
        outlineMediaRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
