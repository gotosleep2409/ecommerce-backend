package org.example.apitest.service;

import lombok.AllArgsConstructor;
import org.example.apitest.exception.ApiException;
import org.example.apitest.model.Category;
import org.example.apitest.model.Product;
import org.example.apitest.model.request.ProductRequest;
import org.example.apitest.repository.CategoriesRepository;
import org.example.apitest.repository.ProductRepository;
import org.example.apitest.util.BeanUtilsAdvanced;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService {
    private final CategoriesRepository categoriesRepository;

    private final ProductRepository productRepository;

    public Page<Product> getPageProduct(int page, int size) {
        PageRequest paging = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        return productRepository.findAll(paging);
    }

    public Product createProduct(ProductRequest request) throws ApiException {
        List<Category> categories = categoriesRepository.getApeCategoriesByIdIn(request.getCategories());
        if (categories.isEmpty()) {
            throw new ApiException("Not found apeCategories");
        }
        Product productToCreate = new Product(request.getName(),request.getCreator() , request.getDescription(),request.getDetail(), request.getImageUrl(), request.getPrice(), request.getQuantity(), request.getPriceSale(), categories);
        return productRepository.save(productToCreate);
    }

    public Product updateProduct(Long id, ProductRequest request) throws ApiException {
        Optional<Product> productExisted = productRepository.findById(id);

        if (!productExisted.isPresent()) {
            throw new ApiException("Not found with id=" + id);
        }

        Product productToUpdate = productExisted.get();
        List<Category> categories = categoriesRepository.getApeCategoriesByIdIn(request.getCategories());
        if (categories.size() == 0) {
            throw new ApiException("Not found apeCategories with apeCategoryIds body request");
        }
        Product productRequest = new Product(request.getName(),request.getCreator() , request.getDescription(),request.getDetail(), request.getImageUrl(), request.getPrice(), request.getQuantity(), request.getPriceSale(), categories);
        BeanUtilsAdvanced.copyProperties(productRequest, productToUpdate);
        return productRepository.save(productToUpdate);
    }

    public void deleteProduct(Long id) throws ApiException {
        Optional<Product> productExisted = productRepository.findById(id);
        if (!productExisted.isPresent()) {
            throw new ApiException("Not found with id=" + id);
        }
        productRepository.delete(productExisted.get());

    }

}
