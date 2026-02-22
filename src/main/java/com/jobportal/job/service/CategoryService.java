package com.jobportal.job.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jobportal.common.exception.DuplicateResourceException;
import com.jobportal.common.exception.ResourceNotFoundException;
import com.jobportal.job.dto.CategoryRequestDTO;
import com.jobportal.job.dto.CategoryResponseDTO;
import com.jobportal.job.entity.Category;
import com.jobportal.job.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponseDTO createCategory(CategoryRequestDTO request) {
        log.info("Creating category: {}", request.getName());

        if (categoryRepository.existsByName(request.getName())) {
            log.warn("Category already exists: {}", request.getName());
            throw new DuplicateResourceException("Category already exists: " + request.getName());
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category saved = categoryRepository.save(category);
        log.info("Category created successfully with id: {}", saved.getId());

        return mapToResponse(saved);
    }

    public List<CategoryResponseDTO> getAllCategories() {
        log.info("Fetching all categories");
        return categoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CategoryResponseDTO getCategoryById(Long id) {
        log.info("Fetching category with id: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Category not found with id: {}", id);
                    return new ResourceNotFoundException("Category not found with id: " + id);
                });

        return mapToResponse(category);
    }

    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO request) {
        log.info("Updating category with id: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Category not found with id: {}", id);
                    return new ResourceNotFoundException("Category not found with id: " + id);
                });

        // Check for duplicate name (excluding current category)
        categoryRepository.findByName(request.getName())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        log.warn("Category name already exists: {}", request.getName());
                        throw new DuplicateResourceException("Category name already exists: " + request.getName());
                    }
                });

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category updated = categoryRepository.save(category);
        log.info("Category updated successfully with id: {}", updated.getId());

        return mapToResponse(updated);
    }

    public void deleteCategory(Long id) {
        log.info("Deleting category with id: {}", id);

        if (!categoryRepository.existsById(id)) {
            log.warn("Category not found with id: {}", id);
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }

        categoryRepository.deleteById(id);
        log.info("Category deleted successfully with id: {}", id);
    }

    private CategoryResponseDTO mapToResponse(Category category) {
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .build();
    }
}
