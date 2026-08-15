package com.github.mohrezal.api.domains.privilege.mapper;

import com.github.mohrezal.api.domains.privilege.dto.PermissionSummary;
import com.github.mohrezal.api.domains.privilege.model.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PermissionMapper {

    PermissionSummary toSummary(Permission permission);
}
