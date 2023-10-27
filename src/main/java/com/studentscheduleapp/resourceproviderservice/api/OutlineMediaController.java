package com.studentscheduleapp.resourceproviderservice.api;

import com.studentscheduleapp.resourceproviderservice.models.*;
import com.studentscheduleapp.resourceproviderservice.models.api.AuthorizeUserRequest;
import com.studentscheduleapp.resourceproviderservice.repos.OutlineMediaRepository;
import com.studentscheduleapp.resourceproviderservice.services.AuthorizeUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("api/outlineMedias")
public class OutlineMediaController {

    @Autowired
    private OutlineMediaRepository outlineMediaRepository;
    @Autowired
    private AuthorizeUserService authorizeUserService;

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
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, Collections.singletonList(new AuthorizeEntity(AuthorizeType.GET, ids, Entity.CUSTOM_LESSON, ps))))) {
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
    public ResponseEntity<List<OutlineMedia>> getByOutlineId(@PathVariable("id") long id){
        return ResponseEntity.ok(outlineMediaRepository.findByOutlineId(id));
    }
    @PostMapping("save")
    public ResponseEntity<OutlineMedia> save(@RequestBody OutlineMedia data){
        return ResponseEntity.ok(outlineMediaRepository.save(data));
    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<List<CustomLesson>> deleteById(@PathVariable("id") long id){
        outlineMediaRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
