package org.example.apitest.model.response;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillResponse {
    @NotNull
    @NotEmpty
    private String address;
    @NotNull
    @NotEmpty
    private String email;
    @NotNull
    @NotEmpty
    private String name;
    private Long userId;
    private String notes;
    @NotNull
    @NotEmpty
    private String paymentMethod;
    @NotNull
    @NotEmpty
    private String phone;
    @NotNull
    @NotEmpty
    private List<BillDetailResponse> billDetails;
    @NotNull
    @NotEmpty
    private String totalPrice;
    @NotNull
    @NotEmpty
    private String codeDiscount;
    @NotNull
    @NotEmpty
    private String discountedAmount;
}