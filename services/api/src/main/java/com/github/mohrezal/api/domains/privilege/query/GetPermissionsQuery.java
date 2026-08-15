package com.github.mohrezal.api.domains.privilege.query;

import com.github.mohrezal.api.domains.privilege.dto.PermissionSummary;
import com.github.mohrezal.api.domains.privilege.mapper.PermissionMapper;
import com.github.mohrezal.api.domains.privilege.query.param.GetPermissionsQueryParams;
import com.github.mohrezal.api.domains.privilege.repository.PermissionRepository;
import com.github.mohrezal.api.shared.interfaces.Query;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetPermissionsQuery
        implements Query<GetPermissionsQueryParams, List<PermissionSummary>> {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Transactional(readOnly = true)
    @Override
    public List<PermissionSummary> execute(GetPermissionsQueryParams params) {
        return permissionRepository.findAllByOrderByServiceAscKeyAsc().stream()
                .map(permissionMapper::toSummary)
                .toList();
    }
}
