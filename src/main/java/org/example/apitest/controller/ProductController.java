package org.example.apitest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.apitest.exception.ApiException;
import org.example.apitest.helper.ResponseBuilder;
import org.example.apitest.model.Category;
import org.example.apitest.model.Product;
import org.example.apitest.model.request.ProductRequest;
import org.example.apitest.service.CategoriesService;
import org.example.apitest.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {
    private final ProductService productService;

    @GetMapping("/list")
    public ResponseEntity<ResponseBuilder<Page<Product>>> getPageProducts(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "100") Integer size) {
        Page<Product> productsPage = productService.getPageProduct(page, size);
        return ResponseEntity.ok(ResponseBuilder.buildResponse(productsPage, "Get list Products successfully", HttpStatus.OK));
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseBuilder<Product>> createProduct(@Valid @RequestBody ProductRequest productsRequest) throws ApiException{
        Product products = productService.createProduct(productsRequest);
        return ResponseEntity.ok(ResponseBuilder.buildResponse(products, "Create Product successfully", HttpStatus.OK));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseBuilder<Product>> updateProduct(
            @PathVariable(name = "id") Long id,
            @RequestBody ProductRequest productRequest) throws ApiException {
        Product products = productService.updateProduct(id, productRequest);
        return ResponseEntity.ok(ResponseBuilder.buildResponse(products, "Update Product successfully", HttpStatus.OK));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseBuilder<Boolean>> deleteCategory(@PathVariable(name = "id") Long id) throws ApiException {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ResponseBuilder.buildResponse(true, "Delete Product successfully", HttpStatus.OK));
    }
    
}