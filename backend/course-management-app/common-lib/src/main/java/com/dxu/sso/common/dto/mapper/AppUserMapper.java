package com.dxu.sso.common.dto.mapper;

import com.dxu.sso.common.dto.user.AppUserDto;
import com.dxu.sso.common.model.user.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AppUserMapper {

    AppUserMapper INSTANCE = Mappers.getMapper(AppUserMapper.class);

    AppUserDto toDto(AppUser appUser);

    AppUser toEntity(AppUserDto appUserDto);
}

