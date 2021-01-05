package com.mit.user.userservice.model;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface ProblemRepository extends CrudRepository<Problem, Long> {

    @Query(value = "SELECT p.id, p.problem_name,p.problem_text, p.problem_time FROM course co " +
            "JOIN course_problems cp ON cp.course_course_id = co.course_id " +
            "JOIN problem p ON p.id = cp.problems_id " +
            "WHERE co.course_id = :courseId", nativeQuery = true)
    public List<Problem> getAllProblemsFromCourse(@Param("courseId") long courseId);

    @Query(value = "SELECT p.id, p.problem_name,p.problem_text, p.problem_time FROM course co " +
            "JOIN course_problems cp ON cp.course_course_id = co.course_id " +
            "JOIN problem p ON p.id = cp.problems_id " +
            "WHERE co.course_id = :courseId AND p.id = :problemId", nativeQuery = true)
    public Optional<Problem> getProblemById(@Param("courseId") long courseId, @Param("problemId") long problemId);

    @Modifying
    @Query(value = "INSERT INTO course_problems (course_course_id, problems_id) VALUES (:courseId,:problemId)", nativeQuery = true)
    @Transactional
    public void addProblem(@Param("courseId") long courseId, @Param("problemId") long problemId);

    @Modifying
    @Query(value = "DELETE FROM course_problems WHERE course_id =:courseId AND problems_id = :problemId", nativeQuery = true)
    @Transactional
    public void deleteProblemFromCourse(@Param("courseId") long courseId, @Param("problemId") long problemId);

    @Modifying
    @Query(value = "UPDATE problem SET problem_name = :problemName, problem_text = :problemText" +
            ", problem_time = :problemTime WHERE id = :problemId", nativeQuery = true)
    @Transactional
    public void updateProblem(@Param("problemId") long problemId, @Param("problemName") String problemName,
                              @Param("problemText") String problemText, @Param("problemTime") long problemTime);

}
