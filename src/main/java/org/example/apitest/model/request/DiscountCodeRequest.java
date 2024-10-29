package org.example.apitest.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountCodeRequest {
    @NotNull
    @NotEmpty
    private String name;
    @NotNull
    @NotEmpty
    private String code;
    @NotNull
    @NotEmpty
    private String percentDiscount;
    @NotNull
    @NotEmpty
    private Boolean duration;
    @NotNull
    @NotEmpty
    private Long userId;
}
