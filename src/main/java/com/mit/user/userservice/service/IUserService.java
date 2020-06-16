package com.mit.user.userservice.service;

import com.mit.user.userservice.model.User;
import com.mit.user.userservice.model.UserDto;

public interface IUserService {
    UserDto registerUser(UserDto user);
    UserDto loginServer(UserDto user);
}
