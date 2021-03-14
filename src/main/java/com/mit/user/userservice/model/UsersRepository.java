package com.mit.user.userservice.model;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends CrudRepository<User, Long> {
    @Query(value = "SELECT u.* FROM user u WHERE u.record_book_number = :record_book_number", nativeQuery = true)
    List<User> getUserByRecordBookNumber(@Param("record_book_number") String recordBookNumber);


    List<User> findUserByLastName(@Param("last_name") String lastName);

    User findByUsername(@Param("username") String username);

    @Query(value = "SELECT u.* FROM user u WHERE u.user_id = :userId", nativeQuery = true)
    Optional<User> findUserById(@Param("userId") long userId);

    @Modifying
    @Query(value = "INSERT INTO user_roles (user_user_id, roles_id) VALUES (:userId,:roleId)", nativeQuery = true)
    @Transactional
    void addRoleToUser(@Param("userId") long userId, @Param("roleId") long roleId);

    @Modifying
    @Query(value="UPDATE user SET user_picture = :user_picture WHERE user_id = :userId",nativeQuery = true)
    @Transactional
    void addPictureUrlToUser(@Param("user_picture") String userPicture, @Param("userId") long userId);

    @Modifying
    @Query(value = "INSERT INTO token_storage (user_id, token) VALUES (:userId,:token)", nativeQuery = true)
    @Transactional
    void sentToken(@Param("userId") long userId, @Param("token") String token);

    @Query(value = "SELECT t_s.token FROM token_storage t_s WHERE token = :token", nativeQuery = true)
    String getToken(@Param("token") String token);

    @Modifying
    @Query(value = "DELETE FROM token_storage WHERE token_storage.token = :token", nativeQuery = true)
    @Transactional
    void deleteToken(@Param("token") String token);
}
