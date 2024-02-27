package com.studentscheduleapp.resourceproviderservice.api;

import com.studentscheduleapp.resourceproviderservice.models.AuthorizeEntity;
import com.studentscheduleapp.resourceproviderservice.models.AuthorizeType;
import com.studentscheduleapp.resourceproviderservice.models.Entity;
import com.studentscheduleapp.resourceproviderservice.models.LessonTemplate;
import com.studentscheduleapp.resourceproviderservice.models.api.AuthorizeUserRequest;
import com.studentscheduleapp.resourceproviderservice.repos.CustomLessonRepository;
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
import java.util.*;

@RestController
public class LessonTemplateController {

    @Autowired
    private LessonTemplateRepository lessonTemplateRepository;
    @Autowired
    private ScheduleTemplateRepository scheduleTemplateRepository;
    @Autowired
    private CustomLessonRepository customLessonRepository;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private AuthorizeUserService authorizeUserService;
    private static final Logger log = LogManager.getLogger(LessonTemplateController.class);

    @GetMapping("${mapping.lessonTemplate.getById}/{ids}")
    public ResponseEntity<List<LessonTemplate>> getById(@PathVariable("ids") String id, @RequestHeader("User-Token") String token) {
        if(token == null || token.isEmpty()) {
            log.warn("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<Long> ids = new ArrayList<>();
        try {
            for (int i = 0; i < id.split(",").length; i++) {
                ids.add(Long.parseLong(id.split(",")[i]));
            }
        } catch (Exception e) {
            log.warn("bad request: cant parse lessonTemplate ids: " + id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<String> ps = new ArrayList<>();
        ps.add("id");
        ps.add("scheduleTemplateId");
        ps.add("lessonId");
        ps.add("time");
        ps.add("comment");
        try {
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.GET, ids, Entity.LESSON_TEMPLATE, ps)))) {
                ArrayList<LessonTemplate> ls = new ArrayList<>();
                for (Long l : ids) {
                    ls.add(lessonTemplateRepository.getById(l));
                }
                log.info("get lessonTemplate with ids: " + id + " success");
                return ResponseEntity.ok(ls);
            }
            log.warn("get lessonTemplate with ids: " + id + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("get lessonTemplate failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("${mapping.lessonTemplate.getByScheduleTemplateId}/{id}")
    public ResponseEntity<List<LessonTemplate>> getByScheduleTemplateId(@PathVariable("id") long id, @RequestHeader("User-Token") String token){
        if(token == null || token.isEmpty()) {
            log.warn("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<LessonTemplate> cs;
        ArrayList<Long> ids = new ArrayList<>();
        try {
            cs = (ArrayList<LessonTemplate>) lessonTemplateRepository.getByScheduleTemplateId(id);
            if (cs == null)
                cs = new ArrayList<>();
            for (LessonTemplate c : cs) {
                ids.add(c.getId());
            }
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("get lessonTemplate with scheduleTemplateId " + id + " failed: " + errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<String> ps = new ArrayList<>();
        ps.add("id");
        ps.add("scheduleTemplateId");
        ps.add("lessonId");
        ps.add("time");
        ps.add("comment");
        try {
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.GET, ids, Entity.OUTLINE, ps)))){
                log.info("get lessonTemplate with scheduleTemplateId: " + id + " success");
                return ResponseEntity.ok(cs);
            }
            log.warn("get lessonTemplate with scheduleTemplateId: " + id + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("get lessonTemplate with scheduleTemplateId: " + id + " failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("${mapping.lessonTemplate.create}")
    public ResponseEntity<LessonTemplate> create(@RequestBody LessonTemplate data, @RequestHeader("User-Token") String token){
        if(token == null || token.isEmpty()) {
            log.warn("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getComment() != null && data.getComment().length() > 255) {
            log.warn("bad request: comment length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            if(scheduleTemplateRepository.getById(data.getScheduleTemplateId()) == null) {
                log.warn("bad request: lessonTemplate scheduleTemplate not exist");
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            if(customLessonRepository.getById(data.getLessonId()) == null) {
                log.warn("bad request: lessonTemplate customLesson not exist");
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.CREATE, Collections.singletonList(data.getScheduleTemplateId()), Entity.LESSON_TEMPLATE, null)))){
                data.setId(0);
                LessonTemplate lt = lessonTemplateRepository.save(data);

                scheduleService.updateSchedule(data.getScheduleTemplateId());
                log.info("create lessonTemplate with scheduleTemplate: " + data.getScheduleTemplateId() + " success");
                return ResponseEntity.ok(lt);
            }
            log.warn("create lessonTemplate with scheduleTemplate: " + data.getScheduleTemplateId() + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("create lessonTemplate failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PatchMapping("${mapping.lessonTemplate.patch}")
    public ResponseEntity<LessonTemplate> patch(@RequestBody LessonTemplate data, @RequestHeader("User-Token") String token, @RequestParam("params") String params){
        if(token == null || token.isEmpty()) {
            log.warn("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        List<String> ps = Arrays.asList(params.split(","));
        if(data.getComment() != null && data.getComment().length() > 255 && ps.contains("comment")) {
            log.warn("bad request: lessonTemplate comment length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            LessonTemplate u = lessonTemplateRepository.getById(data.getId());
            if (u == null) {
                log.warn("patch lessonTemplate with id: " + data.getId() + " failed: entity not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            if (ps.contains("scheduleTemplateId"))
                u.setScheduleTemplateId(data.getScheduleTemplateId());
            if (ps.contains("lessonId"))
                u.setLessonId(data.getLessonId());
            if (ps.contains("time"))
                u.setTime(data.getTime());
            if (ps.contains("comment"))
                u.setComment(data.getComment());
            if(customLessonRepository.getById(data.getLessonId()) == null && ps.contains("lessonId")) {
                log.warn("bad request: lessonTemplate customLesson not exist");
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            if(scheduleTemplateRepository.getById(data.getScheduleTemplateId()) == null && ps.contains("scheduleTemplateId")) {
                log.warn("bad request: lessonTemplate scheduleTemplate not exist");
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.PATCH, Collections.singletonList(data.getId()), Entity.LESSON_TEMPLATE, ps)))){
                LessonTemplate lt = lessonTemplateRepository.save(data);
                scheduleService.updateSchedule(data.getScheduleTemplateId());
                log.info("patch lessonTemplate with id " + data.getId() + " success");
                return ResponseEntity.ok(lt);
            }
            log.warn("patch lessonTemplate with id: " + data.getId() + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("patch lessonTemplate with id " + data.getId() + " failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("${mapping.lessonTemplate.delete}/{ids}")
    public ResponseEntity<Void> deleteById(@PathVariable("ids") String id, @RequestHeader("User-Token") String token){
        if(token == null || token.isEmpty()) {
            log.warn("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<Long> ids = new ArrayList<>();
        try {
            for (int i = 0; i < id.split(",").length; i++) {
                ids.add(Long.parseLong(id.split(",")[i]));
            }
        } catch (Exception e){
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.warn("bad request: cant parse lessonTemplate ids: " + id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.DELETE, ids, Entity.LESSON_TEMPLATE, null)))){
                Set<Long> is = new HashSet<>();
                for (Long l : ids) {
                    LessonTemplate lt = lessonTemplateRepository.getById(l);
                    lessonTemplateRepository.delete(l);
                    is.add(lt.getScheduleTemplateId());
                }
                for (Long l : is) {
                    scheduleService.updateSchedule(l);
                }
                log.info("delete lessonTemplate with ids: " + id + " success");
                return ResponseEntity.ok().build();
            }
            log.warn("delete lessonTemplate with ids: " + id + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("delete lessonTemplate with ids: " + id + " failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
