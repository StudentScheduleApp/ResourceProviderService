package com.studentscheduleapp.resourceproviderservice.api;

import com.studentscheduleapp.resourceproviderservice.models.*;
import com.studentscheduleapp.resourceproviderservice.models.api.AuthorizeUserRequest;
import com.studentscheduleapp.resourceproviderservice.repos.LessonTemplateRepository;
import com.studentscheduleapp.resourceproviderservice.services.AuthorizeUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("api/lessonTemplates")
public class LessonTemplateController {

    @Autowired
    private LessonTemplateRepository lessonTemplateRepository;
    @Autowired
    private AuthorizeUserService authorizeUserService;

    @GetMapping("id/{ids}")
    public ResponseEntity<List<LessonTemplate>> getById(@PathVariable("ids") String id, @RequestHeader("User-Token") String token) {
        ArrayList<Long> ids = new ArrayList<>();
        try {
            for (int i = 0; i < id.split(",").length; i++) {
                ids.add(Long.parseLong(id.split(",")[i]));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<String> ps = new ArrayList<>();
        ps.add("id");
        ps.add("scheduleTemplateId");
        ps.add("lessonId");
        ps.add("time");
        ps.add("comment");
        try {
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, Collections.singletonList(new AuthorizeEntity(AuthorizeType.GET, ids, Entity.CUSTOM_LESSON, ps))))) {
                ArrayList<LessonTemplate> ls = new ArrayList<>();
                for (Long l : ids) {
                    ls.add(lessonTemplateRepository.getById(l));
                }
                return ResponseEntity.ok(ls);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
