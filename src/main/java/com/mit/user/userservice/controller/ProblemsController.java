package com.mit.user.userservice.controller;

import com.mit.user.userservice.component.JwtTokenProvider;
import com.mit.user.userservice.model.Problem;
import com.mit.user.userservice.repository.ProblemRepository;
import com.mit.user.userservice.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/problems")
public class ProblemsController {
    private final ProblemRepository problemRepository;
    private final TestRepository testRepository;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Value("${test.problems-test-url}")
    private String problemsPath;

    public ProblemsController(ProblemRepository problemRepository, TestRepository testRepository) {
        this.problemRepository = problemRepository;
        this.testRepository = testRepository;
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
    @PutMapping(path = "/")
    public ResponseEntity updateProblem(@RequestBody Problem problem) {
        long problemId = problem.getId();
        Optional<Problem> problemToUpdate = problemRepository.findById(problemId);
        if (problemToUpdate.isPresent()) {
            if (!problemToUpdate.get().equals(problem)) {
                if (problem.getProblemName() != null) {
                    problemToUpdate.get().setProblemName(problem.getProblemName());
                }
                if (problem.getProblemText() != null) {
                    problemToUpdate.get().setProblemText(problem.getProblemText());
                }
                if (problem.getProblemTime() != null) {
                    problemToUpdate.get().setProblemTime(problem.getProblemTime());
                }
            }

            problemRepository.updateProblem(problemId, problemToUpdate.get().getProblemName(),
                    problemToUpdate.get().getProblemText(), problemToUpdate.get().getProblemTime());
            Map<Object, Object> model = new HashMap<>();
            model.put("success", true);
            model.put("description", "Problem was successfully updated");
            return new ResponseEntity<>(model, HttpStatus.OK);
        }

        Map<Object, Object> errorModel = new HashMap<>();
        errorModel.put("success", false);
        errorModel.put("errorDescription", "Problem not found");
        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }

    @CrossOrigin(origins = "*")
    @PostMapping(path = "/{problemId}/add-tests/", consumes = "multipart/form-data")
    public ResponseEntity addTestsOnProblem(HttpServletRequest request, @RequestParam("input") MultipartFile input,
                                            @RequestParam("output") MultipartFile output, @PathVariable long problemId) {
        String token = jwtTokenProvider.resolveToken(request);
        Long userId = jwtTokenProvider.getUserId(token);

        Map<Object, Object> errorModel = new HashMap<>();
        errorModel.put("success", false);
        if (!jwtTokenProvider.validateUsersData(request, userId)) {
            errorModel.put("errorDescription", "Wrong id. Forbidden");
            return new ResponseEntity<>(errorModel, HttpStatus.FORBIDDEN);
        }

        if (input.isEmpty()) {
            errorModel.put("errorDescription", "Empty input file");
            return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
        }
        if (output.isEmpty()) {
            errorModel.put("errorDescription", "Empty output file");
            return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
        }

        Optional<Problem> problem = problemRepository.findById(problemId);
        if (problem.isPresent()) {

            try {
                Map<Object, Object> model = new HashMap<>();
                model.put("success", true);
                byte[] inputText = input.getBytes();
                byte[] outputText = output.getBytes();

                long orderNumber = 1;
                try {
                    orderNumber = testRepository.getTestMaxOrderNumberForProblem(problemId) + 1;
                } catch (NullPointerException e) {
                    System.out.println(e.getMessage());
                }

                new File(problemsPath).mkdir();
                new File(problemsPath + problemId).mkdir();
                File inputFile = new File(problemsPath + problemId + "/input" + orderNumber + ".txt");
                File outputFile = new File(problemsPath + problemId + "/output" + orderNumber + ".txt");
                OutputStream inputFileOut = new FileOutputStream(inputFile);
                OutputStream outputFileOut = new FileOutputStream(outputFile);

                testRepository.addTest(problemId, orderNumber,
                        new String(inputText, StandardCharsets.UTF_8), new String(outputText, StandardCharsets.UTF_8));
                inputFileOut.write(inputText);
                inputFileOut.close();
                outputFileOut.write(outputText);
                outputFileOut.close();

                return new ResponseEntity<>(model, HttpStatus.OK);
            } catch (IOException e) {
                errorModel.put("errorDescription", e.getMessage());
                return new ResponseEntity<>(errorModel, HttpStatus.CONFLICT);
            }

        } else {
            errorModel.put("errorDescription", "Problem not found");
            return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
        }
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
