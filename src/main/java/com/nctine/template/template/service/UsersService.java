package com.nctine.template.template.service;

import com.nctine.template.template.entity.UsersEntity;
import com.nctine.template.template.model.request.RegisterUserRequest;

public interface UsersService {

    UsersEntity create(RegisterUserRequest user) throws Exception;

}
