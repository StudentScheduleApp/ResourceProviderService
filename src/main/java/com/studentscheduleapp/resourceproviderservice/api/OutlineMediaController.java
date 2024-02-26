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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class OutlineMediaController {
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
    private static final Logger log = LogManager.getLogger(OutlineMediaController.class);

    @GetMapping("${mapping.outlineMedia.getById}/{ids}")
    public ResponseEntity<List<OutlineMedia>> getById(@PathVariable("ids") String id, @RequestHeader("User-Token") String token) {
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
        ps.add("timestamp");
        ps.add("outlineId");
        ps.add("imageUrl");
        try {
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.GET, ids, Entity.OUTLINE_MEDIA, ps)))) {
                ArrayList<OutlineMedia> ls = new ArrayList<>();
                for (Long l : ids) {
                    ls.add(outlineMediaRepository.getById(l));
                }
                return ResponseEntity.ok(ls);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("${mapping.outlineMedia.getByOutlineId}/{id}")
    public ResponseEntity<List<OutlineMedia>> getByOutlineId(@PathVariable("id") long id, @RequestHeader("User-Token") String token){
        if(token == null || token.isEmpty()) {
            log.info("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<OutlineMedia> cs = new ArrayList<>();
        ArrayList<Long> ids = new ArrayList<>();
        try {
            cs = (ArrayList<OutlineMedia>) outlineMediaRepository.getByOutlineId(id);
            for (OutlineMedia c : cs) {
                ids.add(c.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<String> ps = new ArrayList<>();
        ps.add("id");
        ps.add("timestamp");
        ps.add("outlineId");
        ps.add("imageUrl");
        try {
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.GET, ids, Entity.OUTLINE_MEDIA, ps)))){
                return ResponseEntity.ok(cs);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("${mapping.outlineMedia.create}")
    public ResponseEntity<OutlineMedia> create(@RequestBody OutlineMedia data, @RequestHeader("User-Token") String token, @RequestParam("image") MultipartFile file){
        if(token == null || token.isEmpty()) {
            log.info("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(file == null || file.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        try {
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.CREATE, Collections.singletonList(data.getOutlineId()), Entity.OUTLINE_MEDIA, null)))){
                if(outlineRepository.getById(data.getOutlineId()) != null) {
                    log.info("bad request: outline not exist");
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
                String url = imageRepository.upload(file);
                if (url == null)
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                data.setImageUrl(url);
                data.setTimestamp(System.currentTimeMillis());
                data.setId(0);
                return ResponseEntity.ok(outlineMediaRepository.save(data));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PatchMapping("${mapping.outlineMedia.patch}")
    public ResponseEntity<OutlineMedia> patch(@RequestBody OutlineMedia data, @RequestHeader("User-Token") String token, @RequestParam("image") MultipartFile file){
        if(token == null || token.isEmpty()) {
            log.info("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            OutlineMedia u = outlineMediaRepository.getById(data.getId());
            if (u == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            ArrayList<String> ps = new ArrayList<>();
            if (data.getOutlineId() != u.getOutlineId())
                ps.add("outlineId");
            if (data.getTimestamp() != u.getTimestamp())
                ps.add("timestamp");
            if (file != null && !file.isEmpty())
                ps.add("imageUrl");
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.PATCH, Collections.singletonList(data.getId()), Entity.OUTLINE_MEDIA, ps)))){
                if (file != null && !file.isEmpty()) {
                    String url = imageRepository.upload(file);
                    if (url != null)
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                    if (u.getImageUrl() != null && !u.getImageUrl().isEmpty())
                        imageRepository.delete(urlService.getNameFromImageUrl(u.getImageUrl()));
                    data.setImageUrl(url);
                }
                return ResponseEntity.ok(outlineMediaRepository.save(data));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("${mapping.outlineMedia.delete}/{ids}")
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
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.DELETE, ids, Entity.OUTLINE_MEDIA, null)))){
                for (Long l : ids) {
                    OutlineMedia m = outlineMediaRepository.getById(l);
                    imageRepository.delete(urlService.getNameFromImageUrl(m.getImageUrl()));
                    for (OutlineMediaComment omc : outlineMediaCommentRepository.getByOutlineMediaId(m.getId()))
                        outlineMediaCommentRepository.delete(omc.getMediaId());
                    outlineMediaRepository.delete(l);
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
