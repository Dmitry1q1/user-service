package com.mit.user.userservice.controller;

import com.mit.user.userservice.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
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
                return new ResponseEntity("Solution was successfully added", HttpStatus.OK);
            } else {
                return new ResponseEntity("Problem with this id was not found", HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity("User with this id wasn't found", HttpStatus.BAD_REQUEST);
        }
    }
}
