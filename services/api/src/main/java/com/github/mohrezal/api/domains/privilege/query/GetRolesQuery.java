package com.github.mohrezal.api.domains.privilege.query;

import com.github.mohrezal.api.domains.privilege.dto.RoleSummary;
import com.github.mohrezal.api.domains.privilege.mapper.RoleMapper;
import com.github.mohrezal.api.domains.privilege.query.param.GetRolesQueryParams;
import com.github.mohrezal.api.domains.privilege.repository.RoleRepository;
import com.github.mohrezal.api.shared.interfaces.Query;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetRolesQuery implements Query<GetRolesQueryParams, List<RoleSummary>> {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Transactional(readOnly = true)
    @Override
    public List<RoleSummary> execute(GetRolesQueryParams params) {
        return roleRepository.findAllWithPermissions().stream().map(roleMapper::toSummary).toList();
    }
}
