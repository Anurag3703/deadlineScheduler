package org.example.deadlinescheduler.database.service.implementation;

import lombok.AllArgsConstructor;

import org.example.deadlinescheduler.database.model.Deadline;
import org.example.deadlinescheduler.database.model.User;
import org.example.deadlinescheduler.database.repo.DeadlineRepository;
import org.example.deadlinescheduler.database.repo.UserRepository;
import org.example.deadlinescheduler.database.service.DeadlineService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Service
@AllArgsConstructor
public class DeadlineServiceImpl implements DeadlineService {

    private final DeadlineRepository deadlineRepository;
    private final JavaMailSender mailSender;
    @Qualifier("taskScheduler")
    private final TaskScheduler taskScheduler;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public Deadline addDeadline(Deadline deadline) {
        User user = userRepository.findByEmail(deadline.getUser().getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        deadline.setUser(user);
        Deadline newDeadline = deadlineRepository.save(deadline);
        scheduleReminderEmails(newDeadline);
        return newDeadline;
    }

    @Override
    public void scheduleReminderEmails(Deadline deadline) {

        if (deadline.getUser() == null || deadline.getUser().getEmail() == null) {
            throw new IllegalArgumentException("Deadline must have an associated user with email");
        }
        //LocalDate date = LocalDate.now();
        LocalDate startDate = deadline.getStartDate();
        LocalDate endDate = deadline.getEndDate();

        sendEmail(deadline.getUser().getEmail(),
                "New Deadline added for " + deadline.getUniversityName() , "\nNotes: " + deadline.getNotes());

        scheduleEmail(deadline, startDate,
                "Deadline starts for " + deadline.getUniversityName() + "\nNotes: " + deadline.getNotes());

        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        LocalDate middleDate = startDate.plusDays(daysBetween/2);
        scheduleEmail(deadline,middleDate,
                "You're Halfway to the deadline for " + deadline.getUniversityName() + "\nNotes: " + deadline.getNotes());

        LocalDate oneWeekBefore = endDate.minusWeeks(1);
        scheduleEmail(deadline,oneWeekBefore,
                "One week left for " + deadline.getUniversityName() +
                        "\nNotes: " + deadline.getNotes() +
                        "\nDeadline ends on: " + endDate);

    }

    @Override
    public void sendEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        try {
            mailSender.send(message);
            System.out.println("Email sent to " + toEmail + " with subject: " + subject);
        } catch (Exception e) {
            System.err.println("Failed to send email to " + toEmail);
        }


    }

    @Override
    public void scheduleEmail(Deadline deadline, LocalDate date, String subject) {
        ZonedDateTime zonedDateTime = date.atStartOfDay(ZoneId.systemDefault()); // Zone Time based on the device
        Instant scheduleInstant = zonedDateTime.toInstant(); // Universal time for threads scheduler

       if(scheduleInstant.isAfter(Instant.now())) {
           taskScheduler.schedule(() -> sendEmail(deadline.getUser().getEmail(), "Reminder: " + subject,"Notes: " + deadline.getNotes()), scheduleInstant);
       }else{
           sendEmail(deadline.getUser().getEmail(), "Reminder: " + subject,"Notes: " + deadline.getNotes() );
       }
    }

    @Override
    public List<Deadline> getDeadlines(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getDeadlines();
    }

    @Override
    @Transactional
    public void deleteDeadline(String userEmail, Long deadlineId ) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Find and remove the deadline from user's collection
        Deadline deadlineToDelete = user.getDeadlines()
                .stream()
                .filter(d -> d.getId().equals(deadlineId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Deadline not found"));

        user.getDeadlines().remove(deadlineToDelete);

        userRepository.save(user);

        deadlineRepository.delete(deadlineToDelete);


        deadlineRepository.flush();


    }


}
