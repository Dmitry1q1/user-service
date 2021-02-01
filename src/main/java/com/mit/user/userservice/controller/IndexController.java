package com.mit.user.userservice.controller;

import com.mit.user.userservice.component.JwtTokenProvider;
import com.mit.user.userservice.model.User;
import com.mit.user.userservice.model.UserDto;
import com.mit.user.userservice.model.UsersRepository;
import com.mit.user.userservice.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class IndexController {
    private final UsersRepository usersRepository;
    private final IUserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    public IndexController(UsersRepository usersRepository,
                           IUserService userService) {
        this.usersRepository = usersRepository;
        this.userService = userService;
    }

    @CrossOrigin(origins = "*")
    @PostMapping(path = "/login")
    public ResponseEntity login(@RequestBody UserDto data) {
        try {
            String username = data.getUserName();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,
                    data.getPassword()));

            User user;
            if (Objects.isNull(user = usersRepository.findByUsername(username))) {
                throw new UsernameNotFoundException("Username: " + username + " not found");
            }
            String token = jwtTokenProvider.createToken(username, user.getRoles(), user.getId());

            usersRepository.sentToken(user.getId(), token);
            Map<Object, Object> model = new HashMap<>();
            model.put("username", username);
            model.put("userid", user.getId());
            model.put("token", token);

            return ok(model);
        } catch (AuthenticationException e) {
            Map<Object, Object> errorModel = new HashMap<>();
            errorModel.put("success", false);
            errorModel.put("errorDescription", "Invalid username/password supplied");
            return new ResponseEntity<>(errorModel, HttpStatus.FORBIDDEN);
        }
    }

    @CrossOrigin(origins = "*")
    @PostMapping(path = "/logout")
    public ResponseEntity logout(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        String token = jwtTokenProvider.resolveToken(request);
        jwtTokenProvider.getUsername(token);
        usersRepository.deleteToken(token);

//        if (session != null) {
//            session.invalidate();
//        }
        Map<Object, Object> model = new HashMap<>();
        model.put("success", true);
        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    @CrossOrigin(origins = "*")
    @PostMapping(path = "/registration")
    public ResponseEntity addUser(@RequestBody UserDto user) {
        userService.registerUser(user);
        if (user.getErrorDescription() != null && !user.getErrorDescription().isEmpty()) {
            Map<Object, Object> errorModel = new HashMap<>();
            errorModel.put("success", false);
            errorModel.put("errorDescription", user.getErrorDescription());
            return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
        }
        Map<Object, Object> model = new HashMap<>();
        usersRepository.addRoleToUser(user.getId(), 2);
        model.put("success", true);
        model.put("description", "Successful registration");
        return new ResponseEntity<>(model, HttpStatus.OK);

    }
}
