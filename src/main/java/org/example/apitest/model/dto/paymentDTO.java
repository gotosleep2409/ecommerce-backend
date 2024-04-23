package org.example.apitest.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class paymentDTO {
    private String paymentUrl;
    private String status;
    private String Message;
}
