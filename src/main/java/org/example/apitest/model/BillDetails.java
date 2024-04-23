package org.example.apitest.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "bill_details")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class BillDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "unit_price")
    private String unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id")
    private Bills bill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "size_id")
    private Size size;
}