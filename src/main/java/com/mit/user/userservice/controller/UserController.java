package com.mit.user.userservice.controller;

import com.mit.user.userservice.component.JwtTokenProvider;
import com.mit.user.userservice.model.*;
import com.mit.user.userservice.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {

    final UsersRepository usersRepository;
    private final IUserService userService;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    public UserController(UsersRepository usersRepository, IUserService userService) {
        this.usersRepository = usersRepository;
        this.userService = userService;
    }

    @CrossOrigin(origins = "*")
    @GetMapping(path = "/")
    public List<UserDto> getUsers(@RequestParam(required = false) String lastName) {
        Iterable<User> users;
        if (lastName != null && !lastName.isEmpty()) {
            users = usersRepository.findUserByLastName(lastName);
        } else {
            users = usersRepository.findAll();
        }
        List<UserDto> model = new ArrayList<>();
        for (User user : users) {
            UserDto tempUser = new UserDto();
            tempUser.setId(user.getId());
            tempUser.setFirstName(user.getFirstName());
            tempUser.setLastName(user.getLastName());
            tempUser.setRecordBookNumber(user.getRecordBookNumber());
            tempUser.setRoles(user.getRoles());
            tempUser.setUserName(user.getUsername());
            model.add(tempUser);
        }
        return model;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/{id}")
    public Optional<User> getUser(@PathVariable long id) {
        return usersRepository.findById(id);
    }

    @CrossOrigin(origins = "*")
    @PostMapping(path = "/", consumes = "application/json", produces = "application/json")
    public ResponseEntity addUser(@RequestBody UserDto user) {
        userService.registerUser(user);
        if (user.getErrorDescription() != null && !user.getErrorDescription().isEmpty()) {
            Map<Object, Object> errorModel = new HashMap<>();
            errorModel.put("success", false);
            errorModel.put("errorDescription", user.getErrorDescription());
            return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
        }
        Map<Object, Object> model = new HashMap<>();
        model.put("success", true);
        model.put("description", "Successful registration");
        return new ResponseEntity<>(model, HttpStatus.OK);
    }

//    @GetMapping("users/search")
//    public List<User> getUserByRecordBookNumber(@RequestParam String recordBookNumber) {
//        return usersRepository.getUserByRecordBookNumber(recordBookNumber);
//    }

    @CrossOrigin(origins = "*")
    @GetMapping("/{userId}/courses/")
    public ResponseEntity getUserCourses(HttpServletRequest request, @PathVariable Long userId) {
        if (jwtTokenProvider.validateUsersData(request, userId)) {
            return new ResponseEntity<>(usersRepository.getUserCourseById(userId), HttpStatus.OK);
        } else {
            Map<Object, Object> errorModel = new HashMap<>();
            errorModel.put("success", false);
            errorModel.put("errorDescription", "Wrong id. Forbidden");
            return new ResponseEntity<>(errorModel, HttpStatus.FORBIDDEN);
        }

    }


//    @GetMapping("lastName")
//    public Iterable<User> getUserByLastName(@RequestParam String lastName) {
//        return usersRepository.findUserByLastName(lastName);
//    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id) {
        usersRepository.deleteById(id);
    }


}
