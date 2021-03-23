package com.mit.user.userservice.component;


import com.mit.user.userservice.model.User;
import com.mit.user.userservice.repository.UsersRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    private UsersRepository usersRepository;

    public CustomUserDetailsService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String recordBookNumber) throws UsernameNotFoundException {
        User user;
        if (Objects.nonNull(user = usersRepository.getUserByRecordBookNumber(recordBookNumber))) {
            return this.usersRepository.getUserByRecordBookNumber(recordBookNumber);
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }
}
