package org.example.apitest.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
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
    private List<Long> categories;
    @NotNull
    private Map<String, Integer> sizeQuantityMap;
    private Boolean featured;
}

