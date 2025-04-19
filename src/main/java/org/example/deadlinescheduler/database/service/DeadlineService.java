package org.example.deadlinescheduler.database.service;

import org.example.deadlinescheduler.database.model.Deadline;

import java.time.LocalDate;
import java.util.List;

public interface DeadlineService {
    Deadline addDeadline(Deadline deadline);
    void scheduleReminderEmails(Deadline deadline);
    void sendEmail(String toEmail ,String subject, String body);
    void scheduleEmail(Deadline deadline, LocalDate date, String subject);
    List<Deadline> getDeadlines(String email);
    void deleteDeadline(String userEmail, Long deadlineId );
}
