package org.example.apitest.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "size")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Size {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
}