package com.github.mohrezal.api.domains.privilege.query;

import com.github.mohrezal.api.domains.privilege.dto.RoleSummary;
import com.github.mohrezal.api.domains.privilege.exception.types.RoleNotFoundException;
import com.github.mohrezal.api.domains.privilege.mapper.RoleMapper;
import com.github.mohrezal.api.domains.privilege.query.param.GetRoleQueryParams;
import com.github.mohrezal.api.domains.privilege.repository.RoleRepository;
import com.github.mohrezal.api.shared.interfaces.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetRoleQuery implements Query<GetRoleQueryParams, RoleSummary> {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Transactional(readOnly = true)
    @Override
    public RoleSummary execute(GetRoleQueryParams params) {
        return roleRepository
                .findByIdWithPermissions(params.roleId())
                .map(roleMapper::toSummary)
                .orElseThrow(RoleNotFoundException::new);
    }
}
