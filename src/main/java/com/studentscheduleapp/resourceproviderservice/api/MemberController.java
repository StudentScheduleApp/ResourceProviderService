package com.studentscheduleapp.resourceproviderservice.api;

import com.studentscheduleapp.resourceproviderservice.models.*;
import com.studentscheduleapp.resourceproviderservice.models.api.AuthorizeUserRequest;
import com.studentscheduleapp.resourceproviderservice.repos.MemberRepository;
import com.studentscheduleapp.resourceproviderservice.services.AuthorizeUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("api/members")
public class MemberController {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private AuthorizeUserService authorizeUserService;

    @GetMapping("id/{ids}")
    public ResponseEntity<List<Member>> getById(@PathVariable("ids") String id, @RequestHeader("User-Token") String token) {
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
        ps.add("groupId");
        ps.add("userId");
        ps.add("roles");
        try {
            if (authorizeUserService.authorize(new AuthorizeUserRequest(token, Collections.singletonList(new AuthorizeEntity(AuthorizeType.GET, ids, Entity.MEMBER, ps))))) {
                ArrayList<Member> ls = new ArrayList<>();
                for (Long l : ids) {
                    ls.add(memberRepository.getById(l));
                }
                return ResponseEntity.ok(ls);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("group/{id}")
    public ResponseEntity<List<Member>> getByGroupId(@PathVariable("id") long id, @RequestHeader("User-Token") String token){
        ArrayList<Member> cs = new ArrayList<>();
        ArrayList<Long> ids = new ArrayList<>();
        try {
            cs = (ArrayList<Member>) memberRepository.getByGroupId(id);
            for (Member c : cs) {
                ids.add(c.getId());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<String> ps = new ArrayList<>();
        ps.add("id");
        ps.add("groupId");
        ps.add("userId");
        ps.add("roles");
        try {
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, Collections.singletonList(new AuthorizeEntity(AuthorizeType.GET, ids, Entity.MEMBER, ps))))){
                return ResponseEntity.ok(cs);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("user/{id}")
    public ResponseEntity<List<Member>> getByUserId(@PathVariable("id") long id, @RequestHeader("User-Token") String token){
        ArrayList<Member> cs = new ArrayList<>();
        ArrayList<Long> ids = new ArrayList<>();
        try {
            cs = (ArrayList<Member>) memberRepository.getByUserId(id);
            for (Member c : cs) {
                ids.add(c.getId());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        ArrayList<String> ps = new ArrayList<>();
        ps.add("id");
        ps.add("groupId");
        ps.add("userId");
        ps.add("roles");
        try {
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, Collections.singletonList(new AuthorizeEntity(AuthorizeType.GET, ids, Entity.MEMBER, ps))))){
                return ResponseEntity.ok(cs);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("create")
    public ResponseEntity<Member> create(@RequestBody Member data, @RequestHeader("User-Token") String token){
        try {
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, Collections.singletonList(new AuthorizeEntity(AuthorizeType.CREATE, Collections.singletonList(data.getGroupId()), Entity.MEMBER, null))))){
                return ResponseEntity.ok(memberRepository.save(data));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PatchMapping("patch")
    public ResponseEntity<Member> patch(@RequestBody Member data, @RequestHeader("User-Token") String token){
        try {
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, Collections.singletonList(new AuthorizeEntity(AuthorizeType.PATCH, Collections.singletonList(data.getId()), Entity.MEMBER, null))))){
                return ResponseEntity.ok(memberRepository.save(data));
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
            if(authorizeUserService.authorize(new AuthorizeUserRequest(token, Collections.singletonList(new AuthorizeEntity(AuthorizeType.DELETE, ids, Entity.MEMBER, null))))){
                for (Long l : ids) {
                    memberRepository.delete(l);
                }
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
