package com.mit.user.userservice.controller;

import com.mit.user.userservice.component.JwtTokenProvider;
import com.mit.user.userservice.model.*;
import com.mit.user.userservice.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.management.remote.JMXAuthenticator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class IndexController {
    private final VisitsRepository visitsRepository;
    private final UsersRepository usersRepository;
    private final IUserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;


    public IndexController(VisitsRepository visitsRepository, UsersRepository usersRepository,
                           IUserService userService) {
        this.visitsRepository = visitsRepository;
        this.usersRepository = usersRepository;
        this.userService = userService;
    }

    @GetMapping("/registration")
    public ModelAndView registration() {
        return new ModelAndView("registration", "errorDescription", "");
    }

    @GetMapping("/")
    public ModelAndView index() {
//        Map<String, String> model = new HashMap<>();
//        model.put("name", "Dmitry");
//        Visit visit = new Visit();
//        visit.setDescription(String.format("Visited at %s", LocalDateTime.now()));
//        visitsRepository.save(visit);
//        return new ModelAndView("index", model);
        return new ModelAndView("login", "errorDescription", "");
    }

//    @PostMapping(path = "/login")
//    public ModelAndView logServer(@ModelAttribute("user") UserDto user) {
//        userService.loginServer(user);
//        if (user.getErrorDescription() != null && !user.getErrorDescription().isEmpty()) {
//            return new ModelAndView("login", "errorDescription", user.getErrorDescription());
//        }
//        Map<String, String> model = new HashMap<>();
//        model.put("name", user.getUserName());
//        return new ModelAndView("index", model);
//    }
    @PostMapping(path = "/login")
    public ResponseEntity login(@RequestBody UserDto data) {
        try {
            String username = data.getUserName();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword()));
//            User user = usersRepository.findByUsername(username)
//                    .orElseThrow(() -> new UsernameNotFoundException("Username " + username + "not found"));
            User user;
            if (Objects.isNull(user = usersRepository.findByUsername(username))) {
                throw new UsernameNotFoundException("Username: " + username + " not found");
            }
            String token = jwtTokenProvider.createToken(username, user.getRoles());

            Map<Object, Object> model = new HashMap<>();
            model.put("username", username);
            model.put("token", token);
            return ok(model);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied");
        }
//        return ok(user);
    }

    @PostMapping(path = "/registration")
//    @RequestMapping(value = "/registration", method = RequestMethod.POST,
//            produces = {"application/json", "application/xml"}, consumes={"application/x-www-form-urlencoded;charset=UTF-8"})
    public ModelAndView addUser(@ModelAttribute("user") UserDto user) {
        userService.registerUser(user);
        if (user.getErrorDescription() != null && !user.getErrorDescription().isEmpty()) {
            return new ModelAndView("registration", "errorDescription", user.getErrorDescription());

        }
        return new ModelAndView("login", "errorDescription", "");
    }
}
