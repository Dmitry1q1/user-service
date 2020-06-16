package com.mit.user.userservice.controller;

import com.mit.user.userservice.model.*;
import com.mit.user.userservice.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
public class IndexController {
    private final VisitsRepository visitsRepository;
    private final UsersRepository usersRepository;
    private final IUserService userService;


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

    @PostMapping(path = "/login")
    public ModelAndView logServer(@ModelAttribute("user") UserDto user) {
        userService.loginServer(user);
        if (user.getErrorDescription() != null && !user.getErrorDescription().isEmpty()) {
            return new ModelAndView("login", "errorDescription", user.getErrorDescription());
        }
        Map<String, String> model = new HashMap<>();
        model.put("name", user.getUserName());
        return new ModelAndView("index", model);
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
