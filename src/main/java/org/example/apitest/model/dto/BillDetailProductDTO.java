package org.example.apitest.model.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillDetailProductDTO {
    private Long productId;
    private String productName;
    private List<SizeQuantityDTO> sizeQuantity;
    private boolean reviewed;
}
