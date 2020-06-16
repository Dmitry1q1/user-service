package com.mit.user.userservice.controller;

import com.mit.user.userservice.model.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/")
public class ApiController {

    final VisitsRepository visitsRepository;
    final UsersRepository usersRepository;


    public ApiController(VisitsRepository visitsRepository, UsersRepository usersRepository) {
        this.visitsRepository = visitsRepository;
        this.usersRepository = usersRepository;
    }


    @GetMapping("visits")
    public Iterable<Visit> getVisits() {
        return visitsRepository.findAll();
    }

    @GetMapping("users")
    public ModelAndView test() {
        Map<String, Iterable<User>> model = new HashMap<>();
        model.put("users", usersRepository.findAll());
        return new ModelAndView("users", model);
    }

    public Iterable<User> getUsers() {
        return usersRepository.findAll();
    }

    @GetMapping("users/{id}")
    public Optional<User> getUser(@PathVariable long id) {
        return usersRepository.findById(id);
    }

    @PostMapping(path = "users/add", consumes = "application/json", produces = "application/json")
    public User addUser(@RequestBody User user) {
        return usersRepository.save(user);
    }

    @GetMapping("users/search")
    public List<User> getUserByRecordBookNumber(@RequestParam String recordBookNumber) {
        return usersRepository.getUserByRecordBookNumber(recordBookNumber);
    }

    @GetMapping("users/courses/{userId}")
    public List<String>  getUserCourses(@PathVariable Long userId){
        return usersRepository.getUserCourseById(userId);
    }


    @GetMapping("users/lastName")
    public Iterable<User> getUserByLastName(@RequestParam String lastName) {
        return usersRepository.findUserByLastName(lastName);
    }

    @DeleteMapping("users/{id}")
    public void deleteUserById(@PathVariable Long id) {
        usersRepository.deleteById(id);
    }


}
