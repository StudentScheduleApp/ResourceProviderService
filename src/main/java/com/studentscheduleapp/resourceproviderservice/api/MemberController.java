package com.studentscheduleapp.resourceproviderservice.api;

import com.studentscheduleapp.resourceproviderservice.repos.MemberRepository;
import com.studentscheduleapp.resourceproviderservice.models.CustomLesson;
import com.studentscheduleapp.resourceproviderservice.models.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/members")
public class MemberController {

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping("id/{id}")
    public ResponseEntity<Member> getById(@PathVariable("id") long id){
        return ResponseEntity.ok(memberRepository.findById(id).orElse(null));
    }
    @GetMapping("group/{id}")
    public ResponseEntity<List<Member>> getByGroupId(@PathVariable("id") long id){
        return ResponseEntity.ok(memberRepository.findByGroupId(id));
    }
    @GetMapping("user/{id}")
    public ResponseEntity<List<Member>> getByUserId(@PathVariable("id") long id){
        return ResponseEntity.ok(memberRepository.findByUserId(id));
    }
    @PostMapping("save")
    public ResponseEntity<Member> save(@RequestBody Member data){
        return ResponseEntity.ok(memberRepository.save(data));
    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<List<CustomLesson>> deleteById(@PathVariable("id") long id){
        memberRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
