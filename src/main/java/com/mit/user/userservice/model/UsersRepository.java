package com.mit.user.userservice.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersRepository extends CrudRepository<User, Long> {
    @Query(value = "SELECT u.* FROM user u WHERE u.record_book_number = :record_book_number", nativeQuery = true)
    public List<User> getUserByRecordBookNumber(@Param("record_book_number") String recordBookNumber);


    public List<User> findUserByLastName(@Param("last_name") String lastName);
    public User findByUsername(@Param("username")String username);

    @Query(value = "SELECT co.course_name FROM users_courses uc " +
            "JOIN course co ON co.course_id = uc.course_id WHERE uc.user_id = :userId", nativeQuery = true)
    public List<String> getUserCourseById(@Param("userId") long userId);


}
