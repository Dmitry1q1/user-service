package com.mit.user.userservice.controller;

import com.mit.user.userservice.model.*;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/courses/")
public class CoursesController {
    final private CoursesRepository coursesRepository;
    final private UsersRepository usersRepository;

    public CoursesController(CoursesRepository coursesRepository, UsersRepository usersRepository) {
        this.coursesRepository = coursesRepository;
        this.usersRepository = usersRepository;
    }

    @GetMapping(path = "", produces = "application/json")
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

    @GetMapping(path = "{courseId}", produces = "application/json")
    public Optional<CourseDto> getCourseById(@PathVariable long courseId) {
        Optional<Course> course = coursesRepository.findById(courseId);
        CourseDto resultCourse = null;
        if (course.isPresent()) {
            resultCourse = new CourseDto();
            resultCourse.setCourseName(course.get().getCourseName());
            resultCourse.setId(course.get().getId());
            List<Problem> problems = new ArrayList<>();
            for (Problem problem : course.get().getProblems()) {
                Problem problemDto = new Problem();
                problemDto.setId(problem.getId());
                problemDto.setProblemName(problem.getProblemName());
                problemDto.setProblemName(problem.getProblemName());
                problemDto.setProblemTime(problem.getProblemTime());
                problems.add(problemDto);
            }
            resultCourse.setProblems(problems);
            List<User> users = new ArrayList<>();
            for (User user : course.get().getUsers()) {
                User userDto = new User();
                userDto.setId(user.getId());
                userDto.setFirstName(user.getFirstName());
                userDto.setLastName(user.getLastName());
                userDto.setRecordBookNumber(user.getRecordBookNumber());
                userDto.setUsername(user.getUsername());
                userDto.setRoles(user.getRoles());
                users.add(userDto);
            }
            //resultCourse.setUsers(course.get().getUsers());

        }

        return Optional.ofNullable(resultCourse);
    }

    @GetMapping(path = "{courseId}/all", produces = "application/json")
    public List<String> getAllProblemsFromCourse(@PathVariable long courseId) {
        return coursesRepository.getAllProblemsFromCourse(courseId);
    }

    @PostMapping(path = "add", consumes = "application/json", produces = "application/json")
    public Course addCourse(@RequestBody Course course) {
        return coursesRepository.save(course);
    }

    @DeleteMapping(path = "delete/{id}/")
    public void deleteCourse(@PathVariable long id) {
        coursesRepository.deleteById(id);
    }

    @PostMapping(path = "{courseId}/", consumes = "application/json")
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

    @DeleteMapping(path = "{courseId}/")
    public void deleteUserFromCourse(@PathVariable long courseId, @RequestParam(name = "userId") long userId) {
        coursesRepository.deleteUserFromCourse(courseId, userId);
    }
}
