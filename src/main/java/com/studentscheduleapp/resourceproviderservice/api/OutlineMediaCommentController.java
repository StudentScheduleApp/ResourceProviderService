package com.studentscheduleapp.resourceproviderservice.api;

import com.studentscheduleapp.resourceproviderservice.models.AuthorizeEntity;
import com.studentscheduleapp.resourceproviderservice.models.AuthorizeType;
import com.studentscheduleapp.resourceproviderservice.models.Entity;
import com.studentscheduleapp.resourceproviderservice.models.OutlineMediaComment;
import com.studentscheduleapp.resourceproviderservice.models.api.AuthorizeUserRequest;
import com.studentscheduleapp.resourceproviderservice.repos.OutlineMediaCommentRepository;
import com.studentscheduleapp.resourceproviderservice.repos.OutlineMediaRepository;
import com.studentscheduleapp.resourceproviderservice.repos.UserRepository;
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
public class OutlineMediaCommentController {

    private static final Logger log = LogManager.getLogger(OutlineMediaCommentController.class);
    @Autowired
    private OutlineMediaCommentRepository outlineMediaCommentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OutlineMediaRepository outlineMediaRepository;
    @Autowired
    private AuthorizeUserService authorizeUserService;

    @GetMapping("${mapping.outlineMediaComment.getById}/{ids}")
    public ResponseEntity<List<OutlineMediaComment>> getById(@PathVariable("ids") String id, @RequestHeader("User-Token") String token) {
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
            log.warn("bad request: cant parse user ids: " + id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<String> ps = new ArrayList<>();
        ps.add("id");
        ps.add("text");
        ps.add("userId");
        ps.add("timestamp");
        ps.add("questionCommentId");
        ps.add("mediaId");
        try {
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.GET, ids, Entity.OUTLINE_MEDIA_COMMENT, ps)))) {
                ArrayList<OutlineMediaComment> ls = new ArrayList<>();
                for (Long l : ids) {
                    ls.add(outlineMediaCommentRepository.getById(l));
                }
                log.info("get outlineMediaComment with ids: " + id + " success");
                return ResponseEntity.ok(ls);
            }
            log.warn("get outlineMediaComment with ids: " + id + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("get outlineMediaComment failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("${mapping.outlineMediaComment.getByOutlineMediaId}/{id}")
    public ResponseEntity<List<OutlineMediaComment>> getByOutlineMediaId(@PathVariable("id") long id, @RequestHeader("User-Token") String token) {
        if (token == null || token.isEmpty()) {
            log.warn("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<OutlineMediaComment> cs;
        ArrayList<Long> ids = new ArrayList<>();
        try {
            cs = (ArrayList<OutlineMediaComment>) outlineMediaCommentRepository.getByOutlineMediaId(id);
            if (cs == null)
                cs = new ArrayList<>();
            for (OutlineMediaComment c : cs) {
                ids.add(c.getId());
            }
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("get outlineMediaComment with mediaId " + id + " failed: " + errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<String> ps = new ArrayList<>();
        ps.add("id");
        ps.add("text");
        ps.add("userId");
        ps.add("timestamp");
        ps.add("questionCommentId");
        ps.add("mediaId");
        try {
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.GET, ids, Entity.OUTLINE_MEDIA_COMMENT, ps)))) {
                log.info("get outlineMediaComment with mediaId: " + id + " success");
                return ResponseEntity.ok(cs);
            }
            log.warn("get outlineMediaComment with mediaId: " + id + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("get outlineMediaComment with mediaId " + id + " failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("${mapping.outlineMediaComment.create}")
    public ResponseEntity<OutlineMediaComment> create(@RequestBody OutlineMediaComment data, @RequestHeader("User-Token") String token) {
        if (token == null || token.isEmpty()) {
            log.warn("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (data.getText() == null || data.getText().isEmpty()) {
            log.warn("bad request: text is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (data.getText() != null && data.getText().length() > 255) {
            log.warn("bad request: text length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            if (outlineMediaRepository.getById(data.getMediaId()) == null) {
                log.warn("bad request: outlineMedia not exist");
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.CREATE, Collections.singletonList(data.getMediaId()), Entity.OUTLINE_MEDIA_COMMENT, null)))) {
                data.setUserId(authorizeUserService.getUserIdByToken(token));
                data.setTimestamp(System.currentTimeMillis());
                data.setId(0);
                log.info("create outlineMediaComment with mediaId: " + data.getMediaId() + " success");
                return ResponseEntity.ok(outlineMediaCommentRepository.save(data));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("create outlineMedia with mediaId " + data.getMediaId() + " failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("${mapping.outlineMediaComment.patch}")
    public ResponseEntity<OutlineMediaComment> patch(@RequestBody OutlineMediaComment data, @RequestHeader("User-Token") String token, @RequestParam("params") String params) {
        if (token == null || token.isEmpty()) {
            log.warn("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        List<String> ps = Arrays.asList(params.split(","));
        if ((data.getText() == null || data.getText().isEmpty()) && ps.contains("text")) {
            log.warn("bad request: text is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (data.getText() != null && data.getText().length() > 255 && ps.contains("text")) {
            log.warn("bad request: text length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            OutlineMediaComment u = outlineMediaCommentRepository.getById(data.getId());
            if (u == null) {
                log.warn("patch member with id: " + data.getId() + " failed: entity not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            if (ps.contains("text"))
                u.setText(data.getText());
            if (ps.contains("userId"))
                u.setUserId(data.getUserId());
            if (ps.contains("timestamp"))
                u.setTimestamp(data.getTimestamp());
            if (ps.contains("mediaId"))
                u.setMediaId(data.getMediaId());
            if (ps.contains("questionCommentId"))
                u.setQuestionCommentId(data.getQuestionCommentId());
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.PATCH, Collections.singletonList(data.getId()), Entity.OUTLINE_MEDIA_COMMENT, ps)))) {
                return ResponseEntity.ok(outlineMediaCommentRepository.save(u));
            }
            log.warn("patch member with id: " + data.getId() + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("patch member with id " + data.getId() + " failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("${mapping.outlineMediaComment.delete}/{ids}")
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
            log.warn("bad request: cant parse member ids: " + id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.DELETE, ids, Entity.OUTLINE_MEDIA_COMMENT, null)))) {
                for (Long l : ids) {
                    outlineMediaCommentRepository.delete(l);
                }
                log.info("delete outlineMediaComment with ids: " + id + " success");
                return ResponseEntity.ok().build();
            }
            log.warn("delete outlineMediaComment with ids: " + id + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("delete outlineMediaComment with ids: " + id + " failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
