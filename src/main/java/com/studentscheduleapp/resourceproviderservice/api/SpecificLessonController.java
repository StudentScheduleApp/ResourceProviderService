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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
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
            log.warn("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<Long> ids = new ArrayList<>();
        try {
            for (int i = 0; i < id.split(",").length; i++) {
                ids.add(Long.parseLong(id.split(",")[i]));
            }
        } catch (Exception e) {
            log.warn("bad request: cant parse specificLesson ids: " + id);
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
                log.info("get specificLesson with ids: " + id + " success");
                return ResponseEntity.ok(ls);
            }
            log.warn("get specificLesson with ids: " + id + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("get specificLesson failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("${mapping.specificLesson.getByGroupId}/{id}")
    public ResponseEntity<List<SpecificLesson>> getByGroupId(@PathVariable("id") long id, @RequestHeader("User-Token") String token){
        if(token == null || token.isEmpty()) {
            log.info("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<SpecificLesson> cs;
        ArrayList<Long> ids = new ArrayList<>();
        try {
            cs = (ArrayList<SpecificLesson>) specificLessonRepository.getByGroupId(id);
            if (cs == null)
                cs = new ArrayList<>();
            for (SpecificLesson c : cs) {
                ids.add(c.getId());
            }
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("get specificLesson with groupId " + id + " failed: " + errors);
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
                log.info("get specificLesson with groupId: " + id + " success");
                return ResponseEntity.ok(cs);
            }
            log.warn("get specificLesson with groupId: " + id + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("get specificLesson with groupId " + id + " failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("${mapping.specificLesson.create}")
    public ResponseEntity<SpecificLesson> create(@RequestBody SpecificLesson data, @RequestHeader("User-Token") String token){
        if(token == null || token.isEmpty()) {
            log.warn("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getComment() != null && data.getComment().length() > 255) {
            log.warn("bad request: specificLesson comment length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            if(groupRepository.getById(data.getGroupId()) == null) {
                log.warn("bad request: specificLesson group not exist");
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            if(customLessonRepository.getById(data.getLessonId()) == null) {
                log.warn("bad request: specificLesson user not exist");
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.CREATE, Collections.singletonList(data.getGroupId()), Entity.SPECIFIC_LESSON, null)))){
                data.setId(0);
                SpecificLesson sl = specificLessonRepository.save(data);
                log.info("create specificLesson with groupId: " + sl.getGroupId() + " success");
                return ResponseEntity.ok(sl);
            }
            log.warn("create specificLesson with groupId: " + data.getGroupId() + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("create specificLesson failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PatchMapping("${mapping.specificLesson.patch}")
    public ResponseEntity<SpecificLesson> patch(@RequestBody SpecificLesson data, @RequestHeader("User-Token") String token, @RequestParam("params") String params){
        if(token == null || token.isEmpty()) {
            log.warn("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getComment() != null && data.getComment().length() > 255) {
            log.warn("bad request: specificLesson comment length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            SpecificLesson u = specificLessonRepository.getById(data.getId());
            if (u == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            List<String> ps = Arrays.asList(params.split(","));
            if (ps.contains("groupId"))
                u.setGroupId(data.getGroupId());
            if (ps.contains("lessonId"))
                u.setLessonId(data.getLessonId());
            if (ps.contains("time"))
                u.setTime(data.getTime());
            if (ps.contains("canceled"))
                u.setCanceled(data.isCanceled());
            if (ps.contains("comment"))
                u.setComment(data.getComment());
            if(groupRepository.getById(data.getGroupId()) == null && ps.contains("groupId")) {
                log.warn("bad request: specificLesson group not exist");
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            if(customLessonRepository.getById(data.getLessonId()) == null && ps.contains("lessonId")) {
                log.warn("bad request: specificLesson user not exist");
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.PATCH, Collections.singletonList(data.getId()), Entity.SPECIFIC_LESSON, ps)))){
                SpecificLesson sl = specificLessonRepository.save(u);
                log.info("patch specificLesson with id " + sl.getId() + " success");
                return ResponseEntity.ok(sl);
            }
            log.warn("patch specificLesson with id: " + data.getId() + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("patch specificLesson with id " + data.getId() + " failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("${mapping.specificLesson.delete}/{ids}")
    public ResponseEntity<Void> deleteById(@PathVariable("ids") String id, @RequestHeader("User-Token") String token){
        if(token == null || token.isEmpty()) {
            log.warn("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<Long> ids = new ArrayList<>();
        try {
            for (int i = 0; i < id.split(",").length; i++) {
                ids.add(Long.parseLong(id.split(",")[i]));
            }
        } catch (Exception e){
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.warn("bad request: cant parse specificLesson ids: " + id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.DELETE, ids, Entity.SPECIFIC_LESSON, null)))){
                for (Long l : ids) {
                    specificLessonRepository.delete(l);
                }
                log.info("delete specificLesson with ids: " + id + " success");
                return ResponseEntity.ok().build();
            }
            log.warn("delete specificLesson with ids: " + id + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("delete specificLesson with ids: " + id + " failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
