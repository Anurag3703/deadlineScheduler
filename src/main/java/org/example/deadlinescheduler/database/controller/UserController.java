package org.example.deadlinescheduler.database.controller;

import lombok.AllArgsConstructor;
import org.example.deadlinescheduler.database.dto.Service.DTOServiceImpl;
import org.example.deadlinescheduler.database.dto.UserDTO;
import org.example.deadlinescheduler.database.model.User;
import org.example.deadlinescheduler.database.service.implementation.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {
    private final UserServiceImpl userServiceImpl;
    private DTOServiceImpl dtoServiceImpl;

    @PostMapping("/add")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        try{
            User savedUser = userServiceImpl.saveUser(user);
            UserDTO userDTO = dtoServiceImpl.convertToDto(savedUser);
            return ResponseEntity.ok(userDTO);

        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
