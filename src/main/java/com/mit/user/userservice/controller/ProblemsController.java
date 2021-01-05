package com.mit.user.userservice.controller;

import com.mit.user.userservice.model.Problem;
import com.mit.user.userservice.model.ProblemRepository;
import com.mit.user.userservice.model.User;
import com.mit.user.userservice.model.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/courses/{courseId}/problems")
public class ProblemsController {
    private final ProblemRepository problemRepository;

    public ProblemsController(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
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

//    @GetMapping(path = "", produces = "application/json")
//    public Iterable<Problem> getAllProblems() {
//        return problemRepository.findAll();
//    }

//    @PostMapping(path = "", consumes = "application/json")
//    public Problem addProblem(@RequestBody Problem problem) {
//        return problemRepository.save(problem);
//    }
//
    @PostMapping(path = "/", consumes = "application/json")
    public void addProblemToCourse(@PathVariable long courseId,
                                   @RequestParam(name = "problemId") long problemId) {
        problemRepository.addProblem(courseId, problemId);
    }
//
//    @GetMapping(path = "{problemId}", produces = "application/json")
//    public Optional<Problem> getProblemById(@PathVariable long problemId) {
//        return problemRepository.findById(problemId);
//    }
//
//    @PutMapping(path = "{problemId}")
//    public void updateProblem(@PathVariable long problemId, @RequestBody Problem problem) {
//        Long id = problem.getId();
//        problemRepository.updateProblem(problem.getId(), problem.getProblemName(), problem.getProblemText(), problem.getProblemTime());
//    }
//
//    @DeleteMapping(path = "{problemId}")
//    public void deleteProblem(@PathVariable long problemId) {
//        problemRepository.deleteById(problemId);
//    }

    @DeleteMapping(path = "/")
    public void deleteProblemFromCourse(@PathVariable long courseId,
                                        @RequestParam(name = "problemId") long problemId) {
        problemRepository.deleteProblemFromCourse(courseId, problemId);
    }
}
