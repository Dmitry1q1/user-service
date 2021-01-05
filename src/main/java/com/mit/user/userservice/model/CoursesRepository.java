package com.mit.user.userservice.model;

import org.hibernate.annotations.SQLInsert;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface CoursesRepository extends CrudRepository<Course, Long> {
    @Modifying
    @Query(value = "INSERT INTO users_courses (course_id, user_id) VALUES (:courseId,:userId)", nativeQuery = true)
    @Transactional
    public void addUserToCourse(@Param("courseId") long courseId, @Param("userId") long userId);

    @Modifying
    @Query(value = "DELETE FROM users_courses WHERE course_id =:courseId AND user_id = :userId", nativeQuery = true)
    @Transactional
    public void deleteUserFromCourse(@Param("courseId") long courseId, @Param("userId") long userId);

    @Query(value = "SELECT pm.problem_name, pm.problem_text FROM problem pm JOIN course_problems cp ON cp.problems_id = pm.id" +
            " WHERE cp.course_course_id = :courseId", nativeQuery = true)
    public List<String> getAllProblemsFromCourse(@Param("courseId") long courseId);

    @Query(value = "SELECT u.user_id, u.first_name, u.last_name, u.username, u.record_book_number" +
            " FROM user u JOIN users_courses uc ON" +
            " uc.user_id = u.user_id WHERE uc.course_id = :courseId", nativeQuery = true)
    public List<String> getAllUsersFromCourses(@Param("courseId") long courseId);

    @Query(value="SELECT * FROM course co WHERE co.course_id = :courseId", nativeQuery = true)
    public Optional<Course> getCourseById(@Param("courseId") long courseId);

}
