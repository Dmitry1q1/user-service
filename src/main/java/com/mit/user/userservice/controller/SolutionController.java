package com.mit.user.userservice.controller;

import com.mit.user.userservice.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/solution")
public class SolutionController {
    private final SolutionRepository solutionRepository;
    private final UsersRepository usersRepository;
    private final ProblemRepository problemRepository;

    public SolutionController(SolutionRepository solutionRepository, UsersRepository usersRepository, ProblemRepository problemRepository) {
        this.solutionRepository = solutionRepository;
        this.usersRepository = usersRepository;
        this.problemRepository = problemRepository;
    }

    @GetMapping(path = "/", produces = "application/json")
    public List<Solution> getAllSolutions() {
        return solutionRepository.getAllSolutions();
    }

    @PostMapping(path = "/", consumes = "application/json")
    public ResponseEntity<Solution> addSolution(@RequestBody Solution solution) {
        Optional<User> user = usersRepository.findById(solution.getUserId());
        if (user.isPresent()) {
            Optional<Problem> problem = problemRepository.findById(solution.getProblemId());
            if (problem.isPresent()) {
                LocalDateTime time = LocalDateTime.now();
                solutionRepository.addSolution(solution.getUserId(), solution.getProblemId(), time,
                        solution.getSolutionText(), solution.getSolutionStatus(), solution.getStatusDescription());
                Map<Object, Object> model = new HashMap<>();
                model.put("success", true);
                model.put("description", "Solution was successfully added");
                return new ResponseEntity(model, HttpStatus.OK);
            } else {
                Map<Object, Object> errorModel = new HashMap<>();
                errorModel.put("success", false);
                errorModel.put("errorDescription", "Problem with this id was not found");
                return new ResponseEntity(errorModel, HttpStatus.NOT_FOUND);
            }
        } else {
            Map<Object, Object> errorModel = new HashMap<>();
            errorModel.put("success", false);
            errorModel.put("errorDescription", "User with this id was not found");
            return new ResponseEntity(errorModel, HttpStatus.BAD_REQUEST);
        }
    }
}
