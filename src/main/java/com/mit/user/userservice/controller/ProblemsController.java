package com.mit.user.userservice.controller;

import com.mit.user.userservice.model.Problem;
import com.mit.user.userservice.model.ProblemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/problems")
public class ProblemsController {
    private final ProblemRepository problemRepository;

    public ProblemsController(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    @GetMapping(path = "/", produces = "application/json")
    public Iterable<Problem> getAllProblems() {
        return problemRepository.findAll();
    }

    @PostMapping(path = "/", consumes = "application/json")
    public ResponseEntity addProblem(@RequestBody Problem problem) {
        StringBuilder errorDescription = new StringBuilder();
        if (problem.getProblemName().isEmpty()) {
            errorDescription.append("Problem's name must be not empty\n");
        }
        if (problem.getProblemText().isEmpty()) {
            errorDescription.append("Problem's text must be not empty\n");
        }
        if (problem.getProblemTime() < 0) {
            errorDescription.append("Problem's time must be > 0");
        }
        if (errorDescription.length() > 0) {
            return new ResponseEntity<>(errorDescription, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(problemRepository.save(problem), HttpStatus.OK);
    }

    @GetMapping(path = "/{problemId}", produces = "application/json")
    public ResponseEntity getProblemById(@PathVariable long problemId) {
        Optional<Problem> problem = problemRepository.findById(problemId);
        if(problem.isPresent()){
            return new ResponseEntity(problem,HttpStatus.OK);
        }
        return new ResponseEntity<>("Problem with this id was not found",HttpStatus.NOT_FOUND);
    }


    @PutMapping(path = "/{problemId}")
    public ResponseEntity updateProblem(@PathVariable long problemId, @RequestBody Problem problem) {
        StringBuilder errorDescription = new StringBuilder();
        if (problem.getProblemName().isEmpty()) {
            errorDescription.append("Problem's name must be not empty\n");
        }
        if (problem.getProblemText().isEmpty()) {
            errorDescription.append("Problem's text must be not empty\n");
        }
        if (problem.getProblemTime() < 0) {
            errorDescription.append("Problem's time must be > 0");
        }
        if (errorDescription.length() > 0) {
            return new ResponseEntity<>(errorDescription, HttpStatus.BAD_REQUEST);
        }
        Optional<Problem> problemTemp = problemRepository.findById(problemId);
        if(problemTemp.isPresent()){
            problemRepository.updateProblem(problemId, problem.getProblemName(),
                    problem.getProblemText(), problem.getProblemTime());
            return new ResponseEntity<>("Problem was successfully updated", HttpStatus.OK);
        }
        return new ResponseEntity<>("Problem with this id was not found", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(path = "/{problemId}")
    public ResponseEntity deleteProblem(@PathVariable long problemId) {
        Optional<Problem> problemTemp = problemRepository.findById(problemId);
        if(problemTemp.isPresent()) {
            problemRepository.deleteById(problemId);
            return new ResponseEntity<>("Problem was successfully deleted", HttpStatus.OK);
        }
        return new ResponseEntity<>("Problem with this id was not found", HttpStatus.BAD_REQUEST);
    }

}
