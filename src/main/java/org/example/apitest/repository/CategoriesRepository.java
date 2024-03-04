package org.example.apitest.repository;

import org.example.apitest.model.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoriesRepository extends PagingAndSortingRepository<Category, Long>, CrudRepository<Category, Long> {
    List<Category> getApeCategoriesByIdIn(List<Long> categoryIds);
}
