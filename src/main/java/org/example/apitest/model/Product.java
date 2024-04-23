package org.example.apitest.model;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "product")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "creator")
    private String creator;

    @Column(name = "description")
    private String description;

    @Column(name = "detail")
    private String detail;

    @Column(name = "imageUrl")
    private String imageUrl;

    @Column(name = "price")
    private String price;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "priceSale")
    private String priceSale;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinTable(
            name = "category_products",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<Category> categories;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductSize> productSizes;

    public Product(String name, String creator, String description, String detail, String imageUrl, String price, String priceSale, List<Category> categories) {
        this.name = name;
        this.creator = creator;
        this.description = description;
        this.detail = detail;
        this.imageUrl = imageUrl;
        this.price = price;
        this.priceSale = priceSale;
        this.categories = categories;
    }
}
