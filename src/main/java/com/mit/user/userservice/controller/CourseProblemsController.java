package com.mit.user.userservice.controller;

import com.mit.user.userservice.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/courses/{courseId}/problems")
public class CourseProblemsController {
    private final ProblemRepository problemRepository;
    private final CoursesRepository coursesRepository;
    private final UsersRepository usersRepository;
    private final SolutionRepository solutionRepository;

    public CourseProblemsController(ProblemRepository problemRepository,
                                    CoursesRepository coursesRepository, UsersRepository usersRepository,
                                    SolutionRepository solutionRepository) {
        this.problemRepository = problemRepository;
        this.coursesRepository = coursesRepository;
        this.usersRepository = usersRepository;
        this.solutionRepository = solutionRepository;
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
        Map<Object, Object> errorModel = new HashMap<>();
        errorModel.put("success", false);
        errorModel.put("errorDescription", "Course with this id was not found or there is no such task in it");
        return new ResponseEntity<>(errorModel, HttpStatus.NOT_FOUND);
    }

    @PostMapping(path = "/{problemId}/solution-file/", consumes = "multipart/form-data")
    public ResponseEntity addSolutionOnProblemAsFile(@RequestParam("file") MultipartFile file, @PathVariable long courseId,
                                                     @PathVariable long problemId, @RequestParam(name = "userId") long userId) {
        Map<Object, Object> errorModel = new HashMap<>();
        errorModel.put("success", false);

        if (file.isEmpty()) {
            errorModel.put("errorDescription", "Empty file");
            return new ResponseEntity(errorModel, HttpStatus.BAD_REQUEST);
        }
        Optional<User> user = usersRepository.findById(userId);
        if (user.isPresent()) {
            Optional<Problem> problem = problemRepository.findById(problemId);
            if (problem.isPresent()) {

                try {
                    byte[] text = file.getBytes();
                    String solutionText = new String(text);

                    Solution solution = new Solution(userId, problemId, LocalDateTime.now(),
                            solutionText, "OK", "OK");

                    return new ResponseEntity(solutionRepository.save(solution), HttpStatus.OK);
                } catch (IOException e) {
                    errorModel.put("errorDescription", e.getMessage());
                    return new ResponseEntity(errorModel, HttpStatus.CONFLICT);
                }

            } else {
                errorModel.put("errorDescription", "Problem with this id was not found");
                return new ResponseEntity(errorModel, HttpStatus.BAD_REQUEST);
            }
        } else {
            errorModel.put("errorDescription", "User with this id was not found");
            return new ResponseEntity(errorModel, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/{problemId}/solution-text/", consumes = "application/json")
    public ResponseEntity addSolutionOnProblemAsText(@RequestBody String solutionText, @PathVariable long courseId,
                                                     @PathVariable long problemId, @RequestParam(name = "userId") long userId) {
        Map<Object, Object> errorModel = new HashMap<>();
        errorModel.put("success", false);

        if (solutionText.isEmpty()) {
            errorModel.put("errorDescription", "solutionText not found");
            return new ResponseEntity(errorModel, HttpStatus.BAD_REQUEST);
        }
        Optional<User> user = usersRepository.findById(userId);
        if (user.isPresent()) {
            Optional<Problem> problem = problemRepository.findById(problemId);
            if (problem.isPresent()) {


                Solution solution = new Solution(userId, problemId, LocalDateTime.now(),
                        solutionText, "OK", "OK");

                return new ResponseEntity(solutionRepository.save(solution), HttpStatus.OK);

            } else {
                errorModel.put("errorDescription", "Problem with this id was not found");
                return new ResponseEntity(errorModel, HttpStatus.BAD_REQUEST);
            }
        } else {
            errorModel.put("errorDescription", "User with this id was not found");
            return new ResponseEntity(errorModel, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/", consumes = "application/json")
    public ResponseEntity addProblemToCourse(@PathVariable long courseId,
                                             @RequestParam(name = "problemId") long problemId) {
        Optional<Problem> problem = problemRepository.findById(problemId);
        if (problem.isPresent()) {
            Optional<Course> course = coursesRepository.getCourseById(courseId);
            if (course.isPresent()) {
                Optional<Problem> existsProblem = problemRepository.getProblemById(courseId, problemId);
                if (!existsProblem.isPresent()) {
                    problemRepository.addProblem(courseId, problemId);
                    Map<Object, Object> model = new HashMap<>();
                    model.put("success", true);
                    model.put("description", "Problem was successfully added");
                    return new ResponseEntity(model, HttpStatus.OK);
                } else {
                    Map<Object, Object> errorModel = new HashMap<>();
                    errorModel.put("success", false);
                    errorModel.put("errorDescription", "We already have this problem on this course");
                    return new ResponseEntity(errorModel, HttpStatus.BAD_REQUEST);
                }
            }
            Map<Object, Object> errorModel = new HashMap<>();
            errorModel.put("success", false);
            errorModel.put("errorDescription", "Course with this id was not found");
            return new ResponseEntity<>(errorModel, HttpStatus.NOT_FOUND);
        }
        Map<Object, Object> errorModel = new HashMap<>();
        errorModel.put("success", false);
        errorModel.put("errorDescription", "Problem with this id was not found");
        return new ResponseEntity<>(errorModel, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(path = "/")
    public ResponseEntity deleteProblemFromCourse(@PathVariable long courseId,
                                                  @RequestParam(name = "problemId") long problemId) {
        Optional<Problem> problemTemp = problemRepository.findById(problemId);
        if (problemTemp.isPresent()) {
            problemRepository.deleteProblemFromCourse(courseId, problemId);
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
