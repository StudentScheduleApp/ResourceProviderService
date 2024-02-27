package com.studentscheduleapp.resourceproviderservice.api;

import com.studentscheduleapp.resourceproviderservice.models.AuthorizeEntity;
import com.studentscheduleapp.resourceproviderservice.models.AuthorizeType;
import com.studentscheduleapp.resourceproviderservice.models.CustomLesson;
import com.studentscheduleapp.resourceproviderservice.models.Entity;
import com.studentscheduleapp.resourceproviderservice.models.api.AuthorizeUserRequest;
import com.studentscheduleapp.resourceproviderservice.repos.CustomLessonRepository;
import com.studentscheduleapp.resourceproviderservice.repos.GroupRepository;
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
public class CustomLessonController {

    private static final Logger log = LogManager.getLogger(CustomLessonController.class);
    @Autowired
    private CustomLessonRepository customLessonRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private AuthorizeUserService authorizeUserService;

    @GetMapping("${mapping.customLesson.getById}/{ids}")
    public ResponseEntity<List<CustomLesson>> getById(@PathVariable("ids") String id, @RequestHeader("User-Token") String token) {
        if (token == null || token.isEmpty()) {
            log.warn("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<Long> ids = new ArrayList<>();
        try {
            for (int i = 0; i < id.split(",").length; i++) {
                ids.add(Long.parseLong(id.split(",")[i]));
            }
        } catch (Exception e) {
            log.warn("bad request: cant parse customLesson ids: " + id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<String> ps = new ArrayList<>();
        ps.add("id");
        ps.add("groupId");
        ps.add("name");
        ps.add("teacher");
        try {
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.GET, ids, Entity.CUSTOM_LESSON, ps)))) {
                ArrayList<CustomLesson> ls = new ArrayList<>();
                for (Long l : ids) {
                    ls.add(customLessonRepository.getById(l));
                }
                log.info("get customLesson with ids: " + id + " success");
                return ResponseEntity.ok(ls);
            }
            log.warn("get customLesson with ids: " + id + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("get customLesson failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("${mapping.customLesson.getByGroupId}/{id}")
    public ResponseEntity<List<CustomLesson>> getByGroupId(@PathVariable("id") long id, @RequestHeader("User-Token") String token, @RequestParam("params") String params) {
        if (token == null || token.isEmpty()) {
            log.warn("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        ArrayList<CustomLesson> cs;
        ArrayList<Long> ids = new ArrayList<>();
        try {
            cs = (ArrayList<CustomLesson>) customLessonRepository.getByGroupId(id);
            if (cs == null)
                cs = new ArrayList<>();
            for (CustomLesson c : cs) {
                ids.add(c.getId());
            }
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("get customLesson with groutId " + id + " failed: " + errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<String> ps = new ArrayList<>();
        ps.add("id");
        ps.add("groupId");
        ps.add("name");
        ps.add("teacher");
        try {
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.GET, ids, Entity.CUSTOM_LESSON, ps)))) {
                log.info("get customLesson with groutId: " + id + " success");
                return ResponseEntity.ok(cs);
            }
            log.warn("get customLesson with groutId: " + id + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("get customLesson with groutId " + id + " failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("${mapping.customLesson.create}")
    public ResponseEntity<CustomLesson> create(@RequestBody CustomLesson data, @RequestHeader("User-Token") String token) {
        if (token == null || token.isEmpty()) {
            log.warn("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (data.getName() == null || data.getName().isEmpty()) {
            log.warn("bad request: customLesson name is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (data.getName().length() > 255) {
            log.warn("bad request: customLesson name length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (data.getTeacher() != null && data.getTeacher().length() > 255) {
            log.warn("bad request: customLesson teacher length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            if (groupRepository.getById(data.getGroupId()) == null) {
                log.warn("bad request: customLesson group not exist");
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.CREATE, Collections.singletonList(data.getGroupId()), Entity.CUSTOM_LESSON, null)))) {
                data.setId(0);
                CustomLesson c = customLessonRepository.save(data);
                log.info("create customLesson with groupId: " + data.getGroupId() + " success");
                return ResponseEntity.ok(c);
            }
            log.warn("create customLesson with groupId: " + data.getGroupId() + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("create customLesson failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("${mapping.customLesson.patch}")
    public ResponseEntity<CustomLesson> patch(@RequestBody CustomLesson data, @RequestHeader("User-Token") String token, @RequestParam("params") String params) {
        if (token == null || token.isEmpty()) {
            log.warn("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        List<String> ps = Arrays.asList(params.split(","));
        if ((data.getName() == null || data.getName().isEmpty()) && ps.contains("name")) {
            log.warn("bad request: customLesson name is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (data.getName() != null && data.getName().length() > 255 && ps.contains("name")) {
            log.warn("bad request: customLesson name length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (data.getTeacher() != null && data.getTeacher().length() > 255 && ps.contains("teacher")) {
            log.warn("bad request: customLesson teacher length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            CustomLesson u = customLessonRepository.getById(data.getId());
            if (u == null) {
                log.warn("patch customLesson with id: " + data.getId() + " failed: entity not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            if (ps.contains("name"))
                u.setName(data.getName());
            if (ps.contains("teacher"))
                u.setTeacher(data.getTeacher());
            if (ps.contains("groupId"))
                u.setGroupId(data.getGroupId());
            if (groupRepository.getById(data.getGroupId()) == null && ps.contains("groupId")) {
                log.warn("bad request: customLesson group not exist");
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.PATCH, Collections.singletonList(data.getId()), Entity.CUSTOM_LESSON, ps)))) {

                CustomLesson c = customLessonRepository.save(u);
                log.info("patch customLesson with id " + data.getId() + " success");
                return ResponseEntity.ok(c);
            }
            log.warn("patch customLesson with id: " + data.getId() + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("patch customLesson with id " + data.getId() + " failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("${mapping.customLesson.delete}/{ids}")
    public ResponseEntity<Void> deleteById(@PathVariable("ids") String id, @RequestHeader("User-Token") String token) {
        if (token == null || token.isEmpty()) {
            log.warn("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<Long> ids = new ArrayList<>();
        try {
            for (int i = 0; i < id.split(",").length; i++) {
                ids.add(Long.parseLong(id.split(",")[i]));
            }
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.warn("bad request: cant parse customLesson ids: " + id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.DELETE, ids, Entity.CUSTOM_LESSON, null)))) {
                for (Long l : ids) {
                    customLessonRepository.delete(l);
                }
                log.info("delete customLesson with ids: " + id + " success");
                return ResponseEntity.ok().build();
            }
            log.warn("delete customLesson with ids: " + id + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("delete customLesson with ids: " + id + " failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
