package com.studentscheduleapp.resourceproviderservice.api;

import com.studentscheduleapp.resourceproviderservice.models.*;
import com.studentscheduleapp.resourceproviderservice.models.api.AuthorizeUserRequest;
import com.studentscheduleapp.resourceproviderservice.repos.*;
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
public class OutlineController {
    private static final Logger log = LogManager.getLogger(OutlineController.class);
    @Autowired
    private SpecificLessonRepository specificLessonRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OutlineRepository outlineRepository;
    @Autowired
    private OutlineMediaRepository outlineMediaRepository;
    @Autowired
    private OutlineMediaCommentRepository outlineMediaCommentRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private AuthorizeUserService authorizeUserService;

    @GetMapping("${mapping.outline.getById}/{ids}")
    public ResponseEntity<List<Outline>> getById(@PathVariable("ids") String id, @RequestHeader("User-Token") String token) {
        if (token == null || token.isEmpty()) {
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
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("${mapping.outline.getBySpecificLessonId}/{id}")
    public ResponseEntity<List<Outline>> getBySpecificLessonId(@PathVariable("id") long id, @RequestHeader("User-Token") String token) {
        if (token == null || token.isEmpty()) {
            log.info("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<Outline> cs = new ArrayList<>();
        ArrayList<Long> ids = new ArrayList<>();
        try {
            cs = (ArrayList<Outline>) outlineRepository.getBySpecificLessonId(id);
            for (Outline c : cs) {
                ids.add(c.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<String> ps = new ArrayList<>();
        ps.add("id");
        ps.add("userId");
        ps.add("specificLessonId");
        try {
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.GET, ids, Entity.OUTLINE, ps)))) {
                return ResponseEntity.ok(cs);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("${mapping.outline.getByUserId}/{id}")
    public ResponseEntity<List<Outline>> getByUserId(@PathVariable("id") long id, @RequestHeader("User-Token") String token) {
        if (token == null || token.isEmpty()) {
            log.info("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<Outline> cs = new ArrayList<>();
        ArrayList<Long> ids = new ArrayList<>();
        try {
            cs = (ArrayList<Outline>) outlineRepository.getByUserId(id);
            for (Outline c : cs) {
                ids.add(c.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<String> ps = new ArrayList<>();
        ps.add("id");
        ps.add("userId");
        ps.add("specificLessonId");
        try {
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.GET, ids, Entity.OUTLINE, ps)))) {
                return ResponseEntity.ok(cs);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("${mapping.outline.create}")
    public ResponseEntity<Outline> create(@RequestBody Outline data, @RequestHeader("User-Token") String token) {
        if (token == null || token.isEmpty()) {
            log.info("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.CREATE, Collections.singletonList(data.getSpecificLessonId()), Entity.LESSON_TEMPLATE, null)))) {
                if (userRepository.getById(data.getUserId()) != null) {
                    log.info("bad request: user not exist");
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
                if (specificLessonRepository.getById(data.getSpecificLessonId()) != null) {
                    log.info("bad request: specific lesson not exist");
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
                data.setId(0);
                return ResponseEntity.ok(outlineRepository.save(data));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("${mapping.outline.patch}")
    public ResponseEntity<Outline> patch(@RequestBody Outline data, @RequestHeader("User-Token") String token) {
        if (token == null || token.isEmpty()) {
            log.info("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            Outline u = outlineRepository.getById(data.getId());
            if (u == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            ArrayList<String> ps = new ArrayList<>();
            if (data.getSpecificLessonId() != u.getSpecificLessonId())
                ps.add("specificLessonId");
            if (data.getUserId() != u.getUserId())
                ps.add("userId");
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.PATCH, Collections.singletonList(data.getId()), Entity.LESSON_TEMPLATE, ps)))) {
                return ResponseEntity.ok(outlineRepository.save(data));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("${mapping.outline.delete}/{ids}")
    public ResponseEntity<Void> deleteById(@PathVariable("ids") String id, @RequestHeader("User-Token") String token) {
        if (token == null || token.isEmpty()) {
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
        try {
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.DELETE, ids, Entity.OUTLINE, null)))) {
                for (Long l : ids) {
                    for (OutlineMedia om : outlineMediaRepository.getByOutlineId(l)) {
                        for (OutlineMediaComment omc : outlineMediaCommentRepository.getByOutlineMediaId(om.getId())) {
                            outlineMediaCommentRepository.delete(omc.getId());
                        }
                        if (om.getImageUrl() != null && !om.getImageUrl().isEmpty())
                            try {
                                imageRepository.delete(om.getImageUrl().split("/")[om.getImageUrl().split("/").length - 1]);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        outlineMediaRepository.delete(om.getId());
                    }
                    outlineRepository.delete(l);
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
