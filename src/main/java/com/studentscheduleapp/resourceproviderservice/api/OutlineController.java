package com.studentscheduleapp.resourceproviderservice.api;

import com.studentscheduleapp.resourceproviderservice.models.*;
import com.studentscheduleapp.resourceproviderservice.models.api.AuthorizeUserRequest;
import com.studentscheduleapp.resourceproviderservice.repos.OutlineRepository;
import com.studentscheduleapp.resourceproviderservice.services.AuthorizeUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("api/outlines")
public class OutlineController {

    @Autowired
    private OutlineRepository outlineRepository;
    @Autowired
    private AuthorizeUserService authorizeUserService;

    @GetMapping("id/{ids}")
    public ResponseEntity<List<Outline>> getById(@PathVariable("ids") String id, @RequestHeader("User-Token") String token) {
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
        ps.add("userId");
        ps.add("specificLessonId");
        try {
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.GET, ids, Entity.OUTLINE, ps)))) {
                ArrayList<Outline> ls = new ArrayList<>();
                for (Long l : ids) {
                    ls.add(outlineRepository.getById(l));
                }
                return ResponseEntity.ok(ls);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("user/{id}")
    public ResponseEntity<List<Outline>> getBySpecificLessonId(@PathVariable("id") long id, @RequestHeader("User-Token") String token){
        ArrayList<Outline> cs = new ArrayList<>();
        ArrayList<Long> ids = new ArrayList<>();
        try {
            cs = (ArrayList<Outline>) outlineRepository.getBySpecificLessonId(id);
            for (Outline c : cs) {
                ids.add(c.getId());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<String> ps = new ArrayList<>();
        ps.add("id");
        ps.add("userId");
        ps.add("specificLessonId");
        try {
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.GET, ids, Entity.OUTLINE, ps)))){
                return ResponseEntity.ok(cs);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("user/{id}")
    public ResponseEntity<List<Outline>> getByUserId(@PathVariable("id") long id, @RequestHeader("User-Token") String token){
        ArrayList<Outline> cs = new ArrayList<>();
        ArrayList<Long> ids = new ArrayList<>();
        try {
            cs = (ArrayList<Outline>) outlineRepository.getByUserId(id);
            for (Outline c : cs) {
                ids.add(c.getId());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<String> ps = new ArrayList<>();
        ps.add("id");
        ps.add("userId");
        ps.add("specificLessonId");
        try {
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.GET, ids, Entity.OUTLINE, ps)))){
                return ResponseEntity.ok(cs);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("create")
    public ResponseEntity<Outline> create(@RequestBody Outline data, @RequestHeader("User-Token") String token){
        try {
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.CREATE, Collections.singletonList(data.getSpecificLessonId()), Entity.LESSON_TEMPLATE, null)))){
                return ResponseEntity.ok(outlineRepository.save(data));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PatchMapping("patch")
    public ResponseEntity<Outline> patch(@RequestBody Outline data, @RequestHeader("User-Token") String token){
        try {
            Outline cl = outlineRepository.getById(data.getId());
            if (cl == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            data.setUserId(cl.getUserId());
            data.setSpecificLessonId(cl.getSpecificLessonId());
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.PATCH, Collections.singletonList(data.getId()), Entity.LESSON_TEMPLATE, null)))){
                return ResponseEntity.ok(outlineRepository.save(data));
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
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.DELETE, ids, Entity.OUTLINE, null)))){
                for (Long l : ids) {
                    outlineRepository.delete(l);
                }
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
