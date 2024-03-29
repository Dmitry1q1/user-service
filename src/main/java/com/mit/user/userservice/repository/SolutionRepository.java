package com.mit.user.userservice.repository;

import com.mit.user.userservice.model.Solution;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SolutionRepository extends CrudRepository<Solution, Long> {

    @Query(value = "SELECT s.* FROM solution s", nativeQuery = true)
    List<Solution> getAllSolutions();

    @Query(value = "SELECT status_description FROM solution" +
            " WHERE user_id = :user_id AND id = :id", nativeQuery = true)
    String getSolutionDescription(@Param("user_id") long user_id,
                                  @Param("id") long id);
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO solution (user_id, problems_id," +
            " solution_date, solution_text, solution_status, status_description, programming_language_id)\n" +
            "VALUES (:user_id,:problems_id," +
            " :solution_date, :solution_text, :solution_status, :status_description, :programming_language_id)", nativeQuery = true)
    void addSolution(@Param("user_id") long user_id, @Param("problems_id") long problems_id,
                     @Param("solution_date") LocalDateTime solution_date, @Param("solution_text") String solution_text,
                     @Param("solution_status") String solution_status, @Param("status_description") String status_description,
                     @Param("programming_language_id") String programming_language_id);
}
