package org.example.deadlinescheduler.database.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Deadline> deadlines = new ArrayList<>();


}
