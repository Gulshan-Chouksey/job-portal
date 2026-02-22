package com.jobportal.job.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jobportal.common.exception.DuplicateResourceException;
import com.jobportal.common.exception.ResourceNotFoundException;
import com.jobportal.job.dto.CategoryRequestDTO;
import com.jobportal.job.dto.CategoryResponseDTO;
import com.jobportal.job.entity.Category;
import com.jobportal.job.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 1, 1, 12, 0);

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void shouldCreateCategorySuccessfully() {
        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setName("Backend");
        request.setDescription("Backend development jobs");

        Category saved = new Category();
        saved.setId(1L);
        saved.setName("Backend");
        saved.setDescription("Backend development jobs");
        saved.setCreatedAt(NOW);

        when(categoryRepository.existsByName("Backend")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        CategoryResponseDTO response = categoryService.createCategory(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Backend", response.getName());
        assertEquals("Backend development jobs", response.getDescription());

        verify(categoryRepository, times(1)).existsByName("Backend");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingDuplicateCategory() {
        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setName("Backend");
        request.setDescription("Backend development jobs");

        when(categoryRepository.existsByName("Backend")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> categoryService.createCategory(request)
        );

        assertEquals("Category already exists: Backend", exception.getMessage());

        verify(categoryRepository, times(1)).existsByName("Backend");
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void shouldReturnAllCategories() {
        Category cat1 = new Category();
        cat1.setId(1L);
        cat1.setName("Backend");
        cat1.setDescription("Backend jobs");
        cat1.setCreatedAt(NOW);

        Category cat2 = new Category();
        cat2.setId(2L);
        cat2.setName("Frontend");
        cat2.setDescription("Frontend jobs");
        cat2.setCreatedAt(NOW);

        when(categoryRepository.findAll()).thenReturn(List.of(cat1, cat2));

        List<CategoryResponseDTO> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        assertEquals("Backend", result.get(0).getName());
        assertEquals("Frontend", result.get(1).getName());

        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void shouldGetCategoryByIdSuccessfully() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Backend");
        category.setDescription("Backend jobs");
        category.setCreatedAt(NOW);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryResponseDTO response = categoryService.getCategoryById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Backend", response.getName());

        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenCategoryNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.getCategoryById(99L)
        );

        assertEquals("Category not found with id: 99", exception.getMessage());

        verify(categoryRepository, times(1)).findById(99L);
    }

    @Test
    void shouldUpdateCategorySuccessfully() {
        Category existing = new Category();
        existing.setId(1L);
        existing.setName("Backend");
        existing.setDescription("Old description");
        existing.setCreatedAt(NOW);

        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setName("Backend Updated");
        request.setDescription("New description");

        Category updated = new Category();
        updated.setId(1L);
        updated.setName("Backend Updated");
        updated.setDescription("New description");
        updated.setCreatedAt(NOW);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findByName("Backend Updated")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(updated);

        CategoryResponseDTO response = categoryService.updateCategory(1L, request);

        assertNotNull(response);
        assertEquals("Backend Updated", response.getName());
        assertEquals("New description", response.getDescription());

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingToExistingName() {
        Category existing = new Category();
        existing.setId(1L);
        existing.setName("Backend");
        existing.setDescription("Backend jobs");
        existing.setCreatedAt(NOW);

        Category duplicate = new Category();
        duplicate.setId(2L);
        duplicate.setName("Frontend");
        duplicate.setDescription("Frontend jobs");
        duplicate.setCreatedAt(NOW);

        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setName("Frontend");
        request.setDescription("Updated");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findByName("Frontend")).thenReturn(Optional.of(duplicate));

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> categoryService.updateCategory(1L, request)
        );

        assertEquals("Category name already exists: Frontend", exception.getMessage());

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void shouldAllowUpdatingCategoryWithSameName() {
        Category existing = new Category();
        existing.setId(1L);
        existing.setName("Backend");
        existing.setDescription("Old description");
        existing.setCreatedAt(NOW);

        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setName("Backend");
        request.setDescription("New description");

        Category updated = new Category();
        updated.setId(1L);
        updated.setName("Backend");
        updated.setDescription("New description");
        updated.setCreatedAt(NOW);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findByName("Backend")).thenReturn(Optional.of(existing));
        when(categoryRepository.save(any(Category.class))).thenReturn(updated);

        CategoryResponseDTO response = categoryService.updateCategory(1L, request);

        assertNotNull(response);
        assertEquals("New description", response.getDescription());

        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void shouldDeleteCategorySuccessfully() {
        when(categoryRepository.existsById(1L)).thenReturn(true);

        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1)).existsById(1L);
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentCategory() {
        when(categoryRepository.existsById(99L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.deleteCategory(99L)
        );

        assertEquals("Category not found with id: 99", exception.getMessage());

        verify(categoryRepository, times(1)).existsById(99L);
        verify(categoryRepository, never()).deleteById(99L);
    }

    @Test
    void shouldReturnEmptyListWhenNoCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of());

        List<CategoryResponseDTO> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentCategory() {
        CategoryRequestDTO request = new CategoryRequestDTO();
        request.setName("Test");
        request.setDescription("Test desc");

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.updateCategory(99L, request)
        );

        assertEquals("Category not found with id: 99", exception.getMessage());

        verify(categoryRepository, never()).save(any(Category.class));
    }
}
