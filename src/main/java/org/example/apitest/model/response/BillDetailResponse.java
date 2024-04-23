package org.example.apitest.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.example.apitest.model.dto.SizeQuantityDTO;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillDetailResponse {
    @NotNull
    @NotEmpty
    private Long productId;
    @NotNull
    @NotEmpty
    private String name;
    private int quantity;
    private List<SizeQuantityDTO> sizes;
    private String price;
}
