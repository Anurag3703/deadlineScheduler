package org.example.deadlinescheduler.database.dto.Service;

import lombok.Data;
import org.example.deadlinescheduler.database.dto.DeadlineDTO;
import org.example.deadlinescheduler.database.dto.UserDTO;
import org.example.deadlinescheduler.database.model.Deadline;
import org.example.deadlinescheduler.database.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DTOServiceImpl implements MapperService {
    @Override
    public DeadlineDTO convertToDto(Deadline deadline) {
        DeadlineDTO dto = new DeadlineDTO();
        dto.setId(deadline.getId());
        dto.setUniversityName(deadline.getUniversityName());
        dto.setNotes(deadline.getNotes());
        dto.setStartDate(deadline.getStartDate());
        dto.setEndDate(deadline.getEndDate());


        UserDTO userDTO = new UserDTO();
        userDTO.setId(deadline.getUser().getId());
        userDTO.setEmail(deadline.getUser().getEmail());


        dto.setUserDTO(userDTO);
        return dto;
    }

    @Override
    public UserDTO convertToDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());

        // Convert deadlines but don't set user in them
        if (user.getDeadlines() != null) {
            List<DeadlineDTO> deadlineDTOs = user.getDeadlines().stream()
                    .map(deadline -> {
                        DeadlineDTO deadlineDTO = new DeadlineDTO();
                        deadlineDTO.setId(deadline.getId());
                        deadlineDTO.setUniversityName(deadline.getUniversityName());
                        deadlineDTO.setNotes(deadline.getNotes());
                        deadlineDTO.setStartDate(deadline.getStartDate());
                        deadlineDTO.setEndDate(deadline.getEndDate());
                        // Don't set userDTO here
                        return deadlineDTO;
                    })
                    .collect(Collectors.toList());
            dto.setDeadlineDtoList(deadlineDTOs);
        }
        return dto;
    }

}
