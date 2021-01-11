package com.mit.user.userservice.model;

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
    public List<Solution> getAllSolutions();

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO test.solution (user_id, problems_id," +
            " solution_date, solution_text, solution_status, status_description)\n" +
            "VALUES (:user_id,:problems_id," +
            " :solution_date, :solution_text, :solution_status, :status_description)", nativeQuery = true)
    public void addSolution(@Param("user_id") long user_id, @Param("problems_id") long problems_id,
                            @Param("solution_date") LocalDateTime solution_date, @Param("solution_text") String solution_text,
                            @Param("solution_status") String solution_status, @Param("status_description") String status_description);
}
