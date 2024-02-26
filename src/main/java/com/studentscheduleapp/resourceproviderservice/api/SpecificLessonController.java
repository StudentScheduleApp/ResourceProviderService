package com.studentscheduleapp.resourceproviderservice.api;

import com.studentscheduleapp.resourceproviderservice.models.AuthorizeEntity;
import com.studentscheduleapp.resourceproviderservice.models.AuthorizeType;
import com.studentscheduleapp.resourceproviderservice.models.Entity;
import com.studentscheduleapp.resourceproviderservice.models.SpecificLesson;
import com.studentscheduleapp.resourceproviderservice.models.api.AuthorizeUserRequest;
import com.studentscheduleapp.resourceproviderservice.repos.CustomLessonRepository;
import com.studentscheduleapp.resourceproviderservice.repos.GroupRepository;
import com.studentscheduleapp.resourceproviderservice.repos.SpecificLessonRepository;
import com.studentscheduleapp.resourceproviderservice.services.AuthorizeUserService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class SpecificLessonController {

    @Autowired
    private SpecificLessonRepository specificLessonRepository;
    @Autowired
    private CustomLessonRepository customLessonRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private AuthorizeUserService authorizeUserService;
    private static final Logger log = LogManager.getLogger(SpecificLessonController.class);

    @GetMapping("${mapping.specificLesson.getById}/{ids}")
    public ResponseEntity<List<SpecificLesson>> getById(@PathVariable("ids") String id, @RequestHeader("User-Token") String token) {
        if(token == null || token.isEmpty()) {
            log.info("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<Long> ids = new ArrayList<>();
        try {
            for (int i = 0; i < id.split(",").length; i++) {
                ids.add(Long.parseLong(id.split(",")[i]));
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("${mapping.specificLesson.getByGroupId}/{id}")
    public ResponseEntity<List<SpecificLesson>> getByGroupId(@PathVariable("id") long id, @RequestHeader("User-Token") String token){
        if(token == null || token.isEmpty()) {
            log.info("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<SpecificLesson> cs = new ArrayList<>();
        ArrayList<Long> ids = new ArrayList<>();
        try {
            cs = (ArrayList<SpecificLesson>) specificLessonRepository.getByGroupId(id);
            for (SpecificLesson c : cs) {
                ids.add(c.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("${mapping.specificLesson.create}")
    public ResponseEntity<SpecificLesson> create(@RequestBody SpecificLesson data, @RequestHeader("User-Token") String token){
        if(token == null || token.isEmpty()) {
            log.info("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getComment() != null && data.getComment().length() > 255) {
            log.info("bad request: comment length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.CREATE, Collections.singletonList(data.getGroupId()), Entity.SPECIFIC_LESSON, null)))){
                if(groupRepository.getById(data.getGroupId()) != null) {
                    log.info("bad request: group not exist");
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
                if(customLessonRepository.getById(data.getLessonId()) != null) {
                    log.info("bad request: user not exist");
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
                data.setId(0);
                return ResponseEntity.ok(specificLessonRepository.save(data));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PatchMapping("${mapping.specificLesson.patch}")
    public ResponseEntity<SpecificLesson> patch(@RequestBody SpecificLesson data, @RequestHeader("User-Token") String token){
        if(token == null || token.isEmpty()) {
            log.info("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getComment() != null && data.getComment().length() > 255) {
            log.info("bad request: comment length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
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
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.PATCH, Collections.singletonList(data.getId()), Entity.SPECIFIC_LESSON, ps)))){
                return ResponseEntity.ok(specificLessonRepository.save(data));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("${mapping.specificLesson.delete}/{ids}")
    public ResponseEntity<Void> deleteById(@PathVariable("ids") String id, @RequestHeader("User-Token") String token){
        if(token == null || token.isEmpty()) {
            log.info("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<Long> ids = new ArrayList<>();
        try {
            for (int i = 0; i < id.split(",").length; i++) {
                ids.add(Long.parseLong(id.split(",")[i]));
            }
        } catch (Exception e){
            e.printStackTrace();
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
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
