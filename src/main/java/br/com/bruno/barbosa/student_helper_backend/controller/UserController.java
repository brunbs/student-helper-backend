package br.com.bruno.barbosa.student_helper_backend.controller;

import br.com.bruno.barbosa.student_helper_backend.domain.request.CreateUserRequest;
import br.com.bruno.barbosa.student_helper_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/users")
    public void createUser(@RequestBody CreateUserRequest createUserRequest) {
        userService.createUser(createUserRequest);
    }
}