package com.mit.user.userservice.repository;

import com.mit.user.userservice.model.Course;
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

    @Query(value = "SELECT co.course_id, co.course_name, co.course_description," +
            " co.course_duration, co.course_main_picture " +
            "FROM users_courses uc " +
            "JOIN course co ON co.course_id = uc.course_id WHERE uc.user_id = :userId", nativeQuery = true)
    List<Course> getUserCoursesById(@Param("userId") long userId);

    @Modifying
    @Query(value = "UPDATE course SET course_name = :courseName, course_description = :courseDescription," +
            " course_duration = :courseDuration WHERE course_id = :courseId", nativeQuery = true)
    @Transactional
    void updateCourseInfo(@Param("courseName") String courseName, @Param("courseDescription") String courseDescription,
                          @Param("courseDuration") String courseDuration, @Param("courseId") long courseId);

    @Modifying
    @Query(value = "UPDATE course SET course_main_picture = :course_main_picture" +
            " WHERE course_id = :courseId", nativeQuery = true)
    @Transactional
    void addMainPictureUrlToCourse(@Param("course_main_picture") String courseMainPicture, @Param("courseId") long courseId);

    @Query(value = "SELECT u.user_id, u.first_name, u.last_name, u.username, u.record_book_number" +
            " FROM user u JOIN users_courses uc ON" +
            " uc.user_id = u.user_id WHERE uc.course_id = :courseId", nativeQuery = true)
    List<String> getAllUsersFromCourses(@Param("courseId") long courseId);

    @Query(value = "SELECT * FROM course co WHERE co.course_id = :courseId", nativeQuery = true)
    Optional<Course> getCourseById(@Param("courseId") long courseId);

    @Query(value = "SELECT ca.user_id FROM course_authors ca WHERE ca.course_id = :courseId", nativeQuery = true)
    List<Long> getCourseAuthorsByCourseId(@Param("courseId") long courseId);

    @Modifying
    @Query(value = "DELETE FROM course_authors" +
            " WHERE user_id = :userId AND course_id = :courseId", nativeQuery = true)
    @Transactional
    void deleteCourseAuthorsByCourseIdAndUserId(@Param("courseId") long courseId,
                                                @Param("userId") long userId);

    @Modifying
    @Query(value = "INSERT INTO course_authors (user_id, course_id) VALUES (:userId, :courseId)", nativeQuery = true)
    @Transactional
    void addCourseAuthors(@Param("courseId") long courseId, @Param("userId") long userId);

}
