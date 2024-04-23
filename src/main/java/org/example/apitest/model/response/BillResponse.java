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
    private String address;
    private String email;
    private String name;
    private Long userId;
    private String notes;
    private String paymentMethod;
    private String phone;
    private List<BillDetailResponse> billDetails;
    private String totalPrice;
}