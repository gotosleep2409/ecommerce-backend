package org.example.apitest.controller;

import lombok.RequiredArgsConstructor;
import org.example.apitest.exception.ApiException;
import org.example.apitest.helper.ResponseBuilder;
import org.example.apitest.model.Category;
import org.example.apitest.model.request.CategoriesRequest;
import org.example.apitest.service.CategoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategoriesController {
    private final CategoriesService categoriesService;

    @GetMapping("/list")
    public ResponseEntity<ResponseBuilder<Page<Category>>> getPageCategories(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "100") Integer size) {
        Page<Category> apeCategoriesPage = categoriesService.getPageCategories(page, size);
        return ResponseEntity.ok(ResponseBuilder.buildResponse(apeCategoriesPage, "Get list categories successfully", HttpStatus.OK));
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseBuilder<Category>> createCategory(@RequestBody CategoriesRequest categoriesRequest) {
        Category apeCategories = categoriesService.createdCategory(categoriesRequest);
        return ResponseEntity.ok(ResponseBuilder.buildResponse(apeCategories, "Create categories successfully", HttpStatus.OK));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseBuilder<Category>> updateApeCategory(
            @PathVariable(name = "id") Long id,
            @RequestBody CategoriesRequest categoriesToUpdate) throws ApiException {
        Category apeCategories = categoriesService.updateApeCategory(id, categoriesToUpdate);
        return ResponseEntity.ok(ResponseBuilder.buildResponse(apeCategories, "Update categories successfully", HttpStatus.OK));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseBuilder<Boolean>> deleteCategory(@PathVariable(name = "id") Long id) throws ApiException {
        categoriesService.deleteCategory(id);
        return ResponseEntity.ok(ResponseBuilder.buildResponse(true, "Delete category successfully", HttpStatus.OK));
    }
}
