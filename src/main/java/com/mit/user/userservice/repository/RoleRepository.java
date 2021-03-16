package com.mit.user.userservice.repository;

import com.mit.user.userservice.model.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
    Role findByName(@Param("name")String name);
}
