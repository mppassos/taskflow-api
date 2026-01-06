package com.taskflow.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.taskflow.dto.user.UserResponse;
import com.taskflow.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "fullName", source = "user", qualifiedByName = "mapFullName")
    UserResponse toResponse(User user);

    @Named("mapFullName")
    default String mapFullName(User user) {
        return user != null ? user.getFullName() : null;
    }
}
