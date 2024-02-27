package com.studentscheduleapp.resourceproviderservice.api;

import com.studentscheduleapp.resourceproviderservice.models.*;
import com.studentscheduleapp.resourceproviderservice.models.api.AuthorizeUserRequest;
import com.studentscheduleapp.resourceproviderservice.repos.GroupRepository;
import com.studentscheduleapp.resourceproviderservice.repos.LessonTemplateRepository;
import com.studentscheduleapp.resourceproviderservice.repos.ScheduleTemplateRepository;
import com.studentscheduleapp.resourceproviderservice.services.AuthorizeUserService;
import com.studentscheduleapp.resourceproviderservice.services.ScheduleService;
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
public class ScheduleTemplateController {
    private static final Logger log = LogManager.getLogger(ScheduleTemplateController.class);
    @Autowired
    private LessonTemplateRepository lessonTemplateRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private ScheduleTemplateRepository scheduleTemplateRepository;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private AuthorizeUserService authorizeUserService;

    @GetMapping("${mapping.scheduleTemplate.getById}/{ids}")
    public ResponseEntity<List<ScheduleTemplate>> getById(@PathVariable("ids") String id, @RequestHeader("User-Token") String token) {
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
            log.warn("bad request: cant parse scheduleTemplate ids: " + id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<String> ps = new ArrayList<>();
        ps.add("id");
        ps.add("groupId");
        ps.add("name");
        ps.add("timeStart");
        ps.add("timeStop");
        ps.add("comment");
        try {
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.GET, ids, Entity.SCHEDULE_TEMPLATE, ps)))) {
                ArrayList<ScheduleTemplate> ls = new ArrayList<>();
                for (Long l : ids) {
                    ls.add(scheduleTemplateRepository.getById(l));
                }
                log.info("get scheduleTemplate with ids: " + id + " success");
                return ResponseEntity.ok(ls);
            }
            log.warn("get scheduleTemplate with ids: " + id + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("get scheduleTemplate failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("${mapping.scheduleTemplate.getByGroupId}/{id}")
    public ResponseEntity<List<ScheduleTemplate>> getByGroupId(@PathVariable("id") long id, @RequestHeader("User-Token") String token) {
        if (token == null || token.isEmpty()) {
            log.warn("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<ScheduleTemplate> cs = new ArrayList<>();
        ArrayList<Long> ids = new ArrayList<>();
        try {
            cs = (ArrayList<ScheduleTemplate>) scheduleTemplateRepository.getByGroupId(id);
            if (cs == null)
                cs = new ArrayList<>();
            for (ScheduleTemplate c : cs) {
                ids.add(c.getId());
            }
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("get scheduleTemplate with groupId " + id + " failed: " + errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<String> ps = new ArrayList<>();
        ps.add("id");
        ps.add("groupId");
        ps.add("name");
        ps.add("timeStart");
        ps.add("timeStop");
        ps.add("comment");
        try {
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.GET, ids, Entity.MEMBER, ps)))) {
                log.info("get scheduleTemplate with groupId: " + id + " success");
                return ResponseEntity.ok(cs);
            }
            log.warn("get scheduleTemplate with groupId: " + id + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("get scheduleTemplate with groupId " + id + " failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("${mapping.scheduleTemplate.create}")
    public ResponseEntity<ScheduleTemplate> create(@RequestBody ScheduleTemplate data, @RequestHeader("User-Token") String token) {
        if (token == null || token.isEmpty()) {
            log.warn("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (data.getName() == null || data.getName().isEmpty()) {
            log.warn("bad request: scheduleTemplate name is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (data.getName().length() > 255) {
            log.warn("bad request: scheduleTemplate name length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (data.getComment() != null && data.getComment().length() > 255) {
            log.warn("bad request: scheduleTemplate comment length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            if (groupRepository.getById(data.getGroupId()) == null) {
                log.warn("bad request: scheduleTemplate group not exist");
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.CREATE, Collections.singletonList(data.getGroupId()), Entity.SCHEDULE_TEMPLATE, null)))) {
                data.setId(0);
                ScheduleTemplate t = scheduleTemplateRepository.save(data);
                scheduleService.updateSchedule(t.getId());
                log.info("create scheduleTemplate with groupId: " + t.getGroupId() + " success");
                return ResponseEntity.ok(t);
            }
            log.warn("create scheduleTemplate with groupId: " + data.getGroupId() + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("create scheduleTemplate failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("${mapping.scheduleTemplate.patch}")
    public ResponseEntity<ScheduleTemplate> patch(@RequestBody ScheduleTemplate data, @RequestHeader("User-Token") String token, @RequestParam("params") String params) {
        if (token == null || token.isEmpty()) {
            log.warn("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        List<String> ps = Arrays.asList(params.split(","));
        if (data.getName() == null || data.getName().isEmpty()) {
            log.warn("bad request: scheduleTemplate name is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (data.getName().length() > 255) {
            log.warn("bad request: scheduleTemplate name length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (data.getComment() != null && data.getComment().length() > 255 && ps.contains("comment")) {
            log.warn("bad request: scheduleTemplate comment length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            ScheduleTemplate u = scheduleTemplateRepository.getById(data.getId());
            if (u == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            if (ps.contains("groupId"))
                u.setGroupId(data.getGroupId());
            if (ps.contains("name"))
                u.setName(data.getName());
            if (ps.contains("timeStart"))
                u.setTimeStart(data.getTimeStart());
            if (ps.contains("timeStop"))
                u.setTimeStop(data.getTimeStop());
            if (ps.contains("comment"))
                u.setComment(data.getComment());
            if (groupRepository.getById(data.getGroupId()) == null && ps.contains("groupId")) {
                log.warn("bad request: scheduleTemplate group not exist");
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.PATCH, Collections.singletonList(data.getId()), Entity.SCHEDULE_TEMPLATE, ps)))) {
                ScheduleTemplate t = scheduleTemplateRepository.save(data);
                scheduleService.updateSchedule(t.getId());
                log.info("patch scheduleTemplate with id " + data.getId() + " success");
                return ResponseEntity.ok(t);
            }
            log.warn("patch scheduleTemplate with id: " + data.getId() + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("patch scheduleTemplate with id " + data.getId() + " failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("${mapping.scheduleTemplate.delete}/{ids}")
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
            log.warn("bad request: cant parse scheduleTemplate ids: " + id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.DELETE, ids, Entity.SCHEDULE_TEMPLATE, null)))) {
                for (Long l : ids) {
                    for (LessonTemplate lt : lessonTemplateRepository.getByScheduleTemplateId(l))
                        lessonTemplateRepository.delete(lt.getId());
                    scheduleService.updateSchedule(l);
                    scheduleTemplateRepository.delete(l);
                }
                log.info("delete scheduleTemplate with ids: " + id + " success");
                return ResponseEntity.ok().build();
            }
            log.warn("delete scheduleTemplate with ids: " + id + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("delete scheduleTemplate with ids: " + id + " failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
