package org.example.apitest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.apitest.exception.ApiException;
import org.example.apitest.helper.ResponseBuilder;
import org.example.apitest.model.Comments;
import org.example.apitest.model.Product;
import org.example.apitest.model.ProductSize;
import org.example.apitest.model.Size;
import org.example.apitest.model.request.ProductRequest;
import org.example.apitest.model.response.ProductResponse;
import org.example.apitest.repository.CommentsRepository;
import org.example.apitest.repository.ProductSizeRepository;
import org.example.apitest.repository.SizeRepository;
import org.example.apitest.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {
    private final ProductService productService;
    private final SizeRepository sizeRepository;
    private final ProductSizeRepository productSizeRepository;
    private final CommentsRepository commentsRepository;

    /*@GetMapping("/list")
    public ResponseEntity<ResponseBuilder<Page<Product>>> getPageProducts(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "100") Integer size) {
        Page<Product> productsPage = productService.getPageProduct(page, size);
        return ResponseEntity.ok(ResponseBuilder.buildResponse(productsPage, "Get list Products successfully", HttpStatus.OK));
    }*/

    @GetMapping("/list")
    public ResponseEntity<ResponseBuilder<Page<ProductResponse>>> getPageProducts(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId) {
        Page<Product> productsPage = productService.getPageProduct(page, size, categoryId);

        Page<ProductResponse> productRequestsPage = productsPage.map(product -> {
            ProductResponse productResponse = new ProductResponse();
            productResponse.setId(product.getId());
            productResponse.setName(product.getName());
            productResponse.setCreator(product.getCreator());
            productResponse.setDescription(product.getDescription());
            productResponse.setDetail(product.getDetail());
            productResponse.setImageUrl(product.getImageUrl());
            productResponse.setPrice(product.getPrice());
            productResponse.setPriceSale(product.getPriceSale());
            productResponse.setCategories(product.getCategories());

            Map<String, Integer> sizeQuantityMap = new HashMap<>();
            for (ProductSize productSize : product.getProductSizes()) {
                sizeQuantityMap.put(productSize.getSize().getName(), productSize.getQuantity());
            }

            productResponse.setSizeQuantityMap(sizeQuantityMap);
            return productResponse;
        });
        return ResponseEntity.ok(ResponseBuilder.buildResponse(productRequestsPage, "Get list Products successfully", HttpStatus.OK));
    }


    @PostMapping("/create")
    public ResponseEntity<ResponseBuilder<Product>> createProduct(@Valid @RequestBody ProductRequest productsRequest) throws ApiException{
        Product products = productService.createProduct(productsRequest,productsRequest.getSizeQuantityMap());
        return ResponseEntity.ok(ResponseBuilder.buildResponse(products, "Create Product successfully", HttpStatus.OK));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseBuilder<Product>> updateProduct(
            @PathVariable(name = "id") Long id,
            @RequestBody ProductRequest productRequest) throws ApiException {
        Product products = productService.updateProduct(id, productRequest);
        List<ProductSize> productSizes = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : productRequest.getSizeQuantityMap().entrySet()) {
            String sizeName = entry.getKey();
            Integer quantity = entry.getValue();
            Size size = sizeRepository.getSizeByName(sizeName);
            if (size != null) {
                ProductSize productSize = new ProductSize();
                productSize.setSize(size);
                productSize.setQuantity(quantity);
                productSizes.add(productSize);
            }
        }
        products.setProductSizes(productSizes);
        return ResponseEntity.ok(ResponseBuilder.buildResponse(products, "Update Product successfully", HttpStatus.OK));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseBuilder<Boolean>> deleteCategory(@PathVariable(name = "id") Long id) throws ApiException {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ResponseBuilder.buildResponse(true, "Delete Product successfully", HttpStatus.OK));
    }

    /*@GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductWithProductSizesById(
            @PathVariable Long productId) {
        List<Comments> comments = commentsRepository.findCommentByProductId(productId);
        Product product = productService.findProductWithProductSizesById(productId);
        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(productId);
        productResponse.setName(product.getName());
        productResponse.setCreator(product.getCreator());
        productResponse.setDescription(product.getDescription());
        productResponse.setDetail(product.getDetail());
        productResponse.setImageUrl(product.getImageUrl());
        productResponse.setPrice(product.getPrice());
        productResponse.setPriceSale(product.getPriceSale());
        productResponse.setCategories(product.getCategories());

        Map<String, Integer> sizeQuantityMap = new HashMap<>();
        for (ProductSize productSize : product.getProductSizes()) {
            sizeQuantityMap.put(productSize.getSize().getName(), productSize.getQuantity());
        }
        productResponse.setSizeQuantityMap(sizeQuantityMap);
        productResponse.setComments(comments);
        List<Product> relatedProducts = productService.findRelatedProductsByCategory(product);
        productResponse.setRelatedTo(relatedProducts);
        return ResponseEntity.ok(productResponse);
    }*/

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductWithProductSizesById(@PathVariable Long productId) {
        ProductResponse productResponse = productService.getProductWithProductSizesById(productId);
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportProductsToExcel() {
        byte[] excelData = productService.exportProductsToExcel();

        // Cấu hình headers cho response
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=products.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelData);
    }

}