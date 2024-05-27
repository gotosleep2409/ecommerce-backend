package org.example.apitest.model.response;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.apitest.model.Category;
import org.example.apitest.model.Comments;
import org.example.apitest.model.Product;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    @NotNull
    @NotEmpty
    private Long id;
    @NotNull
    @NotEmpty
    private String name;
    @NotNull
    @NotEmpty
    private String description;
    @NotNull
    @NotEmpty
    private String detail;
    private String creator;
    @NotNull
    @NotEmpty
    private String imageUrl;
    @NotNull
    @NotEmpty
    private String price;
    private String priceSale;
    @NotNull
    @NotEmpty
    private String quantity;
    @NotNull
    private List<Category> categories;
    @NotNull
    private Map<String, Integer> sizeQuantityMap;
    private List<Comments> comments;
    private List<Product> relatedTo;
}

