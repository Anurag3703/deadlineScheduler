package org.example.deadlinescheduler.database.service;

import org.example.deadlinescheduler.database.model.Deadline;
import org.example.deadlinescheduler.database.model.User;

import java.util.List;

public interface UserService {

    List<Deadline> getAllDeadlines(String username);
    User getUser(String username);
    User saveUser(User user);
}
