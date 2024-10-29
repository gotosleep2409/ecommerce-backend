package org.example.apitest.controller;

import lombok.RequiredArgsConstructor;
import org.example.apitest.exception.ApiException;
import org.example.apitest.helper.ResponseBuilder;
import org.example.apitest.model.DiscountCode;
import org.example.apitest.model.request.DiscountCodeRequest;
import org.example.apitest.service.DiscountCodeService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/discountCode")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DiscountCodeController {
    private final DiscountCodeService discountCodeService;

    @GetMapping("/list")
    public ResponseEntity<ResponseBuilder<Page<DiscountCode>>> getPageDiscountCode(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "100") Integer size) {
        Page<DiscountCode> DiscountCodePage = discountCodeService.getPageDiscountCode(page, size);
        return ResponseEntity.ok(ResponseBuilder.buildResponse(DiscountCodePage, "Get list discount code successfully", HttpStatus.OK));
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseBuilder<DiscountCode>> createCategory(@RequestBody DiscountCodeRequest categoriesRequest) {
        DiscountCode discountCode = discountCodeService.createDiscountCode(categoriesRequest);
        return ResponseEntity.ok(ResponseBuilder.buildResponse(discountCode, "Create discount code successfully", HttpStatus.OK));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseBuilder<DiscountCode>> updateApeCategory(
            @PathVariable(name = "id") Long id,
            @RequestBody DiscountCodeRequest categoriesToUpdate) throws ApiException {
        DiscountCode discountCode = discountCodeService.updateDiscountCode(id, categoriesToUpdate);
        return ResponseEntity.ok(ResponseBuilder.buildResponse(discountCode, "Update discount Code successfully", HttpStatus.OK));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseBuilder<Boolean>> deleteCategory(@PathVariable(name = "id") Long id) throws ApiException {
        discountCodeService.deleteDiscountCode(id);
        return ResponseEntity.ok(ResponseBuilder.buildResponse(true, "Delete discount Code successfully", HttpStatus.OK));
    }

    @PostMapping("/check")
    public ResponseEntity<?> checkDiscountCode(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        Optional<DiscountCode> discountCodeOpt = discountCodeService.checkDiscountCode(code);

        if (discountCodeOpt.isPresent()) {
            DiscountCode discountCode = discountCodeOpt.get();
            return ResponseEntity.ok(discountCode);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mã giảm giá không tồn tại.");
        }
    }
}
