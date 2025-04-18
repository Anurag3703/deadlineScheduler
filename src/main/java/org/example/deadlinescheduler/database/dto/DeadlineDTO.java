package org.example.deadlinescheduler.database.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DeadlineDTO {
    private Long id;
    private String universityName;
    private String notes;
    private LocalDate startDate;
    private LocalDate endDate;
    private UserDTO userDTO;
}
