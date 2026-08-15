package com.github.mohrezal.api.domains.privilege.query;

import com.github.mohrezal.api.domains.privilege.dto.PermissionSummary;
import com.github.mohrezal.api.domains.privilege.exception.types.PermissionNotFoundException;
import com.github.mohrezal.api.domains.privilege.mapper.PermissionMapper;
import com.github.mohrezal.api.domains.privilege.query.param.GetPermissionQueryParams;
import com.github.mohrezal.api.domains.privilege.repository.PermissionRepository;
import com.github.mohrezal.api.shared.interfaces.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetPermissionQuery implements Query<GetPermissionQueryParams, PermissionSummary> {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Transactional(readOnly = true)
    @Override
    public PermissionSummary execute(GetPermissionQueryParams params) {
        return permissionRepository
                .findById(params.permissionId())
                .map(permissionMapper::toSummary)
                .orElseThrow(PermissionNotFoundException::new);
    }
}
