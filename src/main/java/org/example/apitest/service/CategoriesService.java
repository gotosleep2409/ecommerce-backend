package org.example.apitest.service;

import lombok.AllArgsConstructor;
import org.example.apitest.exception.ApiException;
import org.example.apitest.model.Category;
import org.example.apitest.model.request.CategoriesRequest;
import org.example.apitest.repository.CategoriesRepository;
import org.example.apitest.util.BeanUtilsAdvanced;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CategoriesService {
    private CategoriesRepository categoriesRepository;
    public Category createdCategory(CategoriesRequest categoriesRequest){
        Category categoriesToSave = new Category();
        BeanUtils.copyProperties(categoriesRequest, categoriesToSave);
        return categoriesRepository.save(categoriesToSave);
    }

    public Category updateApeCategory(Long id, CategoriesRequest categoriesToUpdate) throws ApiException {
        Optional<Category> apeCategoriesExisted = categoriesRepository.findById(id);
        if (!apeCategoriesExisted.isPresent()) {
            throw new ApiException("Not found with id=" + id);
        }
        Category category = apeCategoriesExisted.get();
        BeanUtilsAdvanced.copyProperties(categoriesToUpdate, category);
        return categoriesRepository.save(category);
    }

    public Page<Category> getPageCategories(int page, int size) {
        PageRequest paging = PageRequest.of(page -1, size, Sort.by(Sort.Direction.DESC, "id"));
        return categoriesRepository.findAll(paging);
    }

    public void deleteCategory(Long id) throws ApiException {
        Optional<Category> categoryExisted = categoriesRepository.findById(id);
        if (!categoryExisted.isPresent()) {
            throw new ApiException("Not found with id=" + id);
        }
        categoriesRepository.delete(categoryExisted.get());
    }

}
