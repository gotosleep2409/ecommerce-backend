package org.example.apitest.controller;

import lombok.RequiredArgsConstructor;
import org.example.apitest.exception.ApiException;
import org.example.apitest.helper.ResponseBuilder;
import org.example.apitest.model.Bills;
import org.example.apitest.model.dto.BillDetailDTO;
import org.example.apitest.model.dto.paymentDTO;
import org.example.apitest.model.request.BillsRequest;
import org.example.apitest.model.response.BillResponse;
import org.example.apitest.service.BillsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BillController {
    @Autowired
    private BillsService billService;

    @PostMapping("/create")
    public ResponseEntity<Bills> createBill(@RequestBody BillResponse billResponse) {
        Bills createdBill = billService.createdBill(billResponse);
        return new ResponseEntity<>(createdBill, HttpStatus.CREATED);
    }

    @PostMapping("/vnpay")
    public ResponseEntity<paymentDTO> getPay(@RequestBody BillResponse billResponse) throws UnsupportedEncodingException {
        if (billResponse == null) {
            paymentDTO payment = new paymentDTO();
            payment.setMessage("Fail to get VNPay API");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payment);
        }
        else {
            paymentDTO payment = billService.vnPay(billResponse);
            return ResponseEntity.status(HttpStatus.OK).body(payment);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<ResponseBuilder<Page<Bills>>> getPageOrder(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "paymentMethod", required = false, defaultValue = "") String paymentMethod,
            @RequestParam(value = "paymentStatus", required = false, defaultValue = "") String paymentStatus,
            @RequestParam(value = "status", required = false, defaultValue = "") String status,
            @RequestParam(value = "code", required = false, defaultValue = "") String code) {
        Page<Bills> OrderList = billService.getPageBills(page, size, paymentMethod, paymentStatus, status, code);
        return ResponseEntity.ok(ResponseBuilder.buildResponse(OrderList, "Get list order successfully", HttpStatus.OK));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillDetailDTO> getBillDetailsByBillId(@PathVariable Long id) {
        BillDetailDTO billDetails = billService.getBillDetails(id);
        if (billDetails == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(billDetails);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseBuilder<Boolean>> deleteCategory(@PathVariable(name = "id") Long id) throws ApiException {
        billService.deleteBill(id);
        return ResponseEntity.ok(ResponseBuilder.buildResponse(true, "Delete category successfully", HttpStatus.OK));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseBuilder<Bills>> updateApeCategory(
            @PathVariable(name = "id") Long id,
            @RequestBody BillsRequest billToUpdate) throws ApiException {
        Bills bills = billService.updateBills(id, billToUpdate);
        return ResponseEntity.ok(ResponseBuilder.buildResponse(bills, "Update categories successfully", HttpStatus.OK));
    }

    @GetMapping("/billByUserId/{id}")
    public ResponseEntity<ResponseBuilder<Page<Bills>>> getBillByUserId(
            @PathVariable(name = "id") Long id,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        Page<Bills> BillByUserId = billService.getPageBillsByID(page, size, id);
        return ResponseEntity.ok(ResponseBuilder.buildResponse(BillByUserId, "Get Bill By User Id order successfully", HttpStatus.OK));
    }

    @GetMapping("/chart-data") 
    public ResponseEntity<ResponseBuilder<Map<String, Object>>> getChartData(@RequestParam String period) {
        if ("month".equalsIgnoreCase(period)) {
            Map<String, Object> data = billService.getLast12MonthsStatistics();
            return ResponseEntity.ok(ResponseBuilder.buildResponse(data, "Get data successfully", HttpStatus.OK));
        }else if ("day".equalsIgnoreCase(period)) {
            Map<String, Object> data = billService.getLastWeekStatistics();
            return ResponseEntity.ok(ResponseBuilder.buildResponse(data, "Get data successfully", HttpStatus.OK));
        }
        return ResponseEntity.ok(ResponseBuilder.buildResponse(null, "Get data successfully", HttpStatus.BAD_REQUEST));
    }

    @GetMapping("/chart-turnover-data")
    public ResponseEntity<ResponseBuilder<Map<String, Object>>> getChartTurnoverData(@RequestParam String period) {
        if ("Month".equalsIgnoreCase(period)) {
            Map<String, Object> data = billService.getLast12MonthsTurnover();
            return ResponseEntity.ok(ResponseBuilder.buildResponse(data, "Get data successfully", HttpStatus.OK));
        }else if ("day".equalsIgnoreCase(period)) {
            Map<String, Object> data = billService.getLastWeekTurnover();
            return ResponseEntity.ok(ResponseBuilder.buildResponse(data, "Get data successfully", HttpStatus.OK));
        }
        return ResponseEntity.ok(ResponseBuilder.buildResponse(null, "Get data successfully", HttpStatus.BAD_REQUEST));
    }

    @GetMapping("/chart-top10-products")
    public ResponseEntity<ResponseBuilder<Map<String, List<?>>>> getTop10BestSellingProducts() {
        Map<String, List<?>> data = billService.getTop10BestSellingProducts();
        return ResponseEntity.ok(ResponseBuilder.buildResponse(data, "Get data successfully", HttpStatus.OK));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportBillExcel(@RequestParam("billId") Long billId) {
        byte[] excelData = billService.generateExcelFile(billId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "bill_" + billId + ".xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }
}
