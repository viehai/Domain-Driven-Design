package com.viehai.identity_service.identity.infrastructure.mapper;

import com.viehai.identity_service.identity.interfaces.dto.request.UserCreateRequest;
import com.viehai.identity_service.identity.interfaces.dto.request.UserUpdateRequest;
import com.viehai.identity_service.identity.interfaces.dto.response.UserResponse;
import com.viehai.identity_service.identity.domain.model.User;
import com.viehai.identity_service.identity.interfaces.dto.request.AddressRequest;
import com.viehai.identity_service.identity.domain.model.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "jobs", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "address.id", ignore = true)
    User toUser(UserCreateRequest request);

    UserResponse toUserResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "jobs", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "role", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);

    @Mapping(target = "id", ignore = true)
    Address toAddress(AddressRequest request);
}
