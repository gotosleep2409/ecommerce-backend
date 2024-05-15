package org.example.apitest.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.apitest.model.User;
import java.util.Date;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillDetailDTO {
    private Long id;
    private String code;
    private Date date;
    private String status;
    private String note;
    private String paymentMethod;
    private String paymentStatus;
    private String shoppingAddress;
    private String phoneNumber;
    private String email;
    private String totalAmount;
    private String name;
    private User user;
    private List<BillDetailProductDTO> products;
}
