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
            log.info("bad request: token is null or empty");
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
    @GetMapping("${mapping.user.getByEmail}/{email}")
    public ResponseEntity<User> getByEmail(@PathVariable("email") String email, @RequestHeader("User-Token") String token) {
        if(token == null || token.isEmpty()) {
            log.info("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        User u;
        try {
            u = userRepository.getByEmail(email);
            u.setEmail(null);
            u.setPassword(null);
        } catch (Exception e) {
            e.printStackTrace();
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
                return ResponseEntity.ok(u);
            }
            log.warn("get group with email: " + email + " failed: unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            log.error("get group failed: " + errors);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("${mapping.user.create}")
    public ResponseEntity<User> create(@RequestBody User data, @RequestHeader("User-Token") String token){
        if(token == null || token.isEmpty()) {
            log.info("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getEmail() == null || data.getEmail().isEmpty()) {
            log.info("bad request: email is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getPassword() == null || data.getPassword().isEmpty()) {
            log.info("bad request: password is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getFirstName() == null || data.getFirstName().isEmpty()) {
            log.info("bad request: firstName is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getLastName() == null || data.getLastName().isEmpty()) {
            log.info("bad request: lastName is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getEmail() != null || data.getEmail().length() > 255) {
            log.info("bad request: email length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getPassword() != null || data.getPassword().length() > 255) {
            log.info("bad request: password length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getFirstName() != null || data.getFirstName().length() > 255) {
            log.info("bad request: first name length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getLastName() != null || data.getLastName().length() > 255) {
            log.info("bad request: last name length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.CREATE, Collections.singletonList(0L), Entity.USER, null)))){
                data.setBanned(false);
                data.setRoles(Collections.singletonList(Role.USER));
                data.setAvaUrl(null);
                data.setId(0);
                User da = userRepository.save(data);
                log.info("create group with id: " + da.getId() + " success");
                return ResponseEntity.ok(da);
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
    @PatchMapping("${mapping.user.patch}")
    public ResponseEntity<User> patch(@RequestBody User data, @RequestHeader("User-Token") String token, @RequestParam(value = "image", required = false) MultipartFile file){
        if(token == null || token.isEmpty()) {
            log.info("bad request: token is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getEmail() == null || data.getEmail().isEmpty()) {
            log.info("bad request: email is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getFirstName() == null || data.getFirstName().isEmpty()) {
            log.info("bad request: firstName is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getLastName() == null || data.getLastName().isEmpty()) {
            log.info("bad request: lastName is null or empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getEmail() != null && data.getEmail().length() > 255) {
            log.info("bad request: email length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getPassword() != null && data.getPassword().length() > 255) {
            log.info("bad request: password length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getFirstName() != null && data.getFirstName().length() > 255) {
            log.info("bad request: first name length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(data.getLastName() != null && data.getLastName().length() > 255) {
            log.info("bad request: last name length > 255");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            User requester = userRepository.getById(authorizeUserService.getUserIdByToken(token));
            User u = userRepository.getById(data.getId());
            if (u == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            ArrayList<String> ps = new ArrayList<>();
            data.setAvaUrl(u.getAvaUrl());
            if (data.getRoles() != null && (data.getRoles().size() == 0 || data.getRoles().contains(Role.ULTIMATE)))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            if (file != null && !file.isEmpty())
                ps.add("avaUrl");
          //  if (data.getPassword() != null && !data.getPassword().isEmpty())
          //      ps.add("password");
          //  if (data.getEmail() != null && !data.getEmail().equals(u.getEmail()))
          //      ps.add("email");
            if (data.getRoles() != null && !data.getRoles().equals(u.getRoles())){
                if(!data.getRoles().contains(Role.USER)){
                    data.getRoles().add(Role.USER);
                }
                if(!data.getRoles().contains(Role.ULTIMATE) && u.getRoles().contains(Role.ULTIMATE)){
                    data.getRoles().add(Role.ULTIMATE);
                }
                ps.add("roles");
            }
            if (data.getFirstName() != null && !data.getFirstName().equals(u.getFirstName())){
                ps.add("firstName");
            }
            else{
                data.setFirstName(u.getFirstName());
                ps.add("firstName");
            }
            if (data.getLastName() != null && !data.getLastName().equals(u.getLastName())) {
                ps.add("lastName");
            }
            else {
                data.setLastName(u.getLastName());
                ps.add("lastName");
            }
            if(data.getBanned() != null && data.getBanned() != u.getBanned() &&
                requester.getId() != data.getId()){
                ps.add("banned");
            }
            else{
                ps.add("banned");
                data.setBanned(false);
            }
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.PATCH, Collections.singletonList(data.getId()), Entity.USER, ps)))){
                if (file != null && !file.isEmpty()) {
                    String url = imageRepository.upload(file);
                    if (url != null){
                        if (u.getAvaUrl() != null && !u.getAvaUrl().isEmpty())
                            imageRepository.delete(urlService.getNameFromImageUrl(u.getAvaUrl()));
                        data.setAvaUrl(url);
                    }
                }
                data.setEmail(u.getEmail());
                data.setPassword(u.getPassword());
                return ResponseEntity.ok(userRepository.save(data));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("${mapping.user.delete}/{ids}")
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
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.DELETE, ids, Entity.USER, null)))){
                for (Long l : ids) {
                    User u = userRepository.getById(l);
                    if (u.getAvaUrl() != null && !u.getAvaUrl().isEmpty())
                        imageRepository.delete(urlService.getNameFromImageUrl(u.getAvaUrl()));
                    for (Member m : memberRepository.getByUserId(l))
                        memberRepository.delete(m.getId());
                    userRepository.delete(l);
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
