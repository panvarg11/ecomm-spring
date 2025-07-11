package com.mpantoja.sbecommerce.repositories;

import com.mpantoja.sbecommerce.model.AppRole;
import com.mpantoja.sbecommerce.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(AppRole appRole);
}
