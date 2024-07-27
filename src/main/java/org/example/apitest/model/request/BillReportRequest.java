package org.example.apitest.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillReportRequest {
    private String label;
    private List<Integer> data;
}
