package com.mit.user.userservice.controller;

import com.mit.user.userservice.component.JwtTokenProvider;
import com.mit.user.userservice.model.Course;
import com.mit.user.userservice.model.User;
import com.mit.user.userservice.model.UserDto;
import com.mit.user.userservice.repository.CoursesRepository;
import com.mit.user.userservice.repository.UsersRepository;
import com.mit.user.userservice.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    final UsersRepository usersRepository;
    final CoursesRepository coursesRepository;
    private final IUserService userService;

    @Value("${image.url-user-folder}")
    private String imagePath;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    public UserController(UsersRepository usersRepository, CoursesRepository coursesRepository,
                          IUserService userService) {
        this.usersRepository = usersRepository;
        this.coursesRepository = coursesRepository;
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
            tempUser.setUserDescription(user.getUserDescription());
            tempUser.setUserPicture(user.getUserPicture());
            model.add(tempUser);
        }
        return model;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/token")
    public ResponseEntity getUserByToken(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        Long userId = jwtTokenProvider.getUserId(token);

        Optional<User> user = usersRepository.findUserById(userId);
        if (user.isPresent()) {

            Map<Object, Object> model = new LinkedHashMap<>();
            model.put("success", true);
            model.put("user", user.get());
            List<Course> userCourses = coursesRepository.getUserCoursesById(userId);
            model.put("userCourses", userCourses);
            return new ResponseEntity(model, HttpStatus.OK);
        } else {
            Map<Object, Object> errorModel = new HashMap<>();
            errorModel.put("success", false);
            errorModel.put("errorDescription", "User not found");
            return new ResponseEntity(errorModel, HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/{id}")
    public ResponseEntity getUser(@PathVariable long id) {
        Optional<User> user = usersRepository.findUserById(id);
        if (user.isPresent()) {

            Map<Object, Object> model = new LinkedHashMap<>();
            model.put("success", true);
            model.put("user", user.get());
            List<Course> userCourses = coursesRepository.getUserCoursesById(id);
            model.put("userCourses", userCourses);
            return new ResponseEntity(model, HttpStatus.OK);
        } else {
            Map<Object, Object> errorModel = new HashMap<>();
            errorModel.put("success", false);
            errorModel.put("errorDescription", "User not found");
            return new ResponseEntity(errorModel, HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(origins = "*")
    @PostMapping(path = "/user-avatar/", consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity addUserPicture(HttpServletRequest request,
                                         @RequestParam("avatar") MultipartFile userImage) {

        String token = jwtTokenProvider.resolveToken(request);
        Long userId = jwtTokenProvider.getUserId(token);
        if (!userImage.isEmpty()) {
            try {
                String newFileName = "user_" + userId + userImage.getOriginalFilename();
                File image = new File(imagePath + newFileName);
                OutputStream out = new FileOutputStream(image);

                usersRepository.addPictureUrlToUser(image.getAbsolutePath(), userId);
                out.write(userImage.getBytes());
                out.close();
            } catch (IOException e) {
                Map<Object, Object> errorModel = new HashMap<>();
                errorModel.put("success", false);
                errorModel.put("errorDescription", e.getMessage());
                return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
            }
        }
        Map<Object, Object> model = new HashMap<>();
        model.put("success", true);
        model.put("description", "User avatar was successful added");
        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @PostMapping(path = "/", consumes = "application/json", produces = "application/json")
    public ResponseEntity addUser(@RequestBody UserDto user) {
        userService.registerUser(user);
        usersRepository.addRoleToUser(user.getId(), 2);
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

    @CrossOrigin(origins = "*")
    @PutMapping(path = "/", consumes = "application/json", produces = "application/json")
    public ResponseEntity updateUser(@RequestBody UserDto user) {
        long userId = user.getId();
        Optional<User> userToUpdate = usersRepository.findById(userId);
        if (userToUpdate.isPresent()) {
            if (user.getFirstName() != null) {
                userToUpdate.get().setFirstName(user.getFirstName());
            }
            if (user.getLastName() != null) {
                userToUpdate.get().setLastName(user.getLastName());
            }
            if (user.getUserDescription() != null) {
                userToUpdate.get().setUserDescription(user.getUserDescription());
            }
            if (user.getUserName() != null) {
                if (usersRepository.findByUsername(user.getUserName()) != null) {
                    Map<Object, Object> errorModel = new HashMap<>();
                    errorModel.put("success", false);
                    errorModel.put("errorDescription", "Username is not unique");
                    return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
                } else {
                    userToUpdate.get().setUsername(user.getUserName());
                }
            }

            usersRepository.updateUserInfo(userId, userToUpdate.get().getFirstName(),
                    userToUpdate.get().getLastName(), userToUpdate.get().getUsername(),
                    userToUpdate.get().getUserDescription());
        }

        Map<Object, Object> model = new HashMap<>();
        model.put("success", true);
        model.put("description", "Successful update");
        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @GetMapping(path = "/findByRecordBookNumber/", consumes = "application/json",
            produces = "application/json")
    public ResponseEntity getUserByRecordBookNumber(@RequestBody UserDto userDto) {
        String recordBookNumber = userDto.getRecordBookNumber();
        User user = usersRepository.getUserByRecordBookNumber(recordBookNumber);
        if (user != null) {
            Map<Object, Object> model = new HashMap<>();
            model.put("success", true);
            model.put("courses", user);
            return new ResponseEntity<>(model, HttpStatus.OK);
        } else {
            Map<Object, Object> errorModel = new HashMap<>();
            errorModel.put("success", false);
            errorModel.put("errorDescription", "User not found");
            return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/{userId}/courses/")
    public ResponseEntity getUserCourses(HttpServletRequest request, @PathVariable Long userId) {
        if (jwtTokenProvider.validateUsersData(request, userId)) {
            List<Course> courses = coursesRepository.getUserCoursesById(userId);

            Map<Object, Object> model = new HashMap<>();
            model.put("success", true);
            model.put("courses", courses);
            return new ResponseEntity<>(model, HttpStatus.OK);
        } else {
            Map<Object, Object> errorModel = new HashMap<>();
            errorModel.put("success", false);
            errorModel.put("errorDescription", "Wrong id. Forbidden");
            return new ResponseEntity<>(errorModel, HttpStatus.FORBIDDEN);
        }

    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id) {
        usersRepository.deleteById(id);
    }


}
