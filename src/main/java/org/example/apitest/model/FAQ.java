package org.example.apitest.model;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "faq")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FAQ {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;

    @Column(length = 1000)
    private String answer;
}
