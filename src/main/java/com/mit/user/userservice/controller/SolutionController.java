package com.mit.user.userservice.controller;

import com.mit.user.userservice.component.JwtTokenProvider;
import com.mit.user.userservice.config.SolutionStatus;
import com.mit.user.userservice.model.Problem;
import com.mit.user.userservice.model.Solution;
import com.mit.user.userservice.model.User;
import com.mit.user.userservice.repository.ProblemRepository;
import com.mit.user.userservice.repository.SolutionRepository;
import com.mit.user.userservice.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.mit.user.userservice.config.SolutionStatus.READY_TO_COMPILE;

@RestController
@RequestMapping("/solution")
public class SolutionController {
    private final SolutionRepository solutionRepository;
    private final UsersRepository usersRepository;
    private final ProblemRepository problemRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    public SolutionController(SolutionRepository solutionRepository, UsersRepository usersRepository, ProblemRepository problemRepository) {
        this.solutionRepository = solutionRepository;
        this.usersRepository = usersRepository;
        this.problemRepository = problemRepository;
    }

    @CrossOrigin(origins = "*")
    @GetMapping(path = "/", produces = "application/json")
    public List<Solution> getAllSolutions() {
        return solutionRepository.getAllSolutions();
    }

    @CrossOrigin(origins = "*")
    @PostMapping(path = "/{courseId}/{problemId}/", consumes = "application/json")
    public ResponseEntity addSolution(HttpServletRequest request, @RequestBody String solutionText,
                                      @PathVariable long problemId, @RequestParam Long programmingLanguage) {
        String token = jwtTokenProvider.resolveToken(request);
        Long userId = jwtTokenProvider.getUserId(token);

        Map<Object, Object> errorModel = new HashMap<>();
        errorModel.put("success", false);

        if (!jwtTokenProvider.validateUsersData(request, userId)) {
            errorModel.put("errorDescription", "Wrong id. Forbidden");
            return new ResponseEntity<>(errorModel, HttpStatus.FORBIDDEN);
        }

        if (solutionText.isEmpty()) {
            errorModel.put("errorDescription", "solutionText not found");
            return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
        }
        Optional<User> user = usersRepository.findById(userId);
        if (user.isPresent()) {
            Optional<Problem> problem = problemRepository.findById(problemId);
            if (problem.isPresent()) {

                Solution solution = new Solution();
                solution.setUserId(userId);
                solution.setProblemId(problemId);
                solution.setSolutionDate(LocalDateTime.now());
                solution.setSolutionText(solutionText);
                solution.setSolutionStatus("NOT OK");
                solution.setProgrammingLanguageId(programmingLanguage);
                solution.setStatusDescription(READY_TO_COMPILE.value);

                return new ResponseEntity<>(solutionRepository.save(solution), HttpStatus.OK);

            } else {
                errorModel.put("errorDescription", "Problem not found");
                return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
            }
        } else {
            errorModel.put("errorDescription", "User not found");
            return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(origins = "*")
    @GetMapping(path = "/solution-description/{solutionId}")
    public ResponseEntity getSolutionDescription(HttpServletRequest request, @PathVariable long solutionId) {
        String token = jwtTokenProvider.resolveToken(request);

        Long userId = jwtTokenProvider.getUserId(token);
        Map<Object, Object> model = new HashMap<>();
        model.put("success", true);
        model.put("status", solutionRepository.getSolutionDescription(userId, solutionId));
        return new ResponseEntity<>(model, HttpStatus.OK);
    }
}
