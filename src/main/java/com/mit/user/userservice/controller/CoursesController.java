package com.mit.user.userservice.controller;

import com.mit.user.userservice.model.*;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/courses")
public class CoursesController {
    final private CoursesRepository coursesRepository;
    final private UsersRepository usersRepository;

    public CoursesController(CoursesRepository coursesRepository, UsersRepository usersRepository) {
        this.coursesRepository = coursesRepository;
        this.usersRepository = usersRepository;
    }

    @CrossOrigin(origins = "*")
    @GetMapping(path = "/", produces = "application/json")
    public Iterable<CourseDto> getCourse() {
        Iterable<Course> courses = coursesRepository.findAll();
        List<CourseDto> resultCourses = new ArrayList<>();
        for (Course course : courses) {
            CourseDto courseDto = new CourseDto();
            courseDto.setId(course.getId());
            courseDto.setCourseName(course.getCourseName());
            resultCourses.add(courseDto);
        }
        return resultCourses;
    }

    @CrossOrigin(origins = "*")
    @GetMapping(path = "/{courseId}", produces = "application/json")
    public Optional<CourseDto> getCourseById(@PathVariable long courseId) {
        Optional<Course> course = coursesRepository.findById(courseId);
        CourseDto resultCourse = null;
        if (course.isPresent()) {
            resultCourse = new CourseDto();
            resultCourse.setId(courseId);
            resultCourse.setCourseName(course.get().getCourseName());
            List<Problem> problems = new ArrayList<>();
            for (Problem problem : course.get().getProblems()) {
                Problem problemDto = new Problem();
                problemDto.setId(problem.getId());
                problemDto.setProblemName(problem.getProblemName());
                problemDto.setProblemText(problem.getProblemText());
                problemDto.setProblemTime(problem.getProblemTime());
                problems.add(problemDto);
            }
            resultCourse.setProblems(problems);

        }

        return Optional.ofNullable(resultCourse);
    }

    @CrossOrigin(origins = "*")
    @GetMapping(path = "/{courseId}/users", produces = "application/json")
    public List<UserDto> getAllUsersFromCourse(@PathVariable long courseId){
        List<String> users = coursesRepository.getAllUsersFromCourses(courseId);
        List<UserDto> model = new ArrayList<>();
        for (String user : users) {
            UserDto tempUser = new UserDto();
            String[] words = user.split(",");

            tempUser.setId(Long.valueOf(words[0]));
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
    public Course addCourse(@RequestBody Course course) {
        return coursesRepository.save(course);
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping(path = "/{id}")
    public void deleteCourse(@PathVariable long id) {
        coursesRepository.deleteById(id);
    }

    @CrossOrigin(origins = "*")
    @PostMapping(path = "/{courseId}/", consumes = "application/json")
    public void addUserToCourse(@PathVariable long courseId, @RequestParam(name = "userId") long userId) {
        try {
            coursesRepository.addUserToCourse(courseId, userId);
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException exception = (ConstraintViolationException) e.getCause();
                if (!exception.getConstraintName().equals("PRIMARY")) {
                    throw e;
                }
            } else {
                throw e;
            }
        }
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping(path = "/{courseId}/", consumes = "application/json")
    public void deleteUserFromCourse(@PathVariable long courseId, @RequestParam(name = "userId") long userId) {
        coursesRepository.deleteUserFromCourse(courseId, userId);
    }
}
