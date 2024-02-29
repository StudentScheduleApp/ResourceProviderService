package com.studentscheduleapp.resourceproviderservice.api;

import com.studentscheduleapp.resourceproviderservice.models.*;
import com.studentscheduleapp.resourceproviderservice.models.api.AuthorizeUserRequest;
import com.studentscheduleapp.resourceproviderservice.repos.*;
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
public class GroupController {

    private static final Logger log = LogManager.getLogger(GroupController.class);
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

    @GetMapping("${mapping.group.getById}/{ids}")
    public ResponseEntity<List<Group>> getById(@PathVariable("ids") String id, @RequestHeader("User-Token") String token) {
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
            log.warn("bad request: cant parse group ids: " + id);
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
                log.info("get group with ids: " + id + " success");
                return ResponseEntity.ok(ls);
            }
            log.warn("get group with ids: " + id + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("get group failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("${mapping.group.create}")
    public ResponseEntity<Group> create(@RequestBody Group data, @RequestHeader("User-Token") String token) {
        if (token == null || token.isEmpty()) {
            log.warn("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (data.getName() == null || data.getName().isEmpty()) {
            log.warn("bad request: group name is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (data.getName().length() > 255) {
            log.warn("bad request: group name length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.CREATE, Collections.singletonList(0L), Entity.GROUP, null)))) {
                data.setAvaUrl(null);
                data.setId(0);
                Group g = groupRepository.save(data);
                long uid = authorizeUserService.getUserIdByToken(token);
                List<MemberRole> roles = new ArrayList<MemberRole>();
                roles.add(MemberRole.MEMBER);
                roles.add(MemberRole.ADMIN);
                roles.add(MemberRole.OWNER);
                memberRepository.save(new Member(0, g.getId(), uid, roles));
                log.info("create group with id: " + g.getId() + " success");
                return ResponseEntity.ok(g);
            }
            log.warn("create group: failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("create group failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("${mapping.group.patch}")
    public ResponseEntity<Group> patch(@RequestBody Group data, @RequestHeader("User-Token") String token, @RequestParam(value = "image", required = false) MultipartFile file, @RequestParam("params") String params) {
        if (token == null || token.isEmpty()) {
            log.warn("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        List<String> ps = Arrays.asList(params.split(","));
        if (data.getName() != null && data.getName().length() > 255 && ps.contains("name")) {
            log.warn("bad request: group name length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (data.getName() == null && ps.contains("name")) {
            log.warn("bad request: group name is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            Group u = groupRepository.getById(data.getId());
            if (u == null) {
                log.warn("patch group with id: " + data.getId() + " failed: entity not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            if (ps.contains("name"))
                u.setName(data.getName());
            if (ps.contains("chatId"))
                u.setChatId(data.getChatId());
            if (file != null && !file.isEmpty())
                ps.add("avaUrl");
            else if (data.getAvaUrl() == null || data.getAvaUrl().isEmpty())
                ps.add("avaUrl");
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.PATCH, Collections.singletonList(data.getId()), Entity.GROUP, ps)))) {
                if (file != null && !file.isEmpty()) {
                    if (file.getContentType() != null && file.getContentType().split("/")[0].equals("image")) {
                        String url = imageRepository.upload(file);
                        if (url == null) {
                            log.warn("patch group with id: " + data.getId() + " failed: cant upload image");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                        }
                        if (u.getAvaUrl() != null && !u.getAvaUrl().isEmpty())
                            imageRepository.delete(urlService.getNameFromImageUrl(u.getAvaUrl()));
                        u.setAvaUrl(url);
                    }
                    else {
                        log.warn("patch group with id: " + data.getId() + " failed: unsupported image type: " + file.getContentType());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                    }
                }
                Group g = groupRepository.save(data);
                log.info("patch group with id " + g.getId() + " success");
                return ResponseEntity.ok(g);
            }
            log.warn("patch group with id: " + data.getId() + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("patch group with id " + data.getId() + " failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("${mapping.group.delete}/{ids}")
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
            log.warn("bad request: cant parse group ids: " + id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.DELETE, ids, Entity.GROUP, null)))) {
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
                            for (OutlineMedia om : outlineMediaRepository.getByOutlineId(o.getId())) {
                                for (OutlineMediaComment omc : outlineMediaCommentRepository.getByOutlineMediaId(om.getId())) {
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
                log.info("delete group with ids: " + id + " success");
                return ResponseEntity.ok().build();
            }
            log.warn("delete group with ids: " + id + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("delete group with ids: " + id + " failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
