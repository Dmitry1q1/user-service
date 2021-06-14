package com.mit.user.userservice.repository;

import com.mit.user.userservice.model.Test;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface TestRepository extends CrudRepository<Test, Long> {

    @Query(value = "SELECT MAX(order_number) from test WHERE problem_id = :problemId", nativeQuery = true)
    Long getTestMaxOrderNumberForProblem(@Param("problemId") long problemId);

    @Query(value = "SELECT * from test WHERE problem_id = :problemId", nativeQuery = true)
    List<Test> getAllTestsForProblem(@Param("problemId") long problemId);

    @Modifying
    @Query(value = "INSERT INTO test (problem_id, order_number, input_text, output_text)" +
            "VALUES(:problemId, :orderNumber, :inputText, :outputText)", nativeQuery = true)
    @Transactional
    void addTest(@Param("problemId") long problemId, @Param("orderNumber") long orderNumber,
                 @Param("inputText") String inputText, @Param("outputText") String outputText);
}
