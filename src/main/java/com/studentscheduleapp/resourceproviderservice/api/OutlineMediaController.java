package com.studentscheduleapp.resourceproviderservice.api;

import com.studentscheduleapp.resourceproviderservice.models.*;
import com.studentscheduleapp.resourceproviderservice.models.api.AuthorizeUserRequest;
import com.studentscheduleapp.resourceproviderservice.repos.*;
import com.studentscheduleapp.resourceproviderservice.services.AuthorizeUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("api/outlineMedias")
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

    @GetMapping("id/{ids}")
    public ResponseEntity<List<OutlineMedia>> getById(@PathVariable("ids") String id, @RequestHeader("User-Token") String token) {
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("outline/{id}")
    public ResponseEntity<List<OutlineMedia>> getByOutlineId(@PathVariable("id") long id, @RequestHeader("User-Token") String token){
        ArrayList<OutlineMedia> cs = new ArrayList<>();
        ArrayList<Long> ids = new ArrayList<>();
        try {
            cs = (ArrayList<OutlineMedia>) outlineMediaRepository.getByOutlineId(id);
            for (OutlineMedia c : cs) {
                ids.add(c.getId());
            }
        } catch (Exception e) {
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("create")
    public ResponseEntity<OutlineMedia> create(@RequestBody OutlineMedia data, @RequestHeader("User-Token") String token, @RequestParam("image") MultipartFile file){
        if(file == null || file.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        try {
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.CREATE, Collections.singletonList(data.getOutlineId()), Entity.OUTLINE_MEDIA, null)))){
                data.setImageUrl(imageRepository.upload(file));
                return ResponseEntity.ok(outlineMediaRepository.save(data));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PatchMapping("patch")
    public ResponseEntity<OutlineMedia> patch(@RequestBody OutlineMedia data, @RequestHeader("User-Token") String token, @RequestParam("image") MultipartFile file){
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
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.PATCH, Collections.singletonList(data.getId()), Entity.OUTLINE_MEDIA, null)))){
                if (file != null && !file.isEmpty()) {
                    imageRepository.delete(data.getImageUrl().split("/")[data.getImageUrl().split("/").length - 1]);
                    data.setImageUrl(imageRepository.upload(file));
                }
                return ResponseEntity.ok(outlineMediaRepository.save(data));
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
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.DELETE, ids, Entity.OUTLINE_MEDIA, null)))){
                for (Long l : ids) {
                    OutlineMedia m = outlineMediaRepository.getById(l);
                    try {
                        imageRepository.delete(Long.parseLong(m.getImageUrl().split("/")[m.getImageUrl().split("/").length - 1]));
                    } catch (Exception e) { }
                    for (OutlineMediaComment omc : outlineMediaCommentRepository.getByOutlineMediaId(m.getId()))
                        outlineMediaCommentRepository.delete(omc.getMediaId());
                    outlineMediaRepository.delete(l);
                }
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
