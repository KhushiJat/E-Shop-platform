package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImplementation implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        // CHANGE THIS: Only fetch categories where parentCategory is null (Root Categories)
        Page<Category> categoryPage = categoryRepository.findByParentCategoryIsNull(pageDetails);

        List<Category> categories = categoryPage.getContent();
        if(categories.isEmpty())
            throw new APIException("No category created till now.");

        List<CategoryDTO> categoryDTOS = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElement(categoryPage.getTotalElements());
        categoryResponse.setTotalpages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category categoryFromDb = categoryRepository.findByCategoryName(category.getCategoryName());
        if(categoryFromDb != null){
            throw new APIException("Category with the name " + category.getCategoryName() + " already exists !!!");
        }

        // Handle Parent Category assignment if parentId is present
        if(categoryDTO.getParentId() != null) {
            Category parentCategory = categoryRepository.findById(categoryDTO.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryDTO.getParentId()));
            category.setParentCategory(parentCategory);
        }

        Category savedCategory = categoryRepository.save(category);
        CategoryDTO savedCategoryDTO = modelMapper.map(savedCategory, CategoryDTO.class);
        if (savedCategory.getParentCategory() != null) {
            savedCategoryDTO.setParentId(savedCategory.getParentCategory().getCategoryId());
        }
        return savedCategoryDTO;
    }

    @Override
    public CategoryDTO deleteCategory(Long CategoryId){
        Category category = categoryRepository.findById(CategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", CategoryId));

        categoryRepository.delete(category);
        CategoryDTO deletedDTO = modelMapper.map(category, CategoryDTO.class);
        if (category.getParentCategory() != null) {
            deletedDTO.setParentId(category.getParentCategory().getCategoryId());
        }
        return deletedDTO;
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId){
        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        savedCategory.setCategoryName(categoryDTO.getCategoryName());

        // Handle parent update if provided
        if(categoryDTO.getParentId() != null) {
            Category parentCategory = categoryRepository.findById(categoryDTO.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryDTO.getParentId()));
            savedCategory.setParentCategory(parentCategory);
        } else {
            savedCategory.setParentCategory(null);
        }

        Category updatedCategory = categoryRepository.save(savedCategory);
        CategoryDTO updatedDTO = modelMapper.map(updatedCategory, CategoryDTO.class);
        if (updatedCategory.getParentCategory() != null) {
            updatedDTO.setParentId(updatedCategory.getParentCategory().getCategoryId());
        }
        return updatedDTO;
    }
}