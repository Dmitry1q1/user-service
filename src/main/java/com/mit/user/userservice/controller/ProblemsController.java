package com.mit.user.userservice.controller;

import com.mit.user.userservice.model.Problem;
import com.mit.user.userservice.repository.ProblemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/problems")
public class ProblemsController {
    private final ProblemRepository problemRepository;

    public ProblemsController(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    @CrossOrigin(origins = "*")
    @GetMapping(path = "/", produces = "application/json")
    public Iterable<Problem> getAllProblems() {
        return problemRepository.findAll();
    }

    @CrossOrigin(origins = "*")
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

    @CrossOrigin(origins = "*")
    @GetMapping(path = "/{problemId}", produces = "application/json")
    public ResponseEntity getProblemById(@PathVariable long problemId) {
        Optional<Problem> problem = problemRepository.findById(problemId);
        if (problem.isPresent()) {
            return new ResponseEntity(problem, HttpStatus.OK);
        }
        Map<Object, Object> errorModel = new HashMap<>();
        errorModel.put("success", false);
        errorModel.put("errorDescription", "Problem with this id was not found");
        return new ResponseEntity<>(errorModel, HttpStatus.NOT_FOUND);
    }

    @CrossOrigin(origins = "*")
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
            Map<Object, Object> errorModel = new HashMap<>();
            errorModel.put("success", false);
            errorModel.put("errorDescription", errorDescription);
            return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
        }
        Optional<Problem> problemTemp = problemRepository.findById(problemId);
        if (problemTemp.isPresent()) {
            problemRepository.updateProblem(problemId, problem.getProblemName(),
                    problem.getProblemText(), problem.getProblemTime());
            Map<Object, Object> model = new HashMap<>();
            model.put("success", true);
            model.put("description", "Problem was successfully updated");
            return new ResponseEntity<>(model, HttpStatus.OK);
        }
        Map<Object, Object> errorModel = new HashMap<>();
        errorModel.put("success", false);
        errorModel.put("errorDescription", "Problem with this id was not found");
        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping(path = "/{problemId}")
    public ResponseEntity deleteProblem(@PathVariable long problemId) {
        Optional<Problem> problemTemp = problemRepository.findById(problemId);
        if (problemTemp.isPresent()) {
            problemRepository.deleteProblemFromAllCourses(problemId);
            problemRepository.deleteById(problemId);
            Map<Object, Object> model = new HashMap<>();
            model.put("success", true);
            model.put("description", "Problem was successfully deleted");
            return new ResponseEntity<>(model, HttpStatus.OK);
        }
        Map<Object, Object> errorModel = new HashMap<>();
        errorModel.put("success", false);
        errorModel.put("errorDescription", "Problem with this id was not found");
        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }

}
