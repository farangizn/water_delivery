package org.example.waterdelivery.repo;

import org.example.waterdelivery.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
}