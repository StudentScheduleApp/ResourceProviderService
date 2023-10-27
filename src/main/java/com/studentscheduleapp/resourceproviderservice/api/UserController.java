package com.studentscheduleapp.resourceproviderservice.api;

import com.studentscheduleapp.resourceproviderservice.repos.UserRepository;
import com.studentscheduleapp.resourceproviderservice.models.CustomLesson;
import com.studentscheduleapp.resourceproviderservice.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("id/{id}")
    public ResponseEntity<User> getById(@PathVariable("id") long id){
        return ResponseEntity.ok(userRepository.findById(id).orElse(null));
    }
    @GetMapping("email/{email}")
    public ResponseEntity<User> getByEmail(@PathVariable("email") String email){
        return ResponseEntity.ok(userRepository.findByEmail(email).orElse(null));
    }
    @PostMapping("save")
    public ResponseEntity<User> save(@RequestBody User data){
        return ResponseEntity.ok(userRepository.save(data));
    }
    @DeleteMapping("delete/{id}")
    public ResponseEntity<List<CustomLesson>> deleteById(@PathVariable("id") long id){
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
