package com.studentscheduleapp.resourceproviderservice.api;

import com.studentscheduleapp.resourceproviderservice.models.*;
import com.studentscheduleapp.resourceproviderservice.models.api.AuthorizeUserRequest;
import com.studentscheduleapp.resourceproviderservice.repos.*;
import com.studentscheduleapp.resourceproviderservice.services.AuthorizeUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("api/users")
public class UserController {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthorizeUserService authorizeUserService;

    @GetMapping("id/{ids}")
    public ResponseEntity<List<User>> getById(@PathVariable("ids") String id, @RequestHeader("User-Token") String token) {
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
                return ResponseEntity.ok(ls);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("email/{email}")
    public ResponseEntity<User> getByEmail(@PathVariable("email") String email, @RequestHeader("User-Token") String token) {
        User u;
        try {
            u = userRepository.getByEmail(email);
            u.setEmail(null);
            u.setPassword(null);
        } catch (Exception e) {
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
                return ResponseEntity.ok(u);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("create")
    public ResponseEntity<User> create(@RequestBody User data, @RequestHeader("User-Token") String token){
        try {
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.CREATE, Collections.singletonList(0L), Entity.USER, null)))){
                data.setBanned(false);
                data.setRoles(Collections.singletonList(Role.USER));
                data.setAvaUrl(null);
                return ResponseEntity.ok(userRepository.save(data));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PatchMapping("patch")
    public ResponseEntity<User> patch(@RequestBody User data, @RequestHeader("User-Token") String token){
        try {
            User u = userRepository.getById(data.getId());
            if (u == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            ArrayList<String> ps = new ArrayList<>();
            if (!data.getAvaUrl().equals(u.getAvaUrl()))
                ps.add("avaUrl");
            if (!data.getPassword().equals(u.getPassword()))
                ps.add("password");
            if (!data.getEmail().equals(u.getEmail()))
                ps.add("email");
            if (!data.getRoles().equals(u.getRoles()))
                ps.add("roles");
            if (!data.getFirstName().equals(u.getFirstName()))
                ps.add("firstName");
            if (!data.getLastName().equals(u.getLastName()))
                ps.add("lastName");
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.PATCH, Collections.singletonList(data.getId()), Entity.USER, ps)))){
                return ResponseEntity.ok(userRepository.save(data));
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
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, new AuthorizeEntity(AuthorizeType.DELETE, ids, Entity.USER, null)))){
                for (Long l : ids) {
                    for (Member m : memberRepository.getByUserId(l))
                        memberRepository.delete(m.getId());
                    userRepository.delete(l);
                }
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
