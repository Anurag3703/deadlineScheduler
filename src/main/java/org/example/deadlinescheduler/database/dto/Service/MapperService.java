package org.example.deadlinescheduler.database.dto.Service;

import org.example.deadlinescheduler.database.dto.DeadlineDTO;
import org.example.deadlinescheduler.database.dto.UserDTO;
import org.example.deadlinescheduler.database.model.Deadline;
import org.example.deadlinescheduler.database.model.User;

public interface MapperService {
    DeadlineDTO convertToDto(Deadline deadline);
    UserDTO convertToDto(User user);
}

