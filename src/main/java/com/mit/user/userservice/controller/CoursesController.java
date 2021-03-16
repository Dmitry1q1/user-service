package com.mit.user.userservice.controller;

import com.mit.user.userservice.component.JwtTokenProvider;
import com.mit.user.userservice.model.*;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@RestController
@RequestMapping("/courses")
public class CoursesController {
    final private CoursesRepository coursesRepository;
    final private UsersRepository usersRepository;
    final private ProblemRepository problemRepository;

    @Value("${image.url-course-folder}")
    private String imagePath;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    public CoursesController(CoursesRepository coursesRepository, UsersRepository usersRepository,
                             ProblemRepository problemRepository) {
        this.coursesRepository = coursesRepository;
        this.usersRepository = usersRepository;
        this.problemRepository = problemRepository;
    }

    @CrossOrigin(origins = "*")
    @GetMapping(path = "/", produces = "application/json")
    public Iterable<CourseDto> getCourse() {
        Iterable<Course> courses = coursesRepository.findAll();
        List<CourseDto> resultCourses = new ArrayList<>();
        for (Course course : courses) {
            CourseDto courseDto = new CourseDto();
            List<Long> authorsId = new ArrayList<>();
            authorsId = coursesRepository.getCourseAuthorsByCourseId(course.getId());
            courseDto.setId(course.getId());
            courseDto.setCourseAuthorsId(authorsId);
            courseDto.setCourseName(course.getCourseName());
            courseDto.setCourseDescription(course.getCourseDescription());
            courseDto.setCourseDuration(course.getCourseDuration());
            courseDto.setCourseMainPictureUrl(course.getCourseMainPictureUrl());
            resultCourses.add(courseDto);
        }
        return resultCourses;
    }

    @CrossOrigin(origins = "*")
    @GetMapping(path = "/{courseId}", produces = "application/json")
    public ResponseEntity getCourseById(@PathVariable long courseId) {
        Optional<Course> course = coursesRepository.findById(courseId);

        CourseDto resultCourse = null;
        if (course.isPresent()) {
            resultCourse = new CourseDto();
            List<Long> authorsId = coursesRepository.getCourseAuthorsByCourseId(courseId);
            resultCourse.setCourseAuthorsId(authorsId);
            resultCourse.setId(courseId);
            resultCourse.setCourseName(course.get().getCourseName());
            resultCourse.setCourseDescription(course.get().getCourseDescription());
            resultCourse.setCourseDuration(course.get().getCourseDuration());
            resultCourse.setCourseMainPictureUrl(course.get().getCourseMainPictureUrl());
            List<Problem> problems = problemRepository.getAllProblemsFromCourse(courseId);
            resultCourse.setProblems(problems);

            Map<Object, Object> model = new HashMap<>();
            model.put("success", true);
            model.put("course", resultCourse);
            return new ResponseEntity(model, HttpStatus.OK);
        }

        Map<Object, Object> errorModel = new HashMap<>();
        errorModel.put("success", false);
        errorModel.put("errorDescription", "Course with this id was not found");
        return new ResponseEntity(errorModel, HttpStatus.BAD_REQUEST);
    }

    @CrossOrigin(origins = "*")
    @GetMapping(path = "/{courseId}/users", produces = "application/json")
    public List<UserDto> getAllUsersFromCourse(@PathVariable long courseId) {
        List<String> users = coursesRepository.getAllUsersFromCourses(courseId);
        List<UserDto> model = new ArrayList<>();
        for (String user : users) {
            UserDto tempUser = new UserDto();
            String[] words = user.split(",");

            tempUser.setId(Long.parseLong(words[0]));
            tempUser.setFirstName(words[1]);
            tempUser.setLastName(words[2]);
            tempUser.setRecordBookNumber(words[4]);
//            List<Role> roles = new ArrayList<>();
//            roles.add((Role.getName())words[5]);
//            tempUser.setRoles(words[5]);
            tempUser.setUserName(words[3]);
            model.add(tempUser);
        }
        return model;
    }

    @CrossOrigin(origins = "*")
    @PostMapping(path = "/", consumes = "application/json", produces = "application/json")
    public ResponseEntity addCourse(@RequestBody CourseDto courseDto) {
        Course course = new Course();
        course.setCourseName(courseDto.getCourseName());
        course.setCourseDescription(courseDto.getCourseDescription());
        course.setCourseDuration(courseDto.getCourseDuration());
        course.setId(0);
        Course resultCourse = coursesRepository.save(course);
        if (courseDto.getCourseAuthorsId() != null) {
            for (Long index : courseDto.getCourseAuthorsId()) {
                Optional<User> users = usersRepository.findById(index);
                users.ifPresent(user -> coursesRepository.addCourseAuthors(resultCourse.getId(), user.getId()));
            }
        }

        Map<Object, Object> model = new HashMap<>();
        model.put("success", true);
        model.put("course", resultCourse);

        return new ResponseEntity(model, HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping(path = "/{id}")
    public void deleteCourse(@PathVariable long id) {
        coursesRepository.deleteById(id);
    }

    @CrossOrigin(origins = "*")
    @PostMapping(path = "/{courseId}/course-avatar/", consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity addCoursePicture(@PathVariable long courseId,
                                           @RequestParam("avatar") MultipartFile courseImage) {

        if (!courseImage.isEmpty()) {
            try {
                String newFileName = "course_main_" + courseId + courseImage.getOriginalFilename();
                File image = new File(imagePath + newFileName);
                OutputStream out = new FileOutputStream(image);

                coursesRepository.addMainPictureUrlToCourse(image.getAbsolutePath(), courseId);
                out.write(courseImage.getBytes());
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
        model.put("description", "Course avatar was successful added");
        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @PostMapping(path = "/{courseId}/", consumes = "application/json")
    public ResponseEntity addUserToCourse(HttpServletRequest request, @PathVariable long courseId, @RequestParam(name = "userId") long userId) {
        Map<Object, Object> errorModel = new HashMap<>();
        errorModel.put("success", false);
        if (!jwtTokenProvider.validateUsersData(request, userId)) {
            errorModel.put("errorDescription", "Wrong id. Forbidden");
            return new ResponseEntity<>(errorModel, HttpStatus.FORBIDDEN);
        }

        try {
            Map<Object, Object> model = new HashMap<>();
            model.put("success", true);
            model.put("description", "User was successfully added to course");
            coursesRepository.addUserToCourse(courseId, userId);
            return new ResponseEntity<>(model, HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException exception = (ConstraintViolationException) e.getCause();
                if (!exception.getConstraintName().equals("PRIMARY")) {
                    errorModel.put("errorDescription", e.getMessage());
                    return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
//                    throw e;
                }
            } else {
                errorModel.put("errorDescription", e.getMessage());
                return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
//                throw e;
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping(path = "/{courseId}/", consumes = "application/json")
    public ResponseEntity deleteUserFromCourse(HttpServletRequest request, @PathVariable long courseId, @RequestParam(name = "userId") long userId) {
        Map<Object, Object> errorModel = new HashMap<>();
        errorModel.put("success", false);
        if (!jwtTokenProvider.validateUsersData(request, userId)) {
            errorModel.put("errorDescription", "Wrong id. Forbidden");
            return new ResponseEntity<>(errorModel, HttpStatus.FORBIDDEN);
        }
        coursesRepository.deleteUserFromCourse(courseId, userId);
        Map<Object, Object> model = new HashMap<>();
        model.put("success", true);
        model.put("description", "User was successfully deleted from course");
        return new ResponseEntity<>(model, HttpStatus.OK);

    }
}
