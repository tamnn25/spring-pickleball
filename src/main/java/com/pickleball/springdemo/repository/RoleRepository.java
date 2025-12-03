package com.pickleball.springdemo.repository;

import com.pickleball.springdemo.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    // Find a role by its name
    Optional<Role> findByName(String name);
}
