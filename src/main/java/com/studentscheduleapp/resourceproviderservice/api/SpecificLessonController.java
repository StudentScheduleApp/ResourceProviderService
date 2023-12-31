package com.studentscheduleapp.resourceproviderservice.api;

import com.studentscheduleapp.resourceproviderservice.models.*;
import com.studentscheduleapp.resourceproviderservice.models.api.AuthorizeUserRequest;
import com.studentscheduleapp.resourceproviderservice.repos.SpecificLessonRepository;
import com.studentscheduleapp.resourceproviderservice.services.AuthorizeUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("api/specificLessons")
public class SpecificLessonController {

    @Autowired
    private SpecificLessonRepository specificLessonRepository;
    @Autowired
    private AuthorizeUserService authorizeUserService;

    @GetMapping("id/{ids}")
    public ResponseEntity<List<SpecificLesson>> getById(@PathVariable("ids") String id, @RequestHeader("User-Token") String token) {
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
        ps.add("groupId");
        ps.add("lessonId");
        ps.add("time");
        ps.add("canceled");
        ps.add("comment");
        try {
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.GET, ids, Entity.SPECIFIC_LESSON, ps)))) {
                ArrayList<SpecificLesson> ls = new ArrayList<>();
                for (Long l : ids) {
                    ls.add(specificLessonRepository.getById(l));
                }
                return ResponseEntity.ok(ls);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("group/{id}")
    public ResponseEntity<List<SpecificLesson>> getByGroupId(@PathVariable("id") long id, @RequestHeader("User-Token") String token){
        ArrayList<SpecificLesson> cs = new ArrayList<>();
        ArrayList<Long> ids = new ArrayList<>();
        try {
            cs = (ArrayList<SpecificLesson>) specificLessonRepository.getByGroupId(id);
            for (SpecificLesson c : cs) {
                ids.add(c.getId());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<String> ps = new ArrayList<>();
        ps.add("id");
        ps.add("groupId");
        ps.add("lessonId");
        ps.add("time");
        ps.add("canceled");
        ps.add("comment");
        try {
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.GET, ids, Entity.MEMBER, ps)))){
                return ResponseEntity.ok(cs);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("create")
    public ResponseEntity<SpecificLesson> create(@RequestBody SpecificLesson data, @RequestHeader("User-Token") String token){
        try {
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.CREATE, Collections.singletonList(data.getGroupId()), Entity.SPECIFIC_LESSON, null)))){
                return ResponseEntity.ok(specificLessonRepository.save(data));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PatchMapping("patch")
    public ResponseEntity<SpecificLesson> patch(@RequestBody SpecificLesson data, @RequestHeader("User-Token") String token){
        try {
            SpecificLesson u = specificLessonRepository.getById(data.getId());
            if (u == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            ArrayList<String> ps = new ArrayList<>();
            if (data.getGroupId() != u.getGroupId())
                ps.add("groupId");
            if (data.getLessonId() != u.getLessonId())
                ps.add("lessonId");
            if (data.getTime() != u.getTime())
                ps.add("time");
            if (data.getComment().equals(u.getComment()))
                ps.add("comment");
            if (data.getCanceled().equals(u.getCanceled()))
                ps.add("canceled");
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.PATCH, Collections.singletonList(data.getId()), Entity.SPECIFIC_LESSON, null)))){
                return ResponseEntity.ok(specificLessonRepository.save(data));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("delete/{ids}")
    public ResponseEntity<Void> deleteById(@PathVariable("ids") String id, @RequestHeader("User-Token") String token){
        ArrayList<Long> ids = new ArrayList<>();
        try {
            for (int i = 0; i < id.split(",").length; i++) {
                ids.add(Long.parseLong(id.split(",")[i]));
            }
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.DELETE, ids, Entity.SPECIFIC_LESSON, null)))){
                for (Long l : ids) {
                    specificLessonRepository.delete(l);
                }
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
