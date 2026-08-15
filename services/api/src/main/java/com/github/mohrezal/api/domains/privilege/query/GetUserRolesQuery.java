package com.github.mohrezal.api.domains.privilege.query;

import com.github.mohrezal.api.domains.privilege.dto.RoleSummary;
import com.github.mohrezal.api.domains.privilege.mapper.RoleMapper;
import com.github.mohrezal.api.domains.privilege.query.param.GetUserRolesQueryParams;
import com.github.mohrezal.api.domains.privilege.repository.UserRoleRepository;
import com.github.mohrezal.api.domains.users.exceptions.types.UserNotFoundException;
import com.github.mohrezal.api.domains.users.repositories.UserRepository;
import com.github.mohrezal.api.shared.interfaces.Query;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetUserRolesQuery implements Query<GetUserRolesQueryParams, List<RoleSummary>> {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleMapper roleMapper;

    @Transactional(readOnly = true)
    @Override
    public List<RoleSummary> execute(GetUserRolesQueryParams params) {
        if (!userRepository.existsById(params.userId())) {
            throw new UserNotFoundException();
        }

        return userRoleRepository.findAllByUser_Id(params.userId()).stream()
                .map(userRole -> roleMapper.toSummary(userRole.getRole()))
                .toList();
    }
}
