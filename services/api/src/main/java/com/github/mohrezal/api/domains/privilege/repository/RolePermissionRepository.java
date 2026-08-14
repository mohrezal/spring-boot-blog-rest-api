package com.github.mohrezal.api.domains.privilege.repository;

import com.github.mohrezal.api.domains.privilege.model.Permission;
import com.github.mohrezal.api.domains.privilege.model.Role;
import com.github.mohrezal.api.domains.privilege.model.RolePermission;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {

    List<RolePermission> findAllByRole(Role role);

    boolean existsByRoleAndPermission(Role role, Permission permission);
}
