package org.example.deadlinescheduler.database.repo;

import org.example.deadlinescheduler.database.model.Deadline;
import org.example.deadlinescheduler.database.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
