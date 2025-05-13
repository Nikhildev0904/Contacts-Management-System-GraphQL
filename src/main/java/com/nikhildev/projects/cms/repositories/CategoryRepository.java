package com.nikhildev.projects.cms.repositories;

import com.nikhildev.projects.cms.models.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {

    List<Category> findByCategoryNameContainingIgnoreCase(String categoryName);

    Page<Category> findByIdIn(List<String> categoryIds, Pageable pageable);

    Page<Category> findByIdInAndCategoryNameContainingIgnoreCase(List<String> categoryIds, String categoryName, Pageable pageable);

    boolean existsByCategoryNameIgnoreCase(String categoryName);
}

