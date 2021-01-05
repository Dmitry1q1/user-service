package com.mit.user.userservice.controller;

import com.mit.user.userservice.model.Course;
import com.mit.user.userservice.model.CoursesRepository;
import com.mit.user.userservice.model.Problem;
import com.mit.user.userservice.model.ProblemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/courses/{courseId}/problems")
public class CourseProblemsController {
    private final ProblemRepository problemRepository;
    private final CoursesRepository coursesRepository;

    public CourseProblemsController(ProblemRepository problemRepository, CoursesRepository coursesRepository) {
        this.problemRepository = problemRepository;
        this.coursesRepository = coursesRepository;
    }

    @GetMapping(path = "/", produces = "application/json")
    public List<Problem> getAllProblemsFromCourse(@PathVariable long courseId) {
        return problemRepository.getAllProblemsFromCourse(courseId);
    }

    @GetMapping(path = "/{problemId}", produces = "application/json")
    public ResponseEntity getProblemById(@PathVariable long courseId, @PathVariable long problemId) {
        Optional<Problem> problem = problemRepository.getProblemById(courseId, problemId);
        if (problem.isPresent()) {
            return new ResponseEntity<>(problem, HttpStatus.OK);
        }
        return new ResponseEntity<>("Course with this id was not found or there is no such task in it", HttpStatus.NOT_FOUND);
    }

    @PostMapping(path = "/", consumes = "application/json")
    public ResponseEntity addProblemToCourse(@PathVariable long courseId,
                                             @RequestParam(name = "problemId") long problemId) {
        Optional<Problem> problem = problemRepository.findById(problemId);
        if (problem.isPresent()) {
            Optional<Course> course = coursesRepository.getCourseById(courseId);
            if (course.isPresent()) {
                Optional<Problem> existsProblem = problemRepository.getProblemById(courseId,problemId);
                if (!existsProblem.isPresent()) {
                    problemRepository.addProblem(courseId, problemId);
                    return new ResponseEntity("Problem was successfully added", HttpStatus.OK);
                } else {
                    return new ResponseEntity("We already have this problem on this course", HttpStatus.BAD_REQUEST);
                }
            }
            return new ResponseEntity<>("Course with this id was not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Problem with this id was not found", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(path = "/")
    public ResponseEntity deleteProblemFromCourse(@PathVariable long courseId,
                                                  @RequestParam(name = "problemId") long problemId) {
        Optional<Problem> problemTemp = problemRepository.findById(problemId);
        if (problemTemp.isPresent()) {
            problemRepository.deleteProblemFromCourse(courseId, problemId);
            return new ResponseEntity<>("Problem was successfully deleted", HttpStatus.OK);
        }
        return new ResponseEntity<>("Problem with this id was not found", HttpStatus.BAD_REQUEST);
    }
}
