package org.example.apitest.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;
@Entity
@Table(name = "bills")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Bills {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name= "code")
    private String code;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "bill_date", updatable = false)
    @CreatedDate
    private Date date;

    @Column(name= "bill_status")
    private String status;

    @Column(name= "note")
    private String note;

    @Column(name= "payment_method")
    private String paymentMethod;

    @Column(name = "status")
    private String paymentStatus;

    @Column(name = "shopping_address")
    private String shoppingAddress;

    @Column(name = "phone")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "total_amount")
    private String totalAmount;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}