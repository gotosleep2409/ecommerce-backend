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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;


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
            @RequestParam(required = false, defaultValue = "100") Integer size) {
        Page<Bills> OrderList = billService.getPageBills(page, size);
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
}
