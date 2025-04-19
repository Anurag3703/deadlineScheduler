package org.example.deadlinescheduler.database.controller;

import lombok.AllArgsConstructor;

import org.example.deadlinescheduler.database.dto.DeadlineDTO;
import org.example.deadlinescheduler.database.dto.Service.DTOServiceImpl;
import org.example.deadlinescheduler.database.model.Deadline;

import org.example.deadlinescheduler.database.service.implementation.DeadlineServiceImpl;
import org.example.deadlinescheduler.database.service.implementation.UserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/deadlines")
@AllArgsConstructor

public class DeadlineController {
    private  DeadlineServiceImpl deadlineServiceImpl;
    private UserServiceImpl userServiceImpl;
    private DTOServiceImpl dtoServiceImpl;

    @GetMapping("/all/{email}")
    public ResponseEntity<?> getAllDeadlines(@PathVariable String email) {
        try{
            List<Deadline> deadlines = userServiceImpl.getAllDeadlines(email);
            List<DeadlineDTO> deadlineDTOs = deadlines.stream()
                    .map(dtoServiceImpl::convertToDto)
                    .toList();
            return ResponseEntity.ok(deadlineDTOs);
        }catch (Exception e){
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addDeadline(@RequestBody Deadline deadline) {
        try {
            Deadline newDeadline = deadlineServiceImpl.addDeadline(deadline);
            DeadlineDTO newDeadlineDTO = dtoServiceImpl.convertToDto(newDeadline);
            return ResponseEntity.ok(newDeadlineDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteDeadline(@RequestParam String email, @RequestParam Long id) {
        try{
            deadlineServiceImpl.deleteDeadline(email, id);
            return ResponseEntity.ok("Deadline deleted successfully.");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error deleting deadline: " + e.getMessage());
        }
    }

}
