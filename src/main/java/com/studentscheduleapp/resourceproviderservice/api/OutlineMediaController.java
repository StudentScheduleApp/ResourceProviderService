package com.studentscheduleapp.resourceproviderservice.api;

import com.studentscheduleapp.resourceproviderservice.models.*;
import com.studentscheduleapp.resourceproviderservice.models.api.AuthorizeUserRequest;
import com.studentscheduleapp.resourceproviderservice.repos.ImageRepository;
import com.studentscheduleapp.resourceproviderservice.repos.OutlineMediaCommentRepository;
import com.studentscheduleapp.resourceproviderservice.repos.OutlineMediaRepository;
import com.studentscheduleapp.resourceproviderservice.repos.OutlineRepository;
import com.studentscheduleapp.resourceproviderservice.services.AuthorizeUserService;
import com.studentscheduleapp.resourceproviderservice.services.UrlService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
public class OutlineMediaController {
    private static final Logger log = LogManager.getLogger(OutlineMediaController.class);
    @Autowired
    private OutlineRepository outlineRepository;
    @Autowired
    private OutlineMediaRepository outlineMediaRepository;
    @Autowired
    private OutlineMediaCommentRepository outlineMediaCommentRepository;
    @Autowired
    private AuthorizeUserService authorizeUserService;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private UrlService urlService;

    @GetMapping("${mapping.outlineMedia.getById}/{ids}")
    public ResponseEntity<List<OutlineMedia>> getById(@PathVariable("ids") String id, @RequestHeader("User-Token") String token) {
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
            log.warn("bad request: cant parse outlineMedia ids: " + id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<String> ps = new ArrayList<>();
        ps.add("id");
        ps.add("timestamp");
        ps.add("outlineId");
        ps.add("imageUrl");
        try {
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.GET, ids, Entity.OUTLINE_MEDIA, ps)))) {
                ArrayList<OutlineMedia> ls = new ArrayList<>();
                for (Long l : ids) {
                    ls.add(outlineMediaRepository.getById(l));
                }
                log.info("get outlineMedia with ids: " + id + " success");
                return ResponseEntity.ok(ls);
            }
            log.warn("get outlineMedia with ids: " + id + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("get outlineMedia failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("${mapping.outlineMedia.getByOutlineId}/{id}")
    public ResponseEntity<List<OutlineMedia>> getByOutlineId(@PathVariable("id") long id, @RequestHeader("User-Token") String token) {
        if (token == null || token.isEmpty()) {
            log.warn("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<OutlineMedia> cs;
        ArrayList<Long> ids = new ArrayList<>();
        try {
            cs = (ArrayList<OutlineMedia>) outlineMediaRepository.getByOutlineId(id);
            if (cs == null)
                cs = new ArrayList<>();
            for (OutlineMedia c : cs) {
                ids.add(c.getId());
            }
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("get outlineMedia with outlineId " + id + " failed: " + errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<String> ps = new ArrayList<>();
        ps.add("id");
        ps.add("timestamp");
        ps.add("outlineId");
        ps.add("imageUrl");
        try {
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.GET, ids, Entity.OUTLINE_MEDIA, ps)))) {
                log.info("get outlineMedia with outlineId: " + id + " success");
                return ResponseEntity.ok(cs);
            }
            log.warn("get outlineMedia with outlineId: " + id + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("get outlineMedia with outlineId " + id + " failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("${mapping.outlineMedia.create}")
    public ResponseEntity<OutlineMedia> create(@RequestBody OutlineMedia data, @RequestHeader("User-Token") String token, @RequestParam("image") MultipartFile file) {
        if (token == null || token.isEmpty()) {
            log.warn("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (file == null || file.isEmpty()) {
            log.warn("bad request: outlineMedia image is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            if (outlineRepository.getById(data.getOutlineId()) != null) {
                log.warn("bad request: outlineMedia outline not exist");
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.CREATE, Collections.singletonList(data.getOutlineId()), Entity.OUTLINE_MEDIA, null)))) {
                String url = imageRepository.upload(file);
                if (url == null) {
                    log.warn("create outlineMedia with outlineId: " + data.getOutlineId() + " failed: cant upload image");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }
                data.setImageUrl(url);
                data.setTimestamp(System.currentTimeMillis());
                data.setId(0);
                log.info("create outlineMedia with outlineId: " + data.getOutlineId() + " success");
                return ResponseEntity.ok(outlineMediaRepository.save(data));
            }
            log.warn("create outlineMedia with outlineId: " + data.getOutlineId() + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("create outlineMedia failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("${mapping.outlineMedia.patch}")
    public ResponseEntity<OutlineMedia> patch(@RequestBody OutlineMedia data, @RequestHeader("User-Token") String token, @RequestParam("image") MultipartFile file, @RequestParam("params") String params) {
        if (token == null || token.isEmpty()) {
            log.warn("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            OutlineMedia u = outlineMediaRepository.getById(data.getId());
            if (u == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            List<String> ps = Arrays.asList(params.split(","));
            if (ps.contains("outlineId"))
                data.setOutlineId(u.getOutlineId());
            if (ps.contains("imageUrl"))
                data.setImageUrl(u.getImageUrl());
            if (ps.contains("timestamp"))
                data.setTimestamp(u.getTimestamp());

            if (outlineRepository.getById(data.getOutlineId()) == null && ps.contains("outlineId")) {
                log.warn("bad request: outline not exist");
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.PATCH, Collections.singletonList(data.getId()), Entity.OUTLINE_MEDIA, ps)))) {
                if (file != null && !file.isEmpty()) {
                    if (file.getContentType() != null && file.getContentType().split("/")[0].equals("image")) {
                        String url = imageRepository.upload(file);
                        if (url == null) {
                            log.warn("patch outlineMedia with id: " + data.getId() + " failed: cant upload image");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                        }
                        if (u.getImageUrl() != null && !u.getImageUrl().isEmpty())
                            imageRepository.delete(u.getImageUrl());
                        data.setImageUrl(url);
                    }
                    else {
                        log.warn("patch outlineMedia with id: " + data.getId() + " failed: unsupported image type: " + file.getContentType());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                    }
                }
                OutlineMedia c = outlineMediaRepository.save(u);
                log.info("patch outlineMedia with id " + data.getId() + " success");
                return ResponseEntity.ok(c);
            }
            log.warn("patch outlineMedia with id: " + data.getId() + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("patch outlineMedia with id " + data.getId() + " failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("${mapping.outlineMedia.delete}/{ids}")
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
            log.warn("bad request: cant parse outlineMedia ids: " + id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.DELETE, ids, Entity.OUTLINE_MEDIA, null)))) {
                for (Long l : ids) {
                    OutlineMedia m = outlineMediaRepository.getById(l);
                    imageRepository.delete(m.getImageUrl());
                    for (OutlineMediaComment omc : outlineMediaCommentRepository.getByOutlineMediaId(m.getId()))
                        outlineMediaCommentRepository.delete(omc.getMediaId());
                    outlineMediaRepository.delete(l);
                }
                log.info("delete outlineMedia with ids: " + id + " success");
                return ResponseEntity.ok().build();
            }
            log.warn("delete outlineMedia with ids: " + id + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("delete outlineMedia with ids: " + id + " failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
