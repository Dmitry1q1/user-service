package com.mit.user.userservice.service;

import com.mit.user.userservice.model.User;
import com.mit.user.userservice.model.UserDto;
import com.mit.user.userservice.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {
    private static final String PASSWORD_COPIES_ARE_NOT_EQUALS = "Password copies are not equals";
    private static final String RECORD_BOOK_NUMBER_IS_NOT_UNIQUE = "Record book number is not unique";
    private static final String LOGIN_IS_EMPTY = "Login is empty";
    private static final String PASSWORD_IS_EMPTY = "Password is empty";
    private static final String USERNAME_OR_PASSWORD_IS_WRONG = "Username or Password is wrong";
    private static final String USERNAME_IS_NOT_UNIQUE = "Username is not unique";

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDto registerUser(UserDto user) {
        User entity = new User();
        entity.setUsername(user.getUserName());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        if (!user.getPassword().equals(user.getPasswordConfirm())) {
            user.setErrorDescription(PASSWORD_COPIES_ARE_NOT_EQUALS);
        }

        if (usersRepository.getUserByRecordBookNumber(user.getRecordBookNumber()) != null) {
            user.setErrorDescription(RECORD_BOOK_NUMBER_IS_NOT_UNIQUE);
        }
        if (usersRepository.findByUsername(user.getUserName()) != null) {
            user.setErrorDescription(USERNAME_IS_NOT_UNIQUE);
        }
        entity.setRecordBookNumber(user.getRecordBookNumber());
        entity.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getErrorDescription() == null) {
            usersRepository.save(entity);
            user.setId(entity.getId());
        }
        return user;
    }

    @Override
    public UserDto loginServer(UserDto user) {
        User entity = usersRepository.findByUsername(user.getUserName());
        if (entity == null) {
            user.setErrorDescription(USERNAME_OR_PASSWORD_IS_WRONG);
            return user;
        }
        if (user.getPassword() == null && user.getPassword().isEmpty()) {
            user.setErrorDescription(PASSWORD_IS_EMPTY);
            return user;
        }
        if (user.getUserName() == null && user.getUserName().isEmpty()) {
            user.setErrorDescription(LOGIN_IS_EMPTY);
            return user;
        }
        if (!passwordEncoder.matches(user.getPassword(), entity.getPassword())) {
            user.setErrorDescription(USERNAME_OR_PASSWORD_IS_WRONG);
            return user;
        }
        return user;
    }
}
