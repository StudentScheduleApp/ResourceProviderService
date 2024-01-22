package com.studentscheduleapp.resourceproviderservice.api;

import com.studentscheduleapp.resourceproviderservice.models.*;
import com.studentscheduleapp.resourceproviderservice.models.api.AuthorizeUserRequest;
import com.studentscheduleapp.resourceproviderservice.repos.*;
import com.studentscheduleapp.resourceproviderservice.services.AuthorizeUserService;
import com.studentscheduleapp.resourceproviderservice.services.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("api/groups")
public class GroupController {

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private LessonTemplateRepository lessonTemplateRepository;
    @Autowired
    private CustomLessonRepository customLessonRepository;
    @Autowired
    private SpecificLessonRepository specificLessonRepository;
    @Autowired
    private ScheduleTemplateRepository scheduleTemplateRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private OutlineRepository outlineRepository;
    @Autowired
    private OutlineMediaRepository outlineMediaRepository;
    @Autowired
    private OutlineMediaCommentRepository outlineMediaCommentRepository;
    @Autowired
    private AuthorizeUserService authorizeUserService;
    @Autowired
    private UrlService urlService;

    @GetMapping("id/{ids}")
    public ResponseEntity<List<Group>> getById(@PathVariable("ids") String id, @RequestHeader("User-Token") String token) {
        if(token == null || token.isEmpty()) {
            Logger.getGlobal().info("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
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
        ps.add("chatId");
        ps.add("avaUrl");
        ps.add("name");
        try {
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.GET, ids, Entity.GROUP, ps)))) {
                ArrayList<Group> ls = new ArrayList<>();
                for (Long l : ids) {
                    ls.add(groupRepository.getById(l));
                }
                return ResponseEntity.ok(ls);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("create")
    public ResponseEntity<Group> create(@RequestBody Group data, @RequestHeader("User-Token") String token){
        if(token == null || token.isEmpty()) {
            Logger.getGlobal().info("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.CREATE, Collections.singletonList(0L), Entity.GROUP, null)))){
                return ResponseEntity.ok(groupRepository.save(data));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PatchMapping("patch")
    public ResponseEntity<Group> patch(@RequestBody Group data, @RequestHeader("User-Token") String token, @RequestParam("image") MultipartFile file){
        if(token == null || token.isEmpty()) {
            Logger.getGlobal().info("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            Group u = groupRepository.getById(data.getId());
            if (u == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            ArrayList<String> ps = new ArrayList<>();
            if (data.getChatId() != u.getChatId())
                ps.add("chatId");
            if (!data.getName().equals(u.getName()))
                ps.add("name");
            if (file != null && !file.isEmpty())
                ps.add("avaUrl");
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.PATCH, Collections.singletonList(data.getId()), Entity.GROUP, null)))){
                if (u.getAvaUrl() != null && !u.getAvaUrl().isEmpty())
                    imageRepository.delete(urlService.getNameFromImageUrl(u.getAvaUrl()));
                data.setAvaUrl(imageRepository.upload(file));
                return ResponseEntity.ok(groupRepository.save(data));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("delete/{ids}")
    public ResponseEntity<Void> deleteById(@PathVariable("ids") String id, @RequestHeader("User-Token") String token){
        if(token == null || token.isEmpty()) {
            Logger.getGlobal().info("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<Long> ids = new ArrayList<>();
        try {
            for (int i = 0; i < id.split(",").length; i++) {
                ids.add(Long.parseLong(id.split(",")[i]));
            }
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.DELETE, ids, Entity.GROUP, null)))){
                for (Long l : ids) {
                    Group u = groupRepository.getById(l);
                    if (u.getAvaUrl() != null && !u.getAvaUrl().isEmpty())
                        imageRepository.delete(urlService.getNameFromImageUrl(u.getAvaUrl()));
                    for (CustomLesson lt : customLessonRepository.getByGroupId(l))
                        customLessonRepository.delete(lt.getId());
                    for (Member m : memberRepository.getByGroupId(l))
                        memberRepository.delete(m.getId());
                    for (ScheduleTemplate st : scheduleTemplateRepository.getByGroupId(l)) {
                        for (LessonTemplate lt : lessonTemplateRepository.getByScheduleTemplateId(st.getId()))
                            lessonTemplateRepository.delete(lt.getId());
                        scheduleTemplateRepository.delete(st.getId());
                    }
                    for (SpecificLesson sl : specificLessonRepository.getByGroupId(l)) {
                        for (Outline o : outlineRepository.getBySpecificLessonId(sl.getId())) {
                            for (OutlineMedia om : outlineMediaRepository.getByOutlineId(o.getId())){
                                for (OutlineMediaComment omc : outlineMediaCommentRepository.getByOutlineMediaId(om.getId())){
                                    outlineMediaCommentRepository.delete(omc.getId());
                                }
                                outlineMediaRepository.delete(om.getId());
                            }
                            outlineRepository.delete(o.getId());
                        }
                        specificLessonRepository.delete(sl.getId());
                    }
                    groupRepository.delete(l);
                }
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
