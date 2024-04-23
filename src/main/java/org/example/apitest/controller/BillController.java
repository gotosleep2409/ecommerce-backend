package org.example.apitest.controller;

import lombok.RequiredArgsConstructor;
import org.example.apitest.model.Bills;
import org.example.apitest.model.dto.paymentDTO;
import org.example.apitest.model.response.BillResponse;
import org.example.apitest.service.BillsService;
import org.springframework.beans.factory.annotation.Autowired;
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
}
