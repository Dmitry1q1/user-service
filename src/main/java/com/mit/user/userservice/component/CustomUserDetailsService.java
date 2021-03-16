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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        if (Objects.nonNull(user = usersRepository.findByUsername(username))) {
            return this.usersRepository.findByUsername(username);
        } else {
            throw new UsernameNotFoundException("Username: " + username + " not found");
        }
    }
}
