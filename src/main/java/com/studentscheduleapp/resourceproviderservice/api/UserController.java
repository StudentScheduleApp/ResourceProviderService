package com.studentscheduleapp.resourceproviderservice.api;

import com.studentscheduleapp.resourceproviderservice.models.*;
import com.studentscheduleapp.resourceproviderservice.models.api.AuthorizeUserRequest;
import com.studentscheduleapp.resourceproviderservice.repos.ImageRepository;
import com.studentscheduleapp.resourceproviderservice.repos.MemberRepository;
import com.studentscheduleapp.resourceproviderservice.repos.UserRepository;
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
public class UserController {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthorizeUserService authorizeUserService;
    @Autowired
    private UrlService urlService;
    private static final Logger log = LogManager.getLogger(UserController.class);

    @GetMapping("${mapping.user.getById}/{ids}")
    public ResponseEntity<List<User>> getById(@PathVariable("ids") String id, @RequestHeader("User-Token") String token) {
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
            log.warn("bad request: cant parse user ids: " + id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<String> ps = new ArrayList<>();
        ps.add("id");
        ps.add("firstName");
        ps.add("lastName");
        ps.add("banned");
        ps.add("avaUrl");
        ps.add("roles");
        try {
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.GET, ids, Entity.USER, ps)))) {
                ArrayList<User> ls = new ArrayList<>();
                for (Long l : ids) {
                    User u = userRepository.getById(l);
                    u.setEmail(null);
                    u.setPassword(null);
                    ls.add(u);
                }
                log.info("get user with ids: " + id + " success");
                return ResponseEntity.ok(ls);
            }
            log.warn("get user with ids: " + id + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("get user failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("${mapping.user.getByEmail}/{email}")
    public ResponseEntity<User> getByEmail(@PathVariable("email") String email, @RequestHeader("User-Token") String token) {
        if(token == null || token.isEmpty()) {
            log.warn("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        User u;
        try {
            u = userRepository.getByEmail(email);
            u.setEmail(null);
            u.setPassword(null);
        } catch (Exception e) {
            log.warn("get user with email: " + email + " success");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        ArrayList<String> ps = new ArrayList<>();
        ps.add("id");
        ps.add("firstName");
        ps.add("lastName");
        ps.add("banned");
        ps.add("avaUrl");
        ps.add("roles");
        try {
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.GET, Collections.singletonList(u.getId()), Entity.USER, ps)))) {
                u.setPassword(null);
                log.warn("get user with email: " + email + " success");
                return ResponseEntity.ok(u);
            }
            log.warn("get user with email: " + email + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("get user failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("${mapping.user.create}")
    public ResponseEntity<User> create(@RequestBody User data, @RequestHeader("User-Token") String token){
        if(token == null || token.isEmpty()) {
            log.warn("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getEmail() == null || data.getEmail().isEmpty()) {
            log.warn("bad request: user email is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getPassword() == null || data.getPassword().isEmpty()) {
            log.warn("bad request: user password is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getFirstName() == null || data.getFirstName().isEmpty()) {
            log.warn("bad request: user firstName is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getLastName() == null || data.getLastName().isEmpty()) {
            log.warn("bad request: user lastName is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getEmail().length() > 255) {
            log.warn("bad request: user email length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getPassword().length() > 255) {
            log.warn("bad request: user password length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getFirstName().length() > 255) {
            log.warn("bad request: user firstName length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getLastName().length() > 255) {
            log.warn("bad request: user lastName length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.CREATE, Collections.singletonList(0L), Entity.USER, null)))){
                data.setBanned(false);
                data.setRoles(Collections.singletonList(Role.USER));
                data.setAvaUrl(null);
                data.setId(0);
                User da = userRepository.save(data);
                log.info("create user with id: " + da.getId() + " success");
                return ResponseEntity.ok(da);
            }
            log.warn("create user: failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("create user failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PatchMapping("${mapping.user.patch}")
    public ResponseEntity<User> patch(@RequestBody User data, @RequestHeader("User-Token") String token, @RequestParam(value = "image", required = false) MultipartFile file, @RequestParam("params") String params){
        if(token == null || token.isEmpty()) {
            log.warn("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        List<String> ps = Arrays.asList(params.split(","));
        if((data.getEmail() == null || data.getEmail().isEmpty()) && ps.contains("email")) {
            log.warn("bad request: user email is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if((data.getPassword() == null || data.getPassword().isEmpty()) && ps.contains("password")) {
            log.warn("bad request: user email is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if((data.getFirstName() == null || data.getFirstName().isEmpty()) && ps.contains("firstName")) {
            log.warn("bad request: user firstName is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if((data.getLastName() == null || data.getLastName().isEmpty()) && ps.contains("lastName")) {
            log.warn("bad request: user lastName is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getEmail() != null && data.getEmail().length() > 255) {
            log.warn("bad request: user email length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getPassword() != null && data.getPassword().length() > 255) {
            log.warn("bad request: user password length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getFirstName() != null && data.getFirstName().length() > 255) {
            log.warn("bad request: user firstName length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getLastName() != null && data.getLastName().length() > 255) {
            log.warn("bad request: user lastName length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            User requester = userRepository.getById(authorizeUserService.getUserIdByToken(token));
            User u = userRepository.getById(data.getId());
            if (u == null) {
                log.warn("patch user with id: " + data.getId() + " failed: entity not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            if (ps.contains("email"))
                u.setEmail(data.getEmail());
            if (ps.contains("password"))
                u.setPassword(data.getPassword());
            if (ps.contains("firstName"))
                u.setFirstName(data.getFirstName());
            if (ps.contains("lastName"))
                u.setLastName(data.getLastName());
            if (ps.contains("banned"))
                u.setBanned(data.isBanned());
            if (ps.contains("roles"))
                u.setRoles(data.getRoles());
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.PATCH, Collections.singletonList(data.getId()), Entity.USER, ps)))){
                if (file != null && !file.isEmpty()) {
                    String url = imageRepository.upload(file);
                    if (url != null){
                        if (u.getAvaUrl() != null && !u.getAvaUrl().isEmpty())
                            imageRepository.delete(urlService.getNameFromImageUrl(u.getAvaUrl()));
                        data.setAvaUrl(url);
                    }
                }
                User d = userRepository.save(u);
                log.info("patch user with id " + d.getId() + " success");
                return ResponseEntity.ok(d);
            }
            log.warn("patch user with id: " + data.getId() + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("patch user with id " + data.getId() + " failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("${mapping.user.delete}/{ids}")
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
            log.warn("bad request: cant parse user ids: " + id);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.DELETE, ids, Entity.USER, null)))){
                for (Long l : ids) {
                    User u = userRepository.getById(l);
                    if (u.getAvaUrl() != null && !u.getAvaUrl().isEmpty())
                        imageRepository.delete(urlService.getNameFromImageUrl(u.getAvaUrl()));
                    for (Member m : memberRepository.getByUserId(l))
                        memberRepository.delete(m.getId());
                    userRepository.delete(l);
                }
                log.info("delete user with ids: " + id + " success");
                return ResponseEntity.ok().build();
            }
            log.warn("delete user with ids: " + id + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("delete user with ids: " + id + " failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
