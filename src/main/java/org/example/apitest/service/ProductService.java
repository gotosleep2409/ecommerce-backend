package org.example.apitest.service;

import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.apitest.exception.ApiException;
import org.example.apitest.model.*;
import org.example.apitest.model.request.ProductRequest;
import org.example.apitest.model.response.ProductResponse;
import org.example.apitest.repository.*;
import org.example.apitest.util.BeanUtilsAdvanced;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductService {
    private final CategoriesRepository categoriesRepository;

    private final ProductRepository productRepository;

    private final ProductSizeRepository productSizeRepository;

    private final SizeRepository sizeRepository;

    private final CommentsRepository commentsRepository;

    public Page<Product> getPageProduct(int page, int size, Long categoryId) {
        Pageable paging = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        if (categoryId != null) {
            return productRepository.findByCategoriesId(categoryId, paging);
        }
        return productRepository.findAll(paging);
    }

    public Product createProduct(ProductRequest request, Map<String, Integer> sizeQuantityMap) throws ApiException {
        List<Category> categories = categoriesRepository.getApeCategoriesByIdIn(request.getCategories());
        if (categories.isEmpty()) {
            throw new ApiException("Not found Categories");
        }

        Product productToCreate = new Product(request.getName(), request.getCreator(), request.getDescription(), request.getDetail(), request.getImageUrl(), request.getPrice(), request.getPriceSale(), categories);

        Product savedProduct = productRepository.save(productToCreate);

        sizeQuantityMap.forEach((sizeName, quantity) -> {
            Size size = sizeRepository.getSizeByName(sizeName);

            ProductSize productSize = new ProductSize();
            productSize.setProduct(savedProduct);
            productSize.setSize(size);
            productSize.setQuantity(quantity);

            productSizeRepository.save(productSize);
        });

        return savedProduct;
    }

    public Product updateProduct(Long id, ProductRequest request) throws ApiException {
        Optional<Product> productExisted = productRepository.findById(id);

        if (!productExisted.isPresent()) {
            throw new ApiException("Not found with id=" + id);
        }

        List<Category> categories = categoriesRepository.getApeCategoriesByIdIn(request.getCategories());
        if (categories.size() == 0) {
            throw new ApiException("Not found Categories with CategoryIds body request");
        }

        Product productRequest = new Product(request.getName(), request.getCreator(), request.getDescription(), request.getDetail(), request.getImageUrl(), request.getPrice(), request.getPriceSale(), categories);

        Product productToUpdate = productExisted.get();

        BeanUtilsAdvanced.copyProperties(productRequest, productToUpdate);

        productToUpdate.setCategories(categories);

        for (Map.Entry<String, Integer> entry : request.getSizeQuantityMap().entrySet()) {
            String sizeName = entry.getKey();
            Integer quantity = entry.getValue();

            ProductSize productSize = productSizeRepository.findByProductIdAndSizeName(id, sizeName);
            if (productSize != null) {
                productSize.setQuantity(quantity);
                productSize.setProduct(productToUpdate);
            } else {
                Size size = sizeRepository.getSizeByName(sizeName);
                if (size != null) {
                    productSize = new ProductSize();
                    productSize.setProduct(productToUpdate);
                    productSize.setSize(size);
                    productSize.setQuantity(quantity);
                    productToUpdate.getProductSizes().add(productSize);
                } else {
                    throw new ApiException("Size not found: " + sizeName);
                }
            }
        }
        return productRepository.save(productToUpdate);
    }

    public void deleteProduct(Long id) throws ApiException {
        Optional<Product> productExisted = productRepository.findById(id);
        if (!productExisted.isPresent()) {
            throw new ApiException("Not found with id=" + id);
        }
        productRepository.delete(productExisted.get());
    }

    public Page<Product> getProductByCategoryId(long id, int page, int size) throws ApiException {
        Optional<Category> categoryOptional = categoriesRepository.findById(id);
        PageRequest paging = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        if (!categoryOptional.isPresent()) {
            throw new ApiException("Category not found");
        }
        return productRepository.findByCategories(categoryOptional, paging);
    }

    public Product findProductWithProductSizesById(Long productId) {
        return productRepository.findProductWithProductSizesById(productId);
    }

    public ProductResponse getProductWithProductSizesById(Long productId) {
        List<Comments> comments = commentsRepository.findCommentByProductId(productId);
        Product product = findProductWithProductSizesById(productId);

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

        List<Product> relatedProducts = findRelatedProductsByCategory(product);
        productResponse.setRelatedTo(relatedProducts);

        return productResponse;
    }

    public byte[] exportProductsToExcel() {
        List<Product> productList = new ArrayList<>();
        productRepository.findAll().forEach(productList::add);

        try (Workbook workbook = new XSSFWorkbook()) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Sheet sheet = workbook.createSheet("Products");

            // Tạo dòng tiêu đề
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Product Name", "Size", "Remaining Quantity"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowNum = 1;
            for (Product product : productList) {
                for (ProductSize productSize : product.getProductSizes()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(product.getId());
                    row.createCell(1).setCellValue(product.getName());
                    row.createCell(2).setCellValue(productSize.getSize().getName());
                    row.createCell(3).setCellValue(productSize.getQuantity());
                }
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while exporting data to Excel");
        }
    }

    public List<Product> findRelatedProductsByCategory(Product product) {
        Category category = product.getCategories().get(0);
        Pageable pageable = PageRequest.of(0, 4);
        List<Product> products = productRepository.findByCategoryIdAndNotId(category.getId(), product.getId(), pageable);

        return products.stream()
                .map(p -> new Product(p.getId(), p.getCreator(), p.getDescription(), p.getDetail(), p.getName(), p.getImageUrl(), p.getPrice(), p.getPriceSale(), p.getCategories()))
                .collect(Collectors.toList());
    }
}
